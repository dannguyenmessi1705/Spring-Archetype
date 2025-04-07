# Java Core archetype Client
## Overview :
- Đây là project Demo cho việc tích hợp java Core archetype lib vào dự án.
### Lưu ý :
-  Không bắt buộc phải Clone lại dự án này để phát triển, các team có thể tạo project spring khác và tích hợp vào
## 1 số tác dụng chính của Java Core archetype Lib
 Công dụng | Des
 --- | --- 
 Ít code trong project | Các project thật sự sử dụng dự án sẽ có ít code hơn.
 Không thể sửa | Một số tính năng là bắt buộc và yêu cầu phải sử dụng. Các app sử dụng sẽ không thể sửa
 Dễ dàng cập nhật | 	Nếu sử dụng archetype cũ, để cập nhật một tính năng common cho tất cả project. Chúng ta cần sửa lại tất cả project. Với archetype chúng ta chỉ cần nâng version archetype lib và yêu cầu các project nâng version 
 Dễ tích hợp | 	Việc tích hợp archetype sẽ dễ dàng hơn.
 Không có những thứ không cần | Ở archetype cũ, nếu không sử dụng tính năng gì chúng ta sẽ cần xóa code, xóa dependency. Với archetype lib chỉ cần exclusions trong dependency
 ... | ...
 ## Các branch demo
 
  Branch | Nội dung Demo
  --- | --- 
  Master | Bao gồm demo full tính năng.
  emty-config | Dự án rỗng, không sử dụng tính năng gì cả. Chỉ khai báo archetype nhưng không sài cache, vault kafka......
  Vault-config | Branch demo và hướng dẫn config Vault và sử dụng Vault
  Kafka-config | Branch demo và hướng dẫn config Kafka và sử dụng Kafka
  ... | ...
#Require structural  client
```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │          └── didan
│   │   │             └── ...
│   │   │                 └── name-client
│   │   │                     ├── annotation => NƠi chứa các annotation.   ( Sử dụng thì thêm )
│   │   │                     ├── aop => Các Aspect Oriented Programming rules  ( Sử dụng thì thêm )
│   │   │                     ├── config => Quản lý cấu hình, các properties. ( Sử dụng thì thêm )
│   │   │                     │   ├── database : chứa các config database
│   │   │                     ├── constant => Constant. VD: Mã lỗi, Header...  ( Sử dụng thì thêm )
│   │   │                     ├── ├────── ResponseStatusCodeEnumClient extends ResponseStatusCodeEnum => Class constant Response trả về cho client  ( Sử dụng thì thêm )
│   │   │                     ├── controller => Config base. Hiện đang có base resful ( Sử dụng thì thêm )
│   │   │                     │   ├── restful : Chứa các controller restful ( Sử dụng thì thêm )
│   │   │                     │   ├── kafka : chưa các consumer và producer kafka ( Sử dụng thì thêm )
│   │   │                     ├── dto => Nơi đặt các DTO ( Sử dụng thì thêm, hoặc có thể tách phần này ra một module khác )
│   │   │                     ├── exception => Danh sách các Exception muốn custom thêm nếu có  ( Sử dụng thì thêm )
│   │   │                     ├── filter => Các filter trigger khi có request gửi tới hoặc khi response trả về  ( Sử dụng thì thêm )
|   |   |                     ├── health => Healthcheck service indicator  ( Sử dụng thì thêm )
│   │   │                     ├── metrics => Chứa các config metrics cho theo dõi ứng dụng
│   │   │                     ├── repository => Tầng gọi đến databse ( Sử dụng thì thêm )
│   │   │                     ├── scheduling => Nơi đặt các task hẹn giờ chạy
│   │   │                     ├── service => Service nên để là một intefcace và được impl ở trong sub folder impl ( Sử dụng thì thêm )
│   │   │                     │   ├── impl : Chứa các class impl các inteface service. Các Class này sẽ impl code logic cho các method trừu tượng 
│   │   │                     ├── util => Lớp helper, cung cấp các hàm util, chú ý. Sử dụng Spring Utils library thay vì tự viết các hàm util: VD. xử lý String v..v  ( nếu cảm nhận thấy là common thì reqeust  để thêm vào archetype )
│   │   │                     ├── validator => Custom  validator  ( nếu cảm nhận thấy là common thì reqeust  để thêm vào archetype )
│   │   │                     │   ├── annotation => Annotation của validator
│   │   │                     │   ├── validator => Logic sử lý của validator tương ứng với annotation
│   │   │                     └── ... sẽ cập nhật thêm sau.
│   │   └── resources
│   │       └── application.properties 
│   │       └── bootstrap.properties 
│   │       └── .....
│   └── test
│       ├── java
│       └── resources

```
# Các thuộc tính bắt buộc 
  Tên thuộc tính | Data Type | Ví dụ | Mô tả
  --- | --- | --- | --- 
  app.application-context-name| string | arch-client | sử dụng để định nghĩa context name cho project
  app.application-short-name| string | arch-ct | Giống app.application-context-name nhưng nên là một short name
  info.build.artifact| string | @project.artifactId@ | sử dụng cho api get info
  info.build.name| string |@project.name@| sử dụng cho api get info
  info.build.description| string |@project.description@| sử dụng cho api get info
  info.build.version| string |@project.version@| sử dụng cho api get info

