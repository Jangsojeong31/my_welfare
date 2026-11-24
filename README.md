# 마이 복지 

<div align="center">

프로필과 자연어 질문으로 받을 수 있는 복지 서비스를 찾아 주는 웹 서비스입니다.<br/>
공공 복지 데이터를 저장해 두고, 조건 검색·맞춤 추천·AI 검색으로 조회합니다.

<p align="center">
    <strong>배포 주소</strong> : <a href="https://my-welfare-six.vercel.app/">https://my-welfare-six.vercel.app/</a>
</p>

</div>

---

## 목차

+ [프로젝트 소개](#-프로젝트-소개)
+ [기술 스택](#️-기술-스택)
+ [아키텍처](#-아키텍처)
+ [설계 문서](#-설계-문서)
+ [프로젝트 구조](#프로젝트-구조)
+ [설치 및 실행 방법](#설치-및-실행-방법)

---

## 📌 프로젝트 소개

> 회원이 지역, 연령, 가구, 관심 분야를 저장하면 맞는 복지 서비스를 점수순으로 보여 줍니다.<br/>
> 조건 필터로 목록을 찾고, 자연어 질문은 검색 조건으로 바꾼 뒤 관련 서비스와 답변을 함께 반환합니다.<br/>
> 복지 원천 데이터는 한국사회보장정보원 Open API에서 수집합니다.

### ✨ 주요 기능

+ **회원가입 / 로그인**: 이메일 중복 확인, BCrypt 비밀번호, JWT 액세스 토큰(24시간)
+ **프로필**: 생년월일, 성별, 거주 지역, 소득 수준, 관심 분야, 가구 형태 저장
+ **복지 목록 / 상세**: 생애주기·가구형태·관심분야 필터와 페이지 조회
+ **맞춤 복지**: 로그인 프로필과 연관성이 높은 복지 서비스의 점수를 매겨 조회
+ **AI 검색**: 질문을 지역·소득·키워드 등 조건으로 추출하고, 조건에 적합한 복지 서비스 조회 
+ **데이터 수집**: 중앙부처·지자체 목록 API와 상세 API를 연동해 복지 데이터 수집, DB에 저장

---

## 🛠️ 기술 스택

**Frontend**

![Vue](https://img.shields.io/badge/Vue_3-4FC08D?style=flat&logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white)
![Pinia](https://img.shields.io/badge/Pinia-FFD859?style=flat&logo=pinia&logoColor=black)
![Vue Router](https://img.shields.io/badge/Vue_Router-4FC08D?style=flat&logo=vuedotjs&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=flat&logo=tailwindcss&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=flat&logo=axios&logoColor=white)

**Backend**

![Java](https://img.shields.io/badge/Java_17-007396?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)
![Validation](https://img.shields.io/badge/Validation-6DB33F?style=flat&logo=springboot&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=flat&logo=openai&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=flat&logo=mysql&logoColor=white)

**협업**

![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)

---

## 🏗️ 아키텍처

하나의 Spring Boot 프로세스 안에 기능 모듈을 두고, 각 모듈은 아래 계층으로 나눕니다.

+ **api**: HTTP, 검증, `ApiResponse`
+ **application**: 트랜잭션과 유스케이스
+ **domain**: 엔티티와 순수 규칙
+ **infrastructure**: JPA, 외부 HTTP

| 모듈 | 책임 | 진입 API |
|------|------|----------|
| `user` | 가입, 로그인, 프로필, 맞춤 점수 | `/api/auth`, `/api/users/me/profile` |
| `welfare` | 복지 목록, 상세, 코드 마스터 | `/api/welfare` |
| `ai` | 자연어 조건 추출과 답변 | `/api/ai/welfare/search` |
| `ingestion` | 공공 API 수집과 적재 | `/api/ingestion/welfare` |

`security`는 JWT 필터와 비밀번호 해시를 담당합니다. `common`은 공통 응답, 예외 코드, 생성·수정 시각을 제공합니다.

화면은 Vue가 맡습니다. `views`가 화면, `api`가 서버 호출, `store/auth`가 로그인 상태입니다. 개발 서버는 `/api`를 `localhost:8080`으로 프록시합니다.

공개 API는 가입, 로그인, 이메일 중복 확인, 복지 목록·상세, AI 검색입니다. 프로필, 맞춤 목록, 수집은 토큰이 필요합니다.

---

## 📃 설계 문서

### 1. 스키마

테이블 정의는 [`db/DDL.sql`](db/DDL.sql)에 있습니다. 핵심 테이블은 다음과 같습니다.

+ `users`, `user_profile`: 회원과 프로필
+ `life_stage`, `household_type`, `interest`: 생애주기, 가구 형태, 관심 분야 코드
+ `welfare_service`: 복지 서비스 본문. 코드와의 연결은 `welfare_life_stage`, `welfare_household_type`, `welfare_interest`
+ `welfare_api`, `api_collection_history`: 수집 대상 API와 호출 이력

초기 코드와 Open API 주소는 [`db/DML.sql`](db/DML.sql)에 있습니다.

### 2. API 명세

springdoc으로 생성합니다. 백엔드 실행 후 `/swagger-ui.html`, OpenAPI JSON은 `/v3/api-docs`입니다.

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| POST | `/api/auth/signup` | 회원가입 | 공개 |
| POST | `/api/auth/login` | 로그인 | 공개 |
| GET | `/api/auth/email-check` | 이메일 중복 확인 | 공개 |
| GET | `/api/users/me/profile` | 내 프로필 조회 | 필요 |
| PUT | `/api/users/me/profile` | 내 프로필 저장 | 필요 |
| GET | `/api/welfare` | 복지 목록 | 공개 |
| GET | `/api/welfare/me` | 맞춤 복지 목록 | 필요 |
| GET | `/api/welfare/{id}` | 복지 상세 | 공개 |
| POST | `/api/ai/welfare/search` | AI 검색 | 공개 |
| POST | `/api/ingestion/welfare` | 중앙·지자체 수집 | 필요 |
| POST | `/api/ingestion/welfare/national` | 중앙부처 수집 | 필요 |
| POST | `/api/ingestion/welfare/local` | 지자체 수집 | 필요 |

---

## 프로젝트 구조

```text
project/
├── Backend/                 # Spring Boot API
│   └── src/main/java/com/welfare/
│       ├── user/            # 인증, 프로필, 맞춤 점수
│       ├── welfare/         # 복지 조회
│       ├── ai/              # OpenAI 검색
│       ├── ingestion/       # 공공 API 수집
│       ├── security/        # JWT, Security 필터
│       ├── config/          # JWT, CORS, OpenAI, Open API 설정
│       └── common/          # 공통 응답, 예외, 베이스 엔티티
├── Frontend/                # Vue 3 화면
│   └── src/
│       ├── views/           # 페이지
│       ├── components/      # 공통 컴포넌트
│       ├── api/             # Axios 호출
│       ├── store/           # Pinia
│       └── router/          # 라우트와 로그인 가드
├── db/
│   ├── DDL.sql              # 스키마
│   ├── DML.sql              # 코드·API 마스터
│   ├── dml/                 # 목록 조회용 테스트 데이터
│   └── migration/           # 기존 DB용 변경 스크립트
└── README.md
```

---

## 설치 및 실행 방법

JDK 17, Maven, Node.js, MySQL 8이 필요합니다.

### 1. 저장소 클론

```bash
git clone <저장소 주소>
cd project
```

### 2. 데이터베이스

```sql
CREATE DATABASE welfare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -u root -p welfare < db/DDL.sql
mysql -u root -p welfare < db/DML.sql
```

### 3. 환경 변수

백엔드 실행을 위해 설정이 필요한 환경변수입니다.

```bash
export DB_PASSWORD=비밀번호
export JWT_SECRET=임의의-비밀-문자열
export OPENAI_API_KEY=OpenAI-API-키
export WELFARE_OPEN_API_KEY=공공데이터포털-서비스키
```

### 4. 실행

```bash
# Backend
cd Backend
mvn spring-boot:run

# Frontend (새 터미널)
cd Frontend
npm install
npm run dev
```

+ 화면: [http://localhost:5173](http://localhost:5173)
+ API: [http://localhost:8080](http://localhost:8080)
+ Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

백엔드 테스트는 H2를 사용합니다.

```bash
cd Backend
mvn test
```
