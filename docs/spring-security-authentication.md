# 인증 방식 비교: 세션 vs 토큰 (JWT)

## 1. 세션 기반 인증 (Session-Based Authentication)

### 동작 흐름
1. 클라이언트가 로그인 API 호출 (email, password)
2. 서버가 DB 등으로 사용자 정보를 확인
    - 성공 시 세션(로그인 상태 정보)을 생성해 서버 측 저장소에 저장 (메모리, DB, Redis 등)
    - 실패 시 401 Unauthorized
3. 서버는 세션 ID를 응답의 `Set-Cookie` 헤더로 내려보냄
4. 이후 클라이언트는 요청마다 이 쿠키(세션 ID)를 자동으로 전송
5. 서버는 세션 ID로 세션 저장소를 조회
    - 유효하면 요청 처리
    - 유효하지 않으면 401

### 장점
1. **보안 제어 용이**  
   서버가 세션 상태를 직접 들고 있으므로, 강제 로그아웃 / 접속 차단 / 권한 변경 같은 조치가 즉시 가능
2. **구현 단순** ★  
   스프링 시큐리티 formLogin이나 기본 세션 관리 기능을 쓰면 바로 동작함

### 단점
1. **확장성 이슈** ★  
   서버 인스턴스가 여러 대일 경우, 세션을 공유 저장소(Redis 등)로 빼지 않으면 "이 서버에서 로그인했는데 저 서버에서는 로그인이 안 된" 현상이 발생  
   → 해결은 가능하지만 인프라 구성이 추가됨
2. **서버 부하 증가 가능성** ★  
   세션 정보를 메모리/스토리지에 계속 유지해야 하므로 유저 수에 비례해 관리 비용이 늘어남
3. **쿠키 의존**  
   브라우저 기반 흐름이 기본이라, 모바일/3rd-party 클라이언트와 통신할 땐 커스텀 처리가 필요할 수 있음

---

## 2. 토큰 기반 인증 (Stateless Token / JWT 스타일)

### 동작 흐름
1. 클라이언트가 로그인 API 호출 (email, password)
2. 서버가 사용자 정보를 확인한 뒤 Access Token (예: JWT)을 발급해 응답으로 내려줌
3. 클라이언트는 이 토큰을 저장 (예: 메모리, sessionStorage, httpOnly cookie 등)
4. 이후 모든 요청마다 `Authorization: Bearer <token>` 헤더로 토큰을 전송
5. 서버는 이 토큰을 검증 (서명, 만료 시간 등)하여 사용자 신원을 확인  
   → 별도의 세션 조회 없이 처리 (DB/메모리 접근 없이도 가능)

### 장점
1. **무상태성(Stateless)** ★  
   서버가 로그인 상태를 들고 있지 않으므로 서버를 수평 확장(스케일 아웃)하기 쉽다
2. **유연성** ★  
   브라우저, 모바일 앱, 외부 서비스 등 이형 클라이언트에 공통적으로 적용 가능  
   (쿠키가 아니어도 Authorization 헤더만 보내면 OK)
3. **성능**  
   단순 검증은 DB 조회 없이 가능하므로 빠르고, 캐시 친화적

### 단점
1. **만료 관리가 필요** ★  
   Access Token은 보통 만료 시간을 짧게 둔다  
   만료 후에는 Refresh Token으로 재발급하는 흐름이 필요 → 이건 결국 어느 정도 서버 상태 관리가 다시 개입
2. **토큰 탈취 리스크**  
   유효한 토큰이 유출되면, 만료 전까지 공격자가 그대로 쓸 수 있다  
   → 대응: 만료 짧게, Refresh 분리, HTTPS 강제, httpOnly/secure 쿠키 등 방어책 필요
3. **토큰 크기**  
   JWT는 사용자 클레임(roles 등)이 포함되므로 세션 ID보다 상대적으로 길다 (대역폭 영향은 보통 미미하지만 존재)

---

## 결론
- **웹 중심, 서버 관리형 환경 (예: 사내 어드민 콘솔)**  
  → 세션 기반도 충분히 합리적이고 개발이 단순하다.
- **확장성/멀티 디바이스/API 퍼블릭화를 우선 (예: 모바일 앱, 외부 파트너 API)**  
  → 토큰(JWT) 기반 인증이 유리하다. ★

현대 서비스에서는
- 외부/공용 API: JWT (Access/Refresh)
- 내부 관리자 페이지: 세션 기반
  처럼 **혼합 전략**도 많이 쓴다.

---

# Spring Security

Spring Security는 Servlet 필터 기반으로 동작

## 핵심
- 서블릿 컨테이너의 `FilterChain` 단계에서 보안 처리를 선행한다.
- 기본 서블릿 필터는 스프링 빈을 모르며, `DelegatingFilterProxy`를 사용해 "서블릿 컨테이너에 등록된 Filter"가 실제로는 "스프링 빈으로 관리되는 보안 필터 체인"으로 위임되도록 연결해준다.
- Spring Security는 이 위임 구조 위에 `FilterChainProxy` / `SecurityFilterChain`을 구성해서
    - 인증(Authentication)
    - 인가/권한(Authorization)
    - 세션/토큰 관리
    - CSRF 방어
      등을 한 번에 처리한다.

