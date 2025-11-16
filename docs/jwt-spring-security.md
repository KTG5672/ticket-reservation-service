# JWT + Spring Security 인증 구현
## JWT 란?
### 개요
- JSON Web Token 줄임말이며 클레임(Claim)을 JSON 형식으로 표현하고 안전하게 전달하기 위한 표준 규격 (RFC 7519)
- HMAC(대칭키) 또는 RSA/ECDSA(비대칭키)를 이용한 디지털 서명으로 데이터의 무결성과 발급자 신뢰성을 보장
- 주로 인증, 인가 정보를 담아 서버에 요청하는 용도로 사용
### 구조
- 헤더, 페이로드, 서명으로 구성되며 **마침표(.)** 로 구분
- 헤더, 페이로드는 Base64URL(URL-safe Base64) 로 인코딩되어 구성되며, 서명은 인코딩된 헤더/페이로드를 기반으로 생성
- 헤더/페이로드는 암호화된 것이 아니므로 토큰 자체만으로는 비밀 정보 보호가 되지 않으며, 누구나 내용을 디코딩해서 읽을 수 있음
- 헤더 (Header)
  - 토큰의 타입과 서명 알고리즘을 포함 
  - alg: 서명 부분의 암호화 알고리즘을 표기 (HS256, HS512, RS256, ...)
  - typ: 토큰 타입을 표기 보통 "JWT" 으로 통일
    - 서명이 포함된 JWT 를 **JWS(Signature)** 라고 하나 상위 개념으로 JWT 로 표기
    - **JWE(Encryption)** 는 페이로드가 암호화된 JWT
  ```json
    {
        "alg": "HS256",
        "typ": "JWT"
    }
  ```
- 페이로드 (Payload)
  - 다양한 값들을 포함하며 담기는 값들을 클레임(Claim) 이라고 함
  - 등록된 클레임 - 서비스에 대한 정보가 아닌 토큰에 대한 정보
    - iss(Issuer): 토큰을 발급한 발급자의 식별자를 지정
    - sub(Subject): 토큰의 주제로 어떤 대상을 나타내는지 지정
    - aud(Audience): 토큰의 대상으로 토큰을 받을 수신자를 지정
    - exp(Expiration Time): 토큰의 만료 시간을 지정 (Epoch 초 단위 시간, NumericDate)
    - nbf(Not Before): 토큰의 유효 시작 시간을 지정 (Epoch 초 단위 시간, NumericDate)
    - iat(Issued At): 토큰이 발급된 시간을 지정 (Epoch 초 단위 시간, NumericDate)
    - jti(JWT ID): 토큰의 고유 식별자를 지정 (UUID, 난수 기반 문자열)
  - 공개 클레임 - 여러 시스템에서 공통으로 사용할 수 있도록 정의된 공개용 정보
    - 표준 조직(IANA)에 등록되어 있거나, 충돌 방지를 위한 네임스페이스(URI 등)를 사용해 정의한 키 (email, name 등)
  - 비공개 클레임 - 사용자 정의 클레임으로 비공개용 정보 (서비스 정보)
    - 내부 서비스에서만 사용하는 키 (userId 등)
- 서명 (Signature)
  - 헤더와 페이로드 문자열을 연결(.)하여 특정 암호화 알고리즘으로 암호화한 문자열
  ```
    signature = Base64UrlEncode(
        HMAC-SHA256(base64URL(header) + "." + base64URL(payload), secretKey)
    )
    signature = Base64UrlEncode(
        RSA-SHA256(base64URL(header) + "." + base64URL(payload), privateKey)
    )
  ```
