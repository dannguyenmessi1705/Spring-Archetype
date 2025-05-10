package com.didan.archetype.service.impl;

import com.didan.archetype.config.properties.AuthConfigProperties;
import com.didan.archetype.config.properties.AuthConfigProperties.Type;
import com.didan.archetype.service.AuthService;
import com.didan.archetype.utils.UtilsCommon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(
    value = {"app.auth.enabled"}, // Điều kiện để kích hoạt cấu hình này
    havingValue = "true" // Giá trị cần có để kích hoạt
)
public class AuthServiceImpl implements AuthService {

  final AuthConfigProperties authConfigProperties;
  private String curKey;
  private JwtParser jwtParser;

  public AuthServiceImpl(AuthConfigProperties authConfigProperties) {
    this.authConfigProperties = authConfigProperties;
  }

  private JwtParser getJwtParser(String publicKey)
      throws InvalidKeySpecException, NoSuchAlgorithmException { // Hàm này dùng để lấy JwtParser. Ví d dụ: nếu publicKey đã được sử dụng trước đó, nó sẽ trả về JwtParser đã được tạo ra trước đó. Nếu không, nó sẽ tạo một JwtParser mới
    if (publicKey.equals(this.curKey) && this.jwtParser != null) { // Kiểm tra xem publicKey có giống với khóa hiện tại hay không và jwtParser có khác null hay không
      return this.jwtParser; // Nếu giống, trả về jwtParser hiện tại
    } else { // Nếu không giống hoặc jwtParser là null
      this.jwtParser = Jwts.parser().verifyWith(getPublicKey(publicKey)).build(); // Tạo một JwtParser mới bằng cách sử dụng publicKey đã được mã hóa
      this.curKey = publicKey; // Cập nhật khóa hiện tại
      return this.jwtParser; // Trả về jwtParser mới
    }
  }

  public PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException { // Hàm này dùng để lấy PublicKey từ chuỗi publicKey
    KeyFactory factory = KeyFactory.getInstance("RSA"); // Tạo một KeyFactory với thuật toán RSA
    X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)); // Mã hóa publicKey thành định dạng X509EncodedKeySpec
    return factory.generatePublic(encodedKeySpec); // Tạo PublicKey từ X509EncodedKeySpec
  }

  public PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException { // Hàm này dùng để lấy PrivateKey từ chuỗi privateKey
    KeyFactory factory = KeyFactory.getInstance("RSA"); // Tạo một KeyFactory với thuật toán RSA
    PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)); // Mã hóa privateKey thành định dạng PKCS8EncodedKeySpec
    return factory.generatePrivate(encodedKeySpec); // Tạo PrivateKey từ PKCS8EncodedKeySpec
  }

  @Override
  public boolean verifyToken(String token) {
    try {
      return this.verify(token); // Gọi hàm verify để xác thực token
    } catch (Exception e) {
      log.info(e.getMessage(), e);
      return false;
    }
  }

  private boolean verify(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (Objects.requireNonNull(authConfigProperties.getType()) == Type.KEY) {
      token = resolveToken(token); // Tách token từ chuỗi Bearer
      this.getJwtParser(this.authConfigProperties.getKey().getPublicKey()).parseSignedClaims(token); // Phân tích token đã ký bằng publicKey
      return true; // Nếu phân tích thành công, trả về true
    }
    return false; // Nếu không phải là loại KEY, trả về false
  }

  public String resolveToken(String bearerToken) {
    return bearerToken != null && bearerToken.startsWith("Bearer") ? bearerToken.replace("Bearer", "").trim() : bearerToken;
  }


  @Override
  public boolean verifyTokenWReason(String token)
      throws InvalidKeySpecException, NoSuchAlgorithmException { // Phương thức này dùng để xác thực token và trả về true nếu xác thực thành công, ngược lại sẽ ném ra ngoại lệ
    return this.verify(token); // Gọi hàm verify để xác thực token
  }

  @Override
  public Claims getClaims(String token) { // Phương thức này dùng để lấy Claims từ token
    try {
      return this.getJwtParser(this.authConfigProperties.getKey().getPublicKey()).parseSignedClaims(this.resolveToken(token)).getPayload(); // Phân tích token đã ký và lấy Claims từ payload
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  public Claims getClaimsThrows(String token) // Phương thức này dùng để lấy Claims từ token và ném ra ngoại lệ nếu có lỗi xảy ra
      throws InvalidKeySpecException, NoSuchAlgorithmException {
    return this.getJwtParser(this.authConfigProperties.getKey().getPublicKey()).parseSignedClaims(this.resolveToken(token)).getPayload();
  }

  @Override
  public Claims getClaimsNotVerifyToken(String token) throws JsonProcessingException { // Phương thức này dùng để lấy Claims từ token mà không cần xác thực token
    return new ObjectMapper().readValue(removeSignatureFromToken(token), DefaultClaims.class);
  }

  @Override
  public String signToken(Map<String, Object> claims, String subject, String issuer, long seconds) throws NoSuchAlgorithmException, InvalidKeySpecException {
    PrivateKey privateKey = this.getPrivateKey(this.authConfigProperties.getKey().getPrivateKey()); // Lấy PrivateKey từ chuỗi privateKey
    Instant instantNow = Instant.now(); // Lấy thời gian hiện tại
    Date now = Date.from(instantNow);
    Date expiration = Date.from(instantNow.plusSeconds(seconds)); // Tính toán thời gian hết hạn của token
    return Jwts.builder()
        .issuer(issuer)
        .subject(subject) // Thiết lập subject
        .claims(claims)
        .issuedAt(now)
        .expiration(expiration) // Thiết lập thời gian hết hạn
        .id(UtilsCommon.createNewUUID())
        .signWith(privateKey) // Ký token bằng PrivateKey
        .compact(); // Trả về token đã ký
  }

  private String removeSignatureFromToken(String token) { // Phương thức này dùng để loại bỏ chữ ký khỏi token
    String[] splitToken = token.split("\\."); // Tách token thành các phần bằng dấu chấm
    return new String(Base64.getUrlDecoder().decode(splitToken[1]), StandardCharsets.UTF_8); // Trả về token đã được loại bỏ chữ ký
  }
}