결과적으로 컨트롤러(`@RestController`)까지 요청이 도달하기 전에 이미 "인증", "인가" 여부를 결정한다.

---

# Spring Security 주요 기능

## 1. 요청별 접근 제어 (Authorization)
```
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/public/**").permitAll()      // 누구나 접근 가능
    .requestMatchers("/admin/**").hasRole("ADMIN")  // ADMIN 권한만
    .anyRequest().authenticated()                   // 나머지는 로그인 필요
);
```
→ 이걸 안 쓰면 따로 직접 권한 체크 분기 로직을 넣어야 함

또한 Spring Security는 여러 개의 SecurityFilterChain을 둘 수 있다:
```
@Bean
@Order(1)
SecurityFilterChain api(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    return http.build();
}

@Bean
@Order(2)
SecurityFilterChain web(HttpSecurity http) throws Exception {
    http.securityMatcher("/**")
        .formLogin(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    return http.build();
}
```
- /api/** 는 JWT 같은 stateless 나머지 /** 은 Session + Form Login 로 인증
- 한 애플리케이션 안에서 두 개 이상의 인증 방식이 가능

## 2. CSRF 보호

CSRF(Cross-Site Request Forgery)는 “브라우저가 자동으로 쿠키를 붙여 보내는” 특성을 악용해,
사용자 의도와 무관한 요청(POST/PUT/DELETE 등)을 보내게 만드는 공격이다.

Spring Security의 기본 전략:
1. 세션마다 CSRF 토큰을 발급/저장
2.	상태 변경 요청 시 클라이언트가 그 토큰을 헤더나 폼 필드로 같이 보냄
3.	서버에서 토큰을 검증해 요청이 진짜 사용자의 의도인지 확인

만약 Authorization 헤더에 Bearer 토큰(JWT)을 직접 실어보내는 stateless API라면, 브라우저 쿠키 자동 전송을 하지 않으므로 보통 **csrf().disable()** 한다.

## 3. 로그인 처리 (폼 로그인 / 세션)

Spring Security는 로그인 폼까지 기본 제공 가능하다.
```
http
    .formLogin(form -> form
        .loginPage("/login")                // 커스텀 로그인 페이지 (GET /login)
        .loginProcessingUrl("/login")       // 로그인 제출 (POST /login)
        .defaultSuccessUrl("/", true)       // 성공 시 이동
        .failureUrl("/login?error=true")    // 실패 시 이동
        .permitAll()
    );
```

•	인증이 안 된 사용자가 보호된 URL에 접근하면 자동으로 /login으로 리다이렉트
•	로그인 성공 시 SecurityContextHolder에 인증 정보가 저장되고 세션으로 이어짐

## 4. SecurityContext (현재 사용자 조회)

요청이 인증에 성공하면 Security 필터가 SecurityContextHolder에 Authentication을 넣어 둔다.

직접 꺼내는 방식:
```
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
Object principal = auth.getPrincipal(); // 보통 UserDetails 구현체

스프링 MVC 방식 (권장):

@GetMapping("/me")
public UserInfoResponse me(@AuthenticationPrincipal CustomUserDetails user) {
    return new UserInfoResponse(user.getId(), user.getEmail());
}
```
컨트롤러/서비스 어디서든 “현재 로그인한 사용자” 접근이 쉬워지고, 별도의 ThreadLocal 직접 관리 같은 걸 안 해도 된다.

## 5. PasswordEncoder

비밀번호 평문 저장 금지. 스프링은 PasswordEncoder 인터페이스로 표준화해둠.

```
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
서비스/도메인 계층에서는 이 인터페이스만 사용하면 되고, 구현은 Spring Security의 BCrypt 등으로 캡슐화 가능.

적용:
```
public class SecurityPasswordPort implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public PasswordHash encode(Password password) {
        return new PasswordHash(passwordEncoder.encode(password.value()));
    }

    @Override
    public boolean matches(Password rawPassword, PasswordHash encodedPassword) {
        return passwordEncoder.matches(rawPassword.value(), encodedPassword.value());
    }
}
```

⸻

현재 서비스 구조 (초기 설정)

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // 로그인/회원가입 등은 열어둠
                .anyRequest().authenticated()                  // 나머지는 인증 필요
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
이 설정의 의미:
- SessionCreationPolicy.STATELESS: 이 API는 세션을 쓰지 않고 토큰 같은 stateless 인증을 쓸 준비
- /api/v1/auth/**는 로그인/회원가입 등 인증 전 단계라 permitAll()
- 그 외 모든 요청은 “인증 필요”라고 선언 → 추후 JWT 필터만 추가
- 토큰 인증 기반으로 csrf().disable()

⸻

### 결론
- 세션: 단순하고 컨트롤 쉬움. 서버가 상태를 들고 있으므로 관리 비용이 붙고 수평 확장 시 공유 저장소 필요.
- 토큰(JWT): 무상태 기반이라 확장/멀티클라이언트에 유리. 대신 만료/회수/탈취 방어 전략이 필수.
- Spring Security는 이 둘을 모두 지원하며, 필터 체인 수준에서 URL 패턴별로 다른 정책(세션/토큰 혼합)까지 동시에 구성 가능하다. 
- 설정(SessionCreationPolicy.STATELESS)은 토큰 베이스 인증을 채택하려는 방향으로 맞게 설계 되어 있다.