### 검증
1. 구분자(.) 로 헤더, 페이로드, 서명으로 분리
2. 헤더와 페이로드를 base64URL 디코딩
3. 헤더에 지정된 알고리즘과 대칭 키 또는 공개 키를 사용하여 서명 검증
---
## JWT 인증 구현 (Spring Security)
### Security 인증 과정
- Spring Security 기본 인증 과정은 Login Form(UsernamePasswordAuthenticationFilter)을 이용한 인증
```
Client
  │ POST /login
  ▼
UsernamePasswordAuthenticationFilter
  │ extract username/password
  │ create UsernamePasswordAuthenticationToken
  ▼
AuthenticationManager (ProviderManager)
  ▼
DaoAuthenticationProvider
  │ loadUserByUsername()
  │ passwordEncoder.matches()
  ▼
Authentication (UserDetails 포함)
  ▼
SecurityContextHolder.setAuthentication()
  ▼
Controller 접근 가능 (인증된 상태)
```
### JWT 인증 필터 구현
- UsernamePasswordAuthenticationFilter 에서 인증을 수행 하기전에 커스텀한 JWT 를 이용한 인증 필터를 미리 끼워 넣음
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http
      // ...
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      // ...
}
```
- 토큰을 검증하고 정보를 추출하여 SecurityContextHolder.setAuthentication 를 이용하여 인증 정보를 넣음
  - SecurityContextHolder 에 인증 정보를 넣으면 Security 전체에 인증 되었다라고 알리는 셈
- OncePerRequestFilter 를 상속받아 요청 당 한번만 수행
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

      public static final String AUTHORIZATION = "Authorization";
      public static final String BEARER_PREFIX = "Bearer ";
    
      private final TokenProvider tokenProvider;
    
      @Override
      protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain) throws ServletException, IOException {
    
            // 1. Header 에서 Authentication 추출
            String authHeader = request.getHeader(AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            // 2. "Bearer " Prefix 제거
            String token = authHeader.substring(BEARER_PREFIX.length());
            if (token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
    
            // 3. 토큰 검증
            try {
                if (tokenProvider.validateAccessToken(token)) {
                    // 4. 토큰에서 유저 식별자 조회
                    String userId = tokenProvider.getUserIdByAccessToken(token);
    
                    // 5. 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of());
                    // 6. Security Context에 등록
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                // @Todo: RefreshToken 으로 AccessToken 재발급
                log.debug("Expired JWT token : {}", e.getMessage());
            }
    
            filterChain.doFilter(request, response);
      }
}
```
### 인증 예외 처리
- 현재 구조에서 JWT 검증에 실패하거나 토큰이 없으면 doFilter 로 다음 필터로 넘기며, 결국 엔드 포인트에서는 미인증 상태로 간주됨
- FilterChain 의 마지막에 위치한 FilterSecurityInterceptor 까지 도달했을 때, 미인증 상태로 보호된 엔드포인트에 접근하면 AuthenticationException 이 발생
- ExceptionTranslationFilter 가 해당 예외를 catch 하여 SecurityContextHolder 를 초기화(clear) 한 뒤 AuthenticationEntryPoint 를 호출
- AuthenticationEntryPoint.commence 메서드 호출하여 예외 처리
- 기본 AuthenticationEntryPoint 는 FormLogin EntryPoint 로 로그인 페이지로 redirect (302)
- 따라서 JWT 인증 실패 시에는 커스텀한 AuthenticationEntryPoint 를 구현하여 등록 해줘야함
```java
// Custom AuthenticationEntryPoint
@RequiredArgsConstructor
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ErrorCodeHttpStatusMapper errorCodeMapper;
  
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException authException) throws IOException {
  
          String message = authException.getMessage();
          HttpStatus httpStatus = errorCodeMapper.get(SecurityErrorCode.UNAUTHORIZED);
  
          response.setStatus(httpStatus.value());
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          response.setCharacterEncoding(StandardCharsets.UTF_8.name());
          response.getWriter().write(objectMapper.writeValueAsString(
              ApiErrorResponse.builder()
                  .code(SecurityErrorCode.UNAUTHORIZED.code())
                  .message(SecurityErrorCode.UNAUTHORIZED.message() + "(" + message + ")")
                  .build()
          ));
  
    }
}

// 예외 등록
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // ...
          .exceptionHandling(config -> 
            config.authenticationEntryPoint(authenticationEntryPoint)
          );
        // ...
}

```