package com.didan.archetype.service.impl;

import com.didan.archetype.config.properties.AuthConfigProperties;
import com.didan.archetype.config.properties.AuthConfigProperties.Type;
import com.didan.archetype.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
    value = "{app.auth.enabled}",
    havingValue = "true" // Chỉ định rằng dịch vụ này sẽ được kích hoạt khi thuộc tính app.auth.enabled có giá trị true
)
@Slf4j
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
      this.jwtParser = (JwtParser) Jwts.parser().verifyWith(getPublicKey(publicKey)); // Tạo một JwtParser mới bằng cách sử dụng publicKey đã được mã hóa
      this.curKey = publicKey; // Cập nhật khóa hiện tại
      return this.jwtParser; // Trả về jwtParser mới
    }
  }

  public PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException { // Hàm này dùng để lấy PublicKey từ chuỗi publicKey
    KeyFactory factory = KeyFactory.getInstance("RSA"); // Tạo một KeyFactory với thuật toán RSA
    X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)); // Mã hóa publicKey thành định dạng X509EncodedKeySpec
    return factory.generatePublic(encodedKeySpec); // Tạo PublicKey từ X509EncodedKeySpec
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
  public Claims getClaimsNotVerifyToken(String token) { // Phương thức này dùng để lấy Claims từ token mà không cần xác thực token
    return Jwts.parser().build().parseUnsecuredClaims(this.removeSignatureFromToken(this.resolveToken(token))).getPayload();
  }

  private String removeSignatureFromToken(String token) { // Phương thức này dùng để loại bỏ chữ ký khỏi token
    String[] splitToken = token.split("\\."); // Tách token thành các phần bằng dấu chấm
    return splitToken[0] + "." + splitToken[1] + "."; // Trả về token đã được loại bỏ chữ ký
  }
}
