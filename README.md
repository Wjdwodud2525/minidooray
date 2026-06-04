# Mini Dooray Myself

NHN Academy 팀 프로젝트였던 Mini Dooray를 개인 방식으로 재구성한 프로젝트입니다. 프로젝트, 태스크, 태그, 마일스톤, 댓글, 사용자 계정을 분리된 서비스로 관리하는 Spring Boot 기반 MSA 예제입니다.

## 구성

- `eureka-server`: 서비스 디스커버리
- `api-gateway`: API Gateway 및 서비스 라우팅
- `account-api`: 사용자 가입, 조회, 로그인, 휴면 전환
- `task-api`: 프로젝트, 멤버, 태스크, 태그, 마일스톤, 댓글 관리
- `front-gateway`: Thymeleaf 기반 화면 및 API 연동

## 기술 스택

- Java 21
- Spring Boot 4
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Spring Data JPA
- Thymeleaf
- MySQL, H2(test)
- Maven

## 실행 전 환경변수

운영/로컬 실행 시 DB 접속 정보는 환경변수로 주입합니다.

```bash
export ACCOUNT_DB_URL="jdbc:mysql://host:port/database"
export ACCOUNT_DB_USERNAME="account_user"
export ACCOUNT_DB_PASSWORD="account_password"

export TASK_DB_URL="jdbc:mysql://host:port/database?serverTimezone=Asia/Seoul&useSSL=false"
export TASK_DB_USERNAME="task_user"
export TASK_DB_PASSWORD="task_password"
```

## 테스트

각 모듈은 외부 DB 없이 H2 기반으로 테스트됩니다.

```bash
./mvnw test
(cd eureka-server && ./mvnw test)
(cd api-gateway && ./mvnw test)
(cd account-api && ./mvnw test)
(cd task-api && ./mvnw test)
(cd front-gateway && ./mvnw test)
```

## 로컬 단일 실행

루트 애플리케이션을 실행하면 Eureka, Account API, Task API, API Gateway, Front Gateway가 순서대로 실행됩니다. 로컬 실행에서는 `account-api`, `task-api`가 H2 DB를 사용합니다.

```bash
./mvnw spring-boot:run
```

실행 후 `http://localhost:8080`으로 접속합니다. 종료는 터미널에서 `Ctrl+C`를 누릅니다.

로컬 실행 기본 계정:

| 아이디 | 비밀번호 |
| --- | --- |
| `test` | `1234` |
| `admin` | `1234` |
| `user1` | `1234` |
| `user2` | `1234` |
| `user3` | `1234` |

## 주요 기능

- 사용자 가입, 조회, 수정, 삭제, 로그인
- 프로젝트 생성, 조회, 수정, 삭제
- 프로젝트 멤버 추가 및 제거
- 태스크 생성, 조회, 수정, 삭제
- 태그 및 마일스톤 관리
- 태스크 댓글 등록, 수정, 삭제
- Gateway 기반 API 라우팅
