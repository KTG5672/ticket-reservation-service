## In-Memory DB(H2) → Testcontainers(MySQL) 전환

### 개요
- Spring Boot 테스트 환경에서는 `H2 Database` 같은 **In-Memory DB**를 사용해 테스트를 쉽게 실행할 수 있음  
하지만 운영 환경이 `MySQL`인 경우, **SQL 방언·콜레이션·락 메커니즘 차이**로 인해  
테스트 결과가 실제 서비스와 다르게 동작할 수 있음

- `H2` 기반 테스트 환경과 **Testcontainers**를 사용해 **운영 DB(`MySQL`)** 사용하는 테스트 환경을 비교하며 적용하는 방법 정리

---

### In-Memory DB(H2) 사용 시 발생할 수 있는 문제

| 구분 | MySQL (운영) | H2 (테스트) | 차이점 / 문제점 |
|------|---------------|--------------|----------------|
| DB 엔진/방언 | InnoDB / MySQL8Dialect | H2Dialect | SQL 기능·타입 일부 미지원 |
| Lock 메커니즘 | Record + Gap Lock (Next-Key) | Record Lock Only | 팬텀 리드 / 데드락 재현 불가 |
| Collation | utf8mb4_general_ci (대소문 무시) | UTF-8 (대소문 구분) | LIKE 검색 결과 차이 |
| 정렬 | “가 나 다” | 유니코드 순 (“가 갸 개 …”) | 정렬 순서 불일치 |
| 타입 | JSON / ENUM / UNSIGNED | 일부 미지원 | 스키마 DDL 실패 |
| TimeZone | UTC 설정 가능 | JVM 기본 | Timestamp 비교 차이 |

---

### H2 Database, MySQL 차이 사례

#### 1️⃣ Gap Lock 미지원

MySQL에서는 아래 쿼리를 실행하면 `(5, 10)` 구간 전체에 Gap Lock이 설정되어 INSERT가 차단됩니다.  
H2에서는 이 락이 존재하지 않음

```
SELECT * FROM reservations
WHERE id BETWEEN 5 AND 10
FOR UPDATE;
```

- **MySQL (InnoDB)** → 구간 내 INSERT 불가  
- **H2** → 존재하는 행만 락 → 구간 INSERT 가능  
- 결과 → 운영에서는 Deadlock, 테스트에서는 정상 통과  

>  Gap Lock = 존재하지 않는 행 사이의 빈 공간(Gap)에 거는 락으로, 팬텀 리드를 방지하지만 H2에는 미지원

---

#### 2️⃣ 한글 검색 및 정렬 차이

#### LIKE 검색

| 패턴 | 데이터 | MySQL | H2 | 비고 |
|------|---------|--------|-----|------|
| `%한글%` | `한글ABC` | ✅ 매칭 | ✅ 매칭 | 정상 |
| `%한글%` | `한글abc` | ✅ 매칭 | ⚠️ 불일치 가능 | 정규화/대소문자 차이 |

- H2는 대소문자 구분 (case-sensitive) 비교를 수행
- “한글”이 분해형 (NFD)으로 저장되면 정규화 없이 비교되어 불일치
- MySQL은 `utf8mb4_general_ci` 콜레이션으로 정규화 및 대소문자 무시 비교를 수행

#### 정렬 (ORDER BY)

| DB | 결과 |
|----|------|
| MySQL | 가 → 나 → 다 (자연스러운 순서) |
| H2 | 가 → 갸 → 개 … (유니코드 코드 순서) |

---

### 해결 방안
#### Testcontainers 란?
- Docker 컨테이너에서 실행 가능한 데이터베이스, 메시지 브로커, 웹 브라우저 등을 일회용 인스턴스로 제공 해주는 오픈 소스 라이브러리
- Spring Boot, Junit 환경에서 편리하게 컨테이너를 조작 할 수 있는 인터페이스 제공 (@Testcontainers, @Container) 
#### JUnit 에서 Testcontainers 적용

**Gradle 의존성**
```
testImplementation platform("org.testcontainers:testcontainers-bom:2.0.0")
testImplementation "org.testcontainers:mysql" // MySQL 컨테이너를 띄우기 위한 라이브러리
testImplementation "org.testcontainers:junit-jupiter" // Junit 환경에서 간편하게 사용하기 위한 라이브러리
```


**MySQL 컨테이너 정의 추상 클래스 (필요 시 상속)**
```
@Testcontainers // 컨테이너 시작과 중지를 자동으로 해줌
public abstract class TestContainerForMySQL {

    @Container // static 으로 선언 되어 테스트 클래스 단위에서 한번만 시작하고 중지 (@BeforeAll, @AfterAll)
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    }

}
```
**BaseEntityTest (실제 사용)**
```
@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class BaseEntityTest extends TestContainerForMySQL {
    ...
}
```

**application-test.yml**
```
spring:
  test:
    database:
      replace: none
  jpa:
    hibernate:
      ddl-auto: update
  flyway:
    enabled: false
```
---

### 결론

- H2는 빠르고 편리하지만, Testcontainers를 사용하여 운영 환경과 테스트 환경을 맞춰 테스트 신뢰성을 높일 수 있음 
- 단위 테스트는 Mock 으로 대체 가능 하지만 통합 테스트 시 DB 연결 및 동작을 검증 가능
- 개발자 PC 나 CI 환경에서 따로 DB 설치가 필요 없어 편리함

---

### 참고 자료
- [Testcontainers MySQL 공식 문서](https://java.testcontainers.org/modules/databases/mysql/)
- [H2 Database Features](https://www.h2database.com/html/main.html)
- [MySQL 8.0 Locking Reads 공식 문서](https://dev.mysql.com/doc/refman/8.0/en/innodb-locking-reads.html)