# Hướng dẫn build app thành archetype ( Dành cho máy không kết nối repo công ty hoặc cho người deploy )

- 1 : Tại thư mục gốc của dự án. Xóa 1 số file config của iDEA đi ( .idea, *.iml... )
- 2 : Chạy lệnh `mvn archetype:create-from-project -Darchetype.properties=archetype.properties`
- 3 : Vào floder `target/generated-sources/archetype`
- 4 : Chạy lệnh `mvn clean install`


# Info archetype
 Field | Value
 --- | --- 
 GroupId | com.dan....
 ArtifactId | didan-archetype
 Version | 	1.0.0
 ## Sử dụng để tạo mới project từ arch:
Việc sử dụng tùy thuộc vào IDE đang sử dụng.

- NetBeans: 
	
	- Sau khi thực hiện xong bước Cài đặt. Khởi động netbeans. đợi netbean index lại toàn bộ repository
	- Chọn New Project/Maven/Project from Archtype
	- Chọn tên Archtype tương ứng. 
	- Điền thông tin và next đến cuối

- IntelliJ:

	- Tạo project Maven, click vào `create from archtype`.
	- Chọn archtype tương ứng.
	- Điền thông tin, next đến cuối

- Không dùng IDE:

```bash
mvn archetype:generate                                  \
  -DarchetypeGroupId=<archetype-groupId>                \
  -DarchetypeArtifactId=<archetype-artifactId>          \
  -DarchetypeVersion=<archetype-version>                \
  -DgroupId=<my.groupid>                                \
  -DartifactId=<my-artifactId>
```
[Video hướng dẫn](doc/Video_DEMo_Using_Archetype.mp4)


# Hướng dẫn fix một số lỗi khi deploy
 Error | Fix
 --- | --- 
 Lỗi check health 404  | Liên hệ với đội deploy kiểm tra file ```bootstrap.properties```  xem có đang để ```management.endpoints.web.exposure.include``` hay không. Nếu có yêu cầu xóa bỏ để mặc định
 Lỗi log thành nhiều dòng -> log trên sv không ra json | Kiểm tra các thư viện thêm mới và exclusions các thư viện log đi ( ví dụ ở spring-boot-starter-test)


