# 🔴⚫ My FC Seoul - Backend

> **"오직, FC서울 팬들만을 위한 직관 아카이브 프로젝트"**

## 🔗 관련 링크

- **🚀 서비스 URL (Frontend)**: [https://fc-raichu.vercel.app/](https://fc-raichu.vercel.app/)
- **📽 시연 영상**: [Google Drive Link](https://drive.google.com/file/d/1SaK4jr9wzXs0hiHtA1mv-TAqgKGzTz3f/view?usp=sharing)
- **📊 발표 자료**: [Figma Deck](https://www.figma.com/deck/4Sn9B5XIb6OVuX72xdNVyU)

---

## 1. 프로젝트 소개

### 공통 (Frontend + Backend)
- My FC Seoul은 FC서울 팬이 직관 기록을 남기고, 시즌/경기 데이터를 기반으로 활동을 아카이빙하는 서비스입니다.
- 기록, 랭킹, 선수 후원, 경기 예측(베팅) 등 팬 경험을 확장하는 기능을 제공합니다.

### Backend 주요 내용
- Spring Boot 기반 API 서버로 인증/인가, 도메인 비즈니스 로직, 데이터 영속성을 담당합니다.
- 사용자/게시글/경기/선수/랭킹/베팅/후원 도메인을 중심으로 서비스 계층을 구성했습니다.

## 2. 개발 전략

### 공통 (Frontend + Backend)
- **브랜치 전략 (Git-flow)**
  - `main`: 배포 브랜치
  - `dev`: 통합 개발 브랜치
  - `feature/*`: 기능 단위 작업 브랜치
- **병렬 개발 전략**
  - API 명세 기준으로 프론트/백엔드 작업을 분리해 병렬 진행
  - 초기 단계에서 Mock 기반 개발(MSW)로 프론트 선행 개발 지원

### Backend 주요 내용
- **도메인 중심 설계**: `domain/*` 단위로 컨트롤러/서비스/리포지토리 책임 분리
- **보안 전략**: Keycloak(OAuth2 Resource Server) + Spring Security 기반 JWT 인증/인가
- **쿼리 전략**: Spring Data JPA + Querydsl로 동적/복잡 조회를 타입 안전하게 구성
- **성능 전략**: Caffeine Cache, Redis 활용(토큰/캐시/조회 최적화)

## 3. 채택한 개발 기술

### 공통 (Frontend + Backend)
- 협업: GitHub Issues, Notion, Discord, Figma
- 배포/운영: Frontend(Vercel), Backend(EC2/서버 환경)

### Backend 주요 내용
- **Language & Framework**: Java 17, Spring Boot 3
- **Persistence**: Spring Data JPA, Querydsl, MySQL 8
- **Security**: Spring Security, OAuth2 Resource Server, Keycloak
- **Cache & Infra**: Redis, Spring Cache, Caffeine, Actuator
- **API Docs**: springdoc-openapi (Swagger UI)
- **Build/Test**: Gradle, JUnit5, Spring Boot Test, Testcontainers

## 4. 프로젝트 구조 (Backend)

```text
src/main/java/com/fc/fcseoularchive
├── config                # 보안, 캐시, Querydsl, Swagger, 웹 설정
├── domain
│   ├── admin             # 관리자 기능 (경기/선수/베팅 정산)
│   ├── auth              # Keycloak 연동, 토큰 재발급
│   ├── bet               # 베팅/정산/히스토리
│   ├── donation          # 선수 후원
│   ├── game              # 경기 일정/상세 조회
│   ├── image             # 이미지 저장/압축
│   ├── player            # 선수 정보/포지션별 조회
│   ├── post              # 직관 기록 CRUD
│   ├── rank              # 직관왕/승률왕 랭킹
│   └── user              # 회원 가입, 내 정보, 닉네임 변경
├── global                # 공통 유틸, 예외 처리, 필터링
└── security              # 인증 사용자 식별 유틸
```

</br>

## 5. 역할 분담

|                                                                        **🍊 Frontend / Design**                                                                         |                                               **🎈 Backend**                                                |                                                 **😎 Backend**                                                  |
| :---------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------: |
|                             <img src="https://github.com/kye1115z.png" width="120" height="120"><br/>[@김예은](https://github.com/kye1115z)                             | <img src="https://github.com/wvwwvv.png" width="120" height="120"><br/>[@강상민](https://github.com/wvwwvv) | <img src="https://github.com/inwoohub.png" width="120" height="120"><br/>[@황인우](https://github.com/inwoohub) |
| **UI/UX**: 전체 서비스 와이어프레임 설계 및 브랜드 디자인 시스템 구축<br/>**핵심 기능**: 직관 기록 CRUD, 아카이브 필터링, MSW 연동, SVG 애니메이션 구현, 선수 후원 기능 |                **기능**: 도메인 설계, 직관 게시글 CRUD, 선수, 랭킹, 경기, 베팅 API 구현, 데이터베이스 모델링                |              **기능**: Keycloak 인증 서버 구축 및 연동, 회원 정보 관리 API 개발, 보안 프로토콜설정              |

<br/>

## 6. 주요 기능 (Backend API 관점)

### 공통 (Frontend + Backend)
- 사용자 관점 핵심 흐름: 로그인/회원가입 → 직관 기록 → 경기 일정 확인 → 베팅/후원 → 랭킹 확인

### Backend 주요 내용
- **User/Auth**
  - 회원가입, 내 정보 조회(출석 연동), 닉네임 변경
  - Keycloak 인가코드 교환/AccessToken 재발급
- **Post**
  - 직관 기록 생성/수정/삭제/상세 조회 (이미지 업로드 포함)
- **Game/Player**
  - 월별 경기 일정 조회, 게스트 조회, 경기 상세 조회
  - 선수 전체/포지션별/활동 상태 기반 조회
- **Bet/Donation/Rank**
  - 베팅 생성/히스토리/미확인 정산 결과 조회 및 확인 처리
  - 선수 후원 포인트 반영
  - 시즌별 직관왕/승률왕 랭킹 조회
- **Admin**
  - 회원/게시글 조회, 경기/선수 관리, 경기 결과 입력 + 베팅 정산

## 7. 신경 쓴 부분 (Trouble Shooting)

### 공통 (Frontend + Backend)
- 프론트와 백엔드 간 개발 속도 차이를 줄이기 위해 API 계약 중심으로 협업하고, 병렬 개발 체계를 유지했습니다.

### Backend 주요 내용
- **인증 연동 안정화**
  - OAuth2 Resource Server 설정과 권한 매핑(ROLE_USER/ROLE_ADMIN) 분리
  - 인증 예외를 글로벌 핸들러로 일관 처리
- **조회/정산 로직 복잡도 관리**
  - Querydsl로 랭킹/베팅 관련 복잡 조회를 구조화
  - 관리자 경기 결과 반영 시 베팅 정산 흐름을 서비스 레이어에서 통합 관리
- **성능/운영 대응**
  - 캐시(선수 조회 등) 적용 및 Actuator로 운영 상태 확인

## 8. 프로젝트 후기

### 공통 (Frontend + Backend)
- 프론트/백엔드가 기능 우선순위를 함께 조정하며, 사용자 흐름 중심으로 서비스를 완성했습니다.

### Backend 주요 내용
- 인증/도메인/운영 기능을 한 저장소에서 일관된 구조로 관리하면서 확장 가능한 서버 기반을 확보했습니다.
- 향후 개선 과제로는 테스트 안정화, 운영 환경 분리(local/dev/prod), API 문서 고도화가 있습니다.

---

## 로컬 실행 가이드 (Backend)

1. **필수 환경**
   - Java 17
   - MySQL
   - Redis
2. **실행**
   ```bash
   ./gradlew bootRun
   ```
3. **테스트**
   ```bash
   ./gradlew test --console=plain
   ```
4. **API 문서**
   - `http://localhost:8080/swagger-ui/index.html`