# Documentation
# Default config
- Port Http mặc đình là 8080 (không thay đổi)
- actuator `info,health,prometheus,refresh,bus-refresh` được mở tự động bởi lib, nếu không cần sửa thì không nên ghi đè
## Config sử dụng arch-lib
- Để sử dụng arch-lin sau khi bạn thêm dependency thì tại Main start app ứng dụng spring của bạn sẽ cần khai báo annotation để Enable Archetype
- Sử dụng Annotation ```@EnableArchetype```
```java
@SpringBootApplication(
        exclude = KafkaAutoConfiguration.class
        , scanBasePackages = {"com.didan..."}// Thư mục packet dự án của bạn
)
@EnableArchetype // Khai báo sử dụng Lib Archetype
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
```
## Controller Definition
- Lib tự động config swagger với openapi và handler exception và hỗ trợ validate cho chúng ta.
- Tại Controller chúng ta sẽ cần khai báo như sau
```java
@RestController
@RequestMapping("/arch-client/v1/api")
@Tag(name = "Validate", description = "Validate")
@Validated
public class ValidateController extends BaseController {

}
```
## Validate dữ liệu gửi từ client thông qua http
### Validate tại body
- Lib sử dụng validate của spring tích hợp với các annotation valdiate của javax
- Tại DTO request sẽ cần khai báo các loại validate Xem thêm tại (https://docs.oracle.com/javaee/7/api/javax/validation/constraints/package-summary.html)
```java
@Data
public class PostUserRequestDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "firstName là bắt buộc")
    @NotEmpty(message = "firstName là bắt buộc")
    private String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "lastName là bắt buộc")
    @NotEmpty(message = "lastName là bắt buộc")

    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "career là bắt buộc")
    @NotEmpty(message = "career là bắt buộc")
    private String career;
}
```
- Tiếp theo tại Controller chúng ta sẽ khai báo ``` @Valid @RequestBody() ``` để sử dụng tính năng validate
```java
    @PostMapping(value = "/user", produces = "application/json", consumes = "application/json")
    @Transactional
    public ResponseEntity<GeneralResponse<UserEntity>> postUser(@Valid @RequestBody() PostUserRequestDto postUserDto) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(postUserDto, userEntity);
        userEntity = userRepo.save(userEntity);
        return responseFactory.success(userEntity);
    }
```
### Validate tại header,param
- Sử dụng valdate ngay tại trong header nơi khai báo controller.
- Sử dụng các annotation validate của javax ngay trên method của controller
```java
 @PostMapping(value = "/update")
    public ResponseEntity<GeneralResponse<String>> update(
            @RequestParam(value = "id", required = false)
            @NotNull(message = "id trong RequestParam không thể null")
                    String id,
            @RequestParam(value = "number", required = false)
            @NotNull(message = "Number trong RequestParam không thể null")
            @NotNumber(message = "Number phải là chữ số")
                    String number

    ) {
        return responseFactory.success("ok");
    }
```
### Handling Exception
- Constant status code sẽ được để trong ``ResponseStatusCodeEnum`` Client có thể extend để mở rộng thêm
```java
@Slf4j
@UtilityClass
public class ResponseStatusCodeEnumClient implements ResponseStatusCodeEnum {
    public final ResponseStatusCode DATA_NOT_FOUND = ResponseStatusCode.builder().code("01").httpCode(200).build();
    public final ResponseStatusCode TOKEN_ERROR = ResponseStatusCode.builder().code("02").httpCode(200).build();
    public final ResponseStatusCode USER_EXIST = ResponseStatusCode.builder().code("03").httpCode(400).build();
    public final ResponseStatusCode USER_ID_NOT_FOUND = ResponseStatusCode.builder().code("04").httpCode(400).build();

}
```
- Trong đó `code` là mã lỗi trả về, nó sẽ lấy msg tương ứng trên `messages.properties`, httpCode là http status sẽ trả về client
- Trong lib có sử dụng  ``` @ControllerAdvice ``` để sử dụng.
- Client có thể sử dụng `BaseResponseException` để trả về một lỗi
```java
    @GetMapping(value = "/demoAuthVerify")
    public ResponseEntity<GeneralResponse<Claims>> getRestDemo(String token) {
        Claims claims = authService.getClaims(token);
        if (Objects.nonNull(claims)) {
            return responseFactory.success(claims);
        }
        throw new BaseResponseException(ResponseStatusCodeEnumClient.TOKEN_ERROR );
    }
```
### Gen Response for client
- Lib có tạo 1 class có chứa sẵn các method sử dụng để tạo response trả về cho client.
- Response mặc định sẽ là object type như sau ``ResponseEntity<GeneralResponse<T>>``
```java
    @Autowired
    ResponseFactory responseFactory;
```
- Với dữ liệu thành công. Status mặc định sẽ là 200
```
  @GetMapping(value = "/demo")
    public ResponseEntity<GeneralResponse<Integer>> getData() {
        return responseFactory.success(1);
    }
```
### Response error với dynamic msg
- Để sử dụng tính năng này, tại ``messages.properties`` Cần khai báo theo format sau
- Thêm 2 ký tự ``%%`` tại trước và sau name của msg muốn dynamic
- Ví dụ :
```properties
02 = Token error %%Error%%
```
- Khai báo constant Error mapping với id error
```java
@Slf4j
@UtilityClass
public class ResponseStatusCodeEnumClient implements ResponseStatusCodeEnum {
    public final ResponseStatusCode TOKEN_ERROR = ResponseStatusCode.builder().code("02").httpCode(200).build();
}
 ```
- Khi trả về error chúng ta sẽ trả về như sau
```java
   Map<String, String> errorMsg = new HashMap<>();
   errorMsg.put("Error", "vui long check lai");
   throw new BaseResponseException(null, ResponseStatusCodeEnumClient.TOKEN_ERROR,errorMsg );
```
- Kết quả sẽ là
```
Token error vui long check lai
```
####  dynamic msg với database
- Các msg chúng ta config trong file `messages.properties` hệ thống hỗ trợ custom lại, không nhất thiết phải load từ file `messages.properties`
- Để sử dụng tính năng này, chúng ta cần impl interface `ErrorService` và tạo nó thành 1 Bean của Spring
```java
@Service
public class ErrorServiceImpl implements ErrorService {
  @Override
  @SuppressWarnings("all")
  public String getErrorDetail(String errorCode, String language) {
    // sử dụng nếu bạn muốn sử lý một error với một msg được lấy từ ở bên ngoài, ví dụ database...etc..
    switch (language) {
      case "vi": {
        switch (errorCode) {
          case "00": {
            return "Thành công";
          }
          default: {
            return null;
          }
        }
      }
      default: {
        switch (errorCode) {
          case "00": {
            return "success";
          }
          default: {
            return null;
          }
        }
      }
    }
  }
}
```
- Trong đó `language` là mã ngôn ngữ,  `errorCode` là error code tương ứng với `00` hoặc `02` như ví dụ cũ ở trên
- Lưu ý, mặc định nếu load lỗi hoặc không tìm thấy msg thì nên trả về null, hệ thống sẽ tìm kiếm dữ liệu trong `messages.properties` nếu không thấy sẽ lấy thẳng mã lỗi để trả về
### Sử dụng Spring cache
- Lib arch hỗ trợ config sẵn 2 loại cache là cache local và cache trên redis.
- Để config chúng ta thêm config vào `application.properties`
```properties
app.cache.memory.enable=true # True Nếu muôn sử dụng cache local
app.cache.redis.enable=true # True nếu muốn sử dụng cache trên redis
app.cache.redis.nodes=localhost:6379,localhost:6380,localhost:6381,localhost:6382,localhost:6383,localhost:6384
app.cache.redis.password=12345678
```
- Tại main start add Annotation `@EnableCaching` để nói vs spring chúng ta sử dụng cache
```java

@SpringBootApplication(
        exclude = KafkaAutoConfiguration.class
        , scanBasePackages = {"com.didan..."} // Thư mục packet dự án của bạn
)
@EnableCaching// Nói vs spring chúng ta sử dụng cache
@EnableArchetype 
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
```
- Tại nơi chúng ta muốn cache dữ liệu
```java
@CacheConfig(cacheNames = {"CacheTestService"}, cacheManager = "redisCacheManager")
public interface CacheTestService {
    @Cacheable(value = "getNamePram",sync = true,key ="#name")
    public String getName(String name);
    @Cacheable(value = "getName")
    public String getName();
}
```
- Trong đó `cacheManager` Là loại cache. Default sẽ có 2 loại
	- `redisCacheManager` và `localCacheManager` nếu cần chúng ta có thể tự tạo thêm
- `cacheNames` là tên global của cache này
- `Cacheable` trên từng method là nơi chung ta khai báo sub cache để cache dữ liệu
- Đọc thêm về Spring cache ở đây (https://www.baeldung.com/spring-cache-tutorial)

### Hot Reload config
- Hệ thống đang có tính năng hot reload 1 số config tại thời điểm runtime
- Để làm điều này tại class khai báo thêm annotation ``
```java
@Configuration

@Slf4j
public class TestHotReload {
    
}
```

### Sử dụng `Scheduled` trong spring (https://spring.io/guides/gs/scheduling-tasks/)
- Lib đang hỗ trợ `Scheduled` của Spring, để sử dụng, chúng ta cần Enable tại main start app
```java
@SpringBootApplication(
        exclude = KafkaAutoConfiguration.class
        , scanBasePackages = {"com.didan..."}// Thư mục packet dự án của bạn
)
@EnableCaching
@EnableArchetype 
@EnableScheduling // Enable Scheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
```
- Ví dụ sử dụng
```java
   @Scheduled(fixedDelay = (1000 * 10))
    public void check() {
        log.info(timeRandom + " scheduling test end " + "\n");
    }
```
#### `Lưu ý`
- Hệ thống đang sử dụng hot reaload config, bản chất spring làm điều này thông qua proxy pattern.
- Khi sử dụng tính năng reload, sau thời điểm reload, Scheduled sẽ bị stop và chết :(
- Cách giải quyết là class impl thêm 1 interface `RefreshScheduler` để hệ thống tái kích hoạt lại Scheduled sau thời điểm reload config
```java
@Configuration

@Slf4j
public class ProcessCheckApp implements RefreshScheduler {
  @Autowired
  AsyncService asyncService;

  @Scheduled(fixedDelay = (1000 * 10))
  public void check() {
    log.info(timeRandom + " scheduling test end " + "\n");
  }
}
```
### Log hệ thống
- Lib đang sử dụng log4j làm abstract log, và sử dụng `spring-boot-starter-log4j2` để auto config log
- Chúng ta sử dụng `lombok` nên khi sử dung log chỉ cần khai báo `@Slf4j` tại đầu của class
- Sau đó để hiển thị log thì sử dụng biến `log.level(msg)` để hiển thị log
```java
@Service
@Slf4j
public class CacheTestServiceImpl implements CacheTestService {
    @Override
    public String getName(String name) {
        log.info("get new: "+name);
        return name;
    }

    @Override
    public String getName() {
        return "t";
    }
}
```
### Sử dụng AuthService
- Lib Cung cấp 1 Class chứa các method sử dụng để verify token.
- `Lưu ý token sẽ cần được mã hóa bởi Vault theo thuật toán RSA`
- Để sử dụng, chúng ta cần enabled auth service.
```java
app.auth.enabled=true
app.auth.type=key
app.auth.key.public-key=MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxzlsQZ+ikarESj+6RiVrNC3LaSgn1qAakBtxerGMl70I7jWVnLyWEWLYZ+n/szxU00Mbj1IJFUZdve21rX0WkGrckTjHNkGlkdDy/qcRknokla3A1PB70BkH0AbB15ROriiDkLjMHObPDm5twDjv3AvCaVdqv1MAcT95IhbdWhJdmXAx1X0SXeFjeBwVtGMD0MAH1M4gUVjQ6jsIb7LXC5suHENjKUpQUy2DGidWLBXuag8Zvb66vnCRn7HtyraJVaYduPSSlhAdyfGH+Np87WgGCCzNLyyBxsbazITF4Od1kry2wn0cn7dunYmGMKTlm9LNebIV0Ig13IISISbm++xugFaWrnpOr1PKk8NCD01OYN7HIkgUmuODUVS1vBXiyfcPA9XPQFz0THGF9okyszJPb0hMZvBffO+gQKLLAX7A0WjDn1rECZAlwXRM2PzaTjm+FPt4j+kprWPUpH67rDhcJvCSsmATe9zFhFt/DXax1LNNzp74i1WMmZM2OjYmIHmllrHJngjD4mwTKOc5XPPpDRFb9FgExYC7TSS3Tb03VqoeGqDRm77yrLoe8AWSsHBLbbTm17RqvMLbxmdPwRE3xpS995nB0NnDRliYMDJfaEo791Hiuf0B8ptlJi/hvMr2K67fRIUYeKH1qTz+djwKyS9IFg4vvmOD+ecg6BECAwEAAQ==
```
- Hiện tại lib đang chỉ support với 1 loại duy nhất là Key RSA, chúng ta cần truyền Public Key vào đây.
- Để sử dụng chúng ta lấy bề Bean của `AuthService`
```java
 @Autowired(required = false)
    AuthService authService;
    @Autowired
    ResponseFactory responseFactory;

    @GetMapping(value = "/demoAuthVerify")
    public ResponseEntity<GeneralResponse<Claims>> getRestDemo(String token) {
        Claims claims = authService.getClaims(token);
        if (Objects.nonNull(claims)) {
            return responseFactory.success(claims);
        }
        throw new BaseResponseException(ResponseStatusCodeEnumClient.TOKEN_ERROR );
    }
```
- Các method hỗ trợ bao gồm
```java
public interface AuthService {
    /**
     * Verify token chỉ trả về true hoặc false
     * @param token
     * @return trả về true hoặc false nếu lỗi
     */
    boolean verifyToken(String token);
    /**
     * Verify token trả về true nếu thành công.
     * nếu lỗi bắn ra Exception.
     * Nên bắt cả ngoại lệ Exception và vẫn cần kiểm tra true false
     * @param token  token truyền vào
     * @return true fasle hoặc throws Exception
     */
    @SuppressWarnings("java:S1130")
    boolean verifyTokenWReason(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, InvalidKeySpecException, NoSuchAlgorithmException;

    /**
     * Lấy về một Claims
     * @param token  token truyền vào
     * @return Claims hoặc null nếu lỗi
     */
    Claims getClaims(String token);
    /**
     * Lấy về một Claims
     * @param token  token truyền vào
     * @return Claims hoặc nhan ve Exception
     */
    Claims getClaimsThrows(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, InvalidKeySpecException, NoSuchAlgorithmException;
    public Claims getClaimsNotVerifyToken(String token);
}
```
###  Circuit Breaker
- Lib đang cài sẵn Circuit Breaker `resilience4j` nên mọi người có thể vào đây để thảm khảo cách thử dụng sâu hơn (https://resilience4j.readme.io/docs/getting-started-3)
- Ví dụ :
	- Khai báo config circuitbreaker trong `application.properties`
```properties
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.sliding-window-size=60
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.minimum-number-of-calls=20
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.sliding-window-type=TIME_BASED
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.wait-duration-in-open-state=50s
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.automatic-transition-from-open-to-half-open-enabled=true
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.TestCircuitBreakerCallFallbackMethod.permitted-number-of-calls-in-half-open-state=6
```
- Trong đó `TestCircuitBreakerCallFallbackMethod` là tên của  Circuit Breaker mà chúng ta muốn đặt tên
- Khai báo annotation trong java
```java
public class CircuitBreakerController  extends BaseController {
    @Autowired
    ResponseFactory responseFactory;

    @GetMapping(value = "/TestCircuitBreakerCallFallbackMethod")
    @CircuitBreaker(name = "TestCircuitBreakerCallFallbackMethod", fallbackMethod = "fallbackRequest")
    @SuppressWarnings("java:S112")
    public ResponseEntity<GeneralResponse<String>> getData() {
        throw new RuntimeException();
    }

    @SuppressWarnings({"java:S1172", "java:S3400"})
    public ResponseEntity<GeneralResponse<String>> fallbackRequest(Exception ex) {
        return responseFactory.success("Default data");
    }
}
```
- Giải thích : trong min 20 yêu cầu, nếu có trên 50% error thì trong 50s tiếp theo sẽ mặc định trả về một default...
### Sử dụng vault để lấy data bí mật (https://spring.io/projects/spring-vault)
- Lib có config sẵn để sử dụng vault trong trường hợp lưu trữ dữ liệu bí mật trên Vault
- Để sử dụng vault chúng ta cần config trong `bootstrap.properties` như sau
```properties
spring.cloud.vault.enabled=true
spring.cloud.vault.authentication=approle
spring.cloud.vault.app-role.role-id= {{key_role}}
spring.cloud.vault.app-role.secret-id={{key_secret}}
spring.cloud.vault.uri=http://vault-alpha.digital.vn
spring.cloud.vault.scheme=http
spring.cloud.vault.fail-fast=true
spring.cloud.vault.config.lifecycle.enabled=true
spring.cloud.vault.generic.enabled=false
```
- Tùy thuộc vào loại auth mà bạn sử dụng để kết nối vs Vault thì sẽ có kiểu config auth khác đi.
- Demo sử dụng Vault qua `VaultOperations`
```java 
 @Autowired
 VaultOperations vaultOps;
  
 @GetMapping(value = "/vaultDec")
    public ResponseEntity<GeneralResponse<String>> getDec(String data) {
        try {
            Plaintext vaultResponse = vaultOps.opsForTransit().decrypt("dt-transit", Ciphertext.of(data));
            return responseFactory.success(new String(vaultResponse.getPlaintext()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return responseFactory.fail( ResponseStatusCodeEnumClient.DATA_NOT_FOUND);
    }
```
### Sử dụng Kafka
- Để sử dụng kafka, trong `application.properties` cần khai báo config cho `consumer` và `producer` và `bootstrap`
```properties
spring.kafka.enabled=true
spring.kafka.bootstrap-servers=kafka-1:9092,kafka-2:9092,kafka-3:9092
spring.kafka.consumer.enabled=true

spring.kafka.producer.bootstrap-servers==kafka-1:9092,kafka-2:9092,kafka-3:9092
spring.kafka.producer.enabled=true
spring.kafka.producer.default-topic=event-default-topic
spring.kafka.producer.retries=3
spring.kafka.producer.request-timeout=4000
spring.kafka.producer.queue-size=100
```
- 1 số config thuộc tính mọi người có thể tìm hiểu thêm để rõ hơn tại:
	- https://spring.io/projects/spring-kafka
	- https://www.baeldung.com/spring-kafka
- Ví dụ nhận listen dữ liệu từ kafka
```java
@Configuration
@Slf4j
public class DemoListenKafka {
    @KafkaListener(topics = "springKafkaTest", groupId = "id")
    public void listenKafkaDemo(@Payload String message) {
        log.info("Nhận đc data từ topic: {} với thông tin là : {}", "springKafkaTest", message);
    }
}
```
- trong đó `topics` sẽ chưa tên của topic và `groupId` sẽ định nghĩa chúng ta lấy thuộc group nào
- Ví dụ về push kafka:
- Người dùng có thể Autowired `kafkaTemplate` hoặc sử dụng utils `PushDataToKafkaUtils` ở demo mình sẽ sử dụng `PushDataToKafkaUtils`
```java
    @Autowired
    PushDataToKafkaUtils pushDataToKafkaUtils;
    @GetMapping(value = "/kafka")
    public ResponseEntity<GeneralResponse<String>> getKafka(@RequestParam(value = "topic", defaultValue = "springKafkaTest") String topic) {
        pushDataToKafkaUtils.push("springKafkaTest Data", topic);
        return responseFactory.success(topic);
    }
```
### Custom RestTemplate để call API ra bên ngoài
-  Bạn có thể Autowired `RestTemplate` để sử dụng mặc định luôn. Tuy nhiên nếu bạn muốn custom mới 1 `RestTemplate` thì cần thêm `interceptorDefault` vào trong danh sách Interceptors
```java
  @Autowired
 InterceptorDefault interceptorDefault
  public RestTemplate restTemplateCustom() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(this.interceptorDefault));
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        return restTemplate;
        }
```
- Bạn cần set Factory sử dụng `BufferingClientHttpRequestFactory` để không bị lỗi request

### Time Trace run method
- Lib hỗ trợ 1 annotation sẽ tính toán thời gian sử sử lý của một method là bao nhiêu `nanoTime`
- Để sử dụng tính năng này cần enabled time trace ở trong `application.properties`
```properties
app.time-trace-enabled=true
```
- Sử dụng annotation `TimeTraceAspect` trên đầu các method để sử dụng tính năng này, Lưu ý: method phải là public,
- Tính năng này chỉ tính toán chuẩn nếu method chạy blocking, nếu non blocking (Asyn) thì sẽ không chính xác
### Header common Constant
- Lib có tạo sẵn 1 số constant Header hay sử dụng. Có thể xem tại `com.didan.archetype.constant.HeaderConstant`
```java
    public static final String AUTHORIZATION = "Authorization";
    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String TIMESTAMP = "Timestamp";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String USER_ID = "User-Id";
    public static final String CLIENT_ID = "Client-Id";
    public static final String CLIENT_SECRET = "Client-Secret";
    public static final String SIGNATURE = "Signature";
    public static final String REQUEST_ID = "Request-Id";
    public static final String ORIGINAL_REQUEST_ID = "Original-Request-Id";
```
## Thread Key
- Tại mỗi một request lib sẽ bắt và tự động lấy 1 số data từ header truyền đến để set vào `ThreadContext`
- 1 số key sau:
```java
    X_FORWARD_FOR("x-forwarded-for", "forwardIP"),
    X_REAL_IP("x-real-ip", "clientIP"),
    X_REQUEST_ID("x-request-id", "requestID"),
    X_CORRELATION_ID("X-Correlation-ID", "correlationID");
```
- Với `X_FORWARD_FOR` thì `x-forwarded-for` là header http và `forwardIP` là tên sẽ được đặt tên trong ThreadContext
- Với `X_REAL_IP` thì `x-real-ip` là header http và `clientIP` là tên sẽ được đặt tên trong ThreadContext
- Với `X_REQUEST_ID` thì `x-request-id` là header http và `requestID` là tên sẽ được đặt tên trong ThreadContext, nếu client không gửi lên, hệ thống sẽ tạo ngẫu nhiên 1 chuỗi
- Với `X_CORRELATION_ID` thì `X-Correlation-ID` là header http và `correlationID` là tên sẽ được đặt tên trong ThreadContext, nếu client không gửi lên, hệ thống sẽ tạo ngẫu nhiên 1 chuỗi
- Để sử lấy về data chúng ta sẽ dụng method static trong ThreadContext, chúng ta sẽ nhận được về một String
```java
ThreadContext.get(TrackingContextEnum.X_REQUEST_ID.getThreadKey())
ThreadContext.get("requestID")
```
### Log Http request từ client gửi lên ứng dụng
- Lib cung cấp 1 tính năng để log toàn bộ thông tin request và response của 1 yêu cầu từ client gửi lên
- Để sử dụng tính năng này chúng ta `enabled` `app.log-request-http` trong `application.properties`
```properties
app.log-request-http=true
```
- Ví dụ:
```text
 Request =  
Request to : http://localhost:8080/actuator/health 
Method     : GET 
Header     : {host=localhost:8080, connection=close, accept-encoding=gzip, user-agent=kube-probe/1.18} 
Body       :  
 

 Response =  
Status code  : {}200 
Header     : {x-request-id=47085ab774b6458a840bfdcfaf1788fc, X-Correlation-ID=@project.name@-3e8506db936c48eb86e29769ba2c25cb, Connection=close, Vary=Origin, Content-Length=635, Date=Wed, 09 Jun 2021 07:24:28 GMT, Content-Type=application/vnd.spring-boot.actuator.v3+json;charset=UTF-8} 
Body       : {"status":"UP","components":{"db":{"status":"UP","details":{"database":"MariaDB","result":1,"validationQuery":"SELECT 1"}},"devOppService":{"status":"UP"},"discoveryComposite":{"description":"Discovery Client not initialized","status":"UNKNOWN","components":{"discoveryClient":{"description":"Discovery Client not initialized","status":"UNKNOWN"}}},"diskSpace":{"status":"UP","details":{"total":63278178304,"free":10575486976,"threshold":10485760}},"ping":{"status":"UP"},"refreshScope":{"status":"UP"},"serviceSelfHealth":{"status":"UP","details":{"service_status":"available"}},"vault":{"status":"UP","details":{"version":"1.5.2"}}}} 
```
### Log Http request khi sử dụng RestTemplate call APi ra bên ngoài
- Lib cung cấp 1 tính năng để log toàn bộ thông tin request và response của 1 yêu cầu từ ứng dụng của chúng ta đến ứng dụng khác thông qua RestTemplate
- Lưu ý: Nếu dùng RestTemplate mặc định và config true thì mặc định hỗ trợ tính năng này, tùy nhiên nếu custom một RestTemplate mới thì cần làm theo hướng dẫn `Custom RestTemplate để call API ra bên ngoài`
- Để sử dụng tính năng này chúng ta `enabled` `app.default-service-enable-log-request` trong `application.properties`
```properties
app.default-service-enable-log-request=true
```
- Ví dụ:
```text
 Request =  
Request to : http://localhost:8080/actuator/health 
Method     : GET 
Header     : {host=localhost:8080, connection=close, accept-encoding=gzip, user-agent=kube-probe/1.18} 
Body       :  
 

 Response =  
Status code  : {}200 
Header     : {x-request-id=47085ab774b6458a840bfdcfaf1788fc, X-Correlation-ID=@project.name@-3e8506db936c48eb86e29769ba2c25cb, Connection=close, Vary=Origin, Content-Length=635, Date=Wed, 09 Jun 2021 07:24:28 GMT, Content-Type=application/vnd.spring-boot.actuator.v3+json;charset=UTF-8} 
Body       : {"status":"UP","components":{"db":{"status":"UP","details":{"database":"MariaDB","result":1,"validationQuery":"SELECT 1"}},"devOppService":{"status":"UP"},"discoveryComposite":{"description":"Discovery Client not initialized","status":"UNKNOWN","components":{"discoveryClient":{"description":"Discovery Client not initialized","status":"UNKNOWN"}}},"diskSpace":{"status":"UP","details":{"total":63278178304,"free":10575486976,"threshold":10485760}},"ping":{"status":"UP"},"refreshScope":{"status":"UP"},"serviceSelfHealth":{"status":"UP","details":{"service_status":"available"}},"vault":{"status":"UP","details":{"version":"1.5.2"}}}} 
```
### Grpc
- Lib hỗ trợ sẵn mọi người làm 1 Server GRPC và Client GRPC
- Để làm server gprc thì xem hướng dẫn tại đây (https://www.baeldung.com/grpc-introduction)
- Ví dụ :
```java
@GRpcService
public class HelloWorldGRPCServerController extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = HelloResponse.newBuilder().setReply("Replly for msg :"+request.getGreeting()+" thanks for me").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
```
- lib hỗ trợ sẵn class GrpcRequestFactory để gửi request đến một GRPC server
```java
    @Autowired
    GrpcRequestFactory grpcRequestFactory;

    @GetMapping(value = "/testGrpc")
    public String getRestGrpc(String msg) {
        final ManagedChannel channel = grpcRequestFactory.createChannel("127.0.0.1", 8081);
        HelloServiceGrpc.HelloServiceBlockingStub helloServiceFutureStub = HelloServiceGrpc.newBlockingStub(channel);
        HelloRequest helloRequest = HelloRequest.newBuilder().setGreeting(msg).build();
        HelloResponse helloResponse = helloServiceFutureStub.sayHello(helloRequest);
        return helloResponse.getReply();
    }
```
### Config Swagger authentication API
- Lib hỗ trợ config sẵn Swagger authentication for API bằng  `application.properties`
```properties
swagger.auth.enabled=true
swagger.auth.name=bearerAuth
```
- trong đó name là tên của auth
- Trên swgeer mặc định sẽ điền token trên Header với thuộc tính `Authorization`
- Để sử dụng auth trên trên controller chúng ta config như sau
```java
  @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping()
    public ResponseEntity<GeneralResponse<PagingResponseDTO<ApproveRoleConfigResponseDTO>>> getAllApproveRoleConfigs()
```
![alt text](https://image.prntscr.com/image/af7pRM_PQwS_bTsldh1S8w.png)
### Custom info swagger
- Lib hỗ trợ custom 1 số info cơ bản của Swagger bằng  `application.properties`
```properties
springdoc.api-docs.enabled=true
springdoc.api-docs.title=Di Dan Archetype
springdoc.api-docs.description=Nguyen Di Dan Architecture
springdoc.api-docs.contactName=Nguyen Di Dan
springdoc.api-docs.contactUrl=https://iam.didan.id.vn
springdoc.api-docs.version=1.0.0
```
### validator
- Lib hỗ trợ sẵn 1 số validator để mọi người sử dụng dễ hơn. Nó sử dụng tương tự như validate của javax
- Mọi người có thể vào `com.didan.archetype.validator.annotation` để tham khảo danh sách
- Ví dụ check 1 dữ liệu trong object not null
```java
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SingleValueNotNull(value = {"id", "base64"}, message = "Need to enter id or base64 of Image")
public class TsImageBase {
    @Schema(example = "e1e134d8-1543-4b3b-bb44-dd5ad7ab7615")
    private String id;
    @Schema(example = "null")
    @Expose(serialize = false, deserialize = false)
    private String base64;
    @Schema(hidden = true)
    private String label;
}
```
```java
  @PostMapping(value = "/test", produces = "application/json", consumes = "application/json")
    public ResponseEntity<GeneralResponse<UserEntity>> test(@Valid @RequestBody() TsImageBase test) {
     
    }
```
