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

| **🍊 Frontend / Design** | **🎈 Backend** | **😎 Backend** |
| :----------------------: | :-----------: | :-----------: |
| <img src="https://github.com/kye1115z.png" width="120" height="120"><br/>[@김예은](https://github.com/kye1115z) | <img src="https://github.com/wvwwvv.png" width="120" height="120"><br/>[@강상민](https://github.com/wvwwvv) | <img src="https://github.com/inwoohub.png" width="120" height="120"><br/>[@황인우](https://github.com/inwoohub) |
| **UI/UX**: 전체 서비스 와이어프레임 설계 및 브랜드 디자인 시스템 구축<br/>**핵심 기능**: 직관 기록 CRUD, 아카이브 필터링, MSW 연동, SVG 애니메이션 구현, 선수 후원 기능 | **기능**: 도메인 설계, 직관 게시글 CRUD, 선수, 랭킹, 경기, 베팅 API 구현, 데이터베이스 모델링 | **기능**: Keycloak 인증 서버 구축 및 연동, 회원 정보 관리 API 개발, 보안 프로토콜설정 |

<br/>

## 6. 페이지별 주요 기능

| **랜딩 페이지 (Landing)** | **로그인 (Login)** | **회원가입 (Signup)** |
| :----------------------: | :---------------: | :------------------: |
| ![랜딩페이지](https://private-user-images.githubusercontent.com/78716896/572218299-686af908-eeb6-4c76-bb6f-b2fa3d03b14d.GIF?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDQyNzYsIm5iZiI6MTc3NTAwMzk3NiwicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTgyOTktNjg2YWY5MDgtZWViNi00Yzc2LWJiNmYtYjJmYTNkMDNiMTRkLkdJRj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAwMzkzNlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWNmNjQ2ODU0ZmY0ZmE4ZDY4OWUzMzZjMmQzNDY5Nzc2ZDRhYWVkMTczMjI5ZjI5N2ViYTA3NzhlMmM1NWM2YzAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.-3jSQDQiIadZRx68zsKgu9Gqlp7MxnYufyMPDuVp-lo) | ![로그인](https://private-user-images.githubusercontent.com/78716896/572218300-0c3928c3-d7b2-4215-a3c3-d7b4f5319573.GIF?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDQyNzYsIm5iZiI6MTc3NTAwMzk3NiwicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTgzMDAtMGMzOTI4YzMtZDdiMi00MjE1LWEzYzMtZDdiNGY1MzE5NTczLkdJRj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAwMzkzNlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTA0MGZmYWJjYWM5OTk0NzgwMmUxNTE3NThlYzRiZWFlZTlhYjk0OTk0MDAxMzJjOTRkMjlkZTE3N2MwZTZjMTEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.JolbTcIRx1LW6kkANAzuiWbJH0odO3TRb4HhGYM9CSs) | ![회원가입](https://private-user-images.githubusercontent.com/78716896/572219561-bb7751ca-6a80-404b-8a75-ee28796d97d0.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDU5MjksIm5iZiI6MTc3NTAwNTYyOSwicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTk1NjEtYmI3NzUxY2EtNmE4MC00MDRiLThhNzUtZWUyODc5NmQ5N2QwLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAxMDcwOVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWYyN2M2Y2IzOTliZTM5ZDMyZGFkN2E1NjVhOGNmY2Q2NDM4NGJjN2Y0MDk3MjNjYmQwMDU4MjNkZjI4MGJhZjYmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.FxCyBOUlOg8wXRFjIQZnUWyWdkYN1UeZFZY1gtV9Gk4) |
| 브랜드 애니메이션 제공 | K League 팀 로고 애니메이션 및 입력폼 | 이메, 비밀번호, 닉네임 입력 |

| **직관 기록(Record)** | **직관 상세(Detail)** | **경기 일정(Calendar)** |
| :------------------: | :------------------: | :--------------------: |
| ![직관기록](https://private-user-images.githubusercontent.com/78716896/572218804-5d9f6574-3047-4f50-9882-84a55081f652.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDU5MjksIm5iZiI6MTc3NTAwNTYyOSwicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTg4MDQtNWQ5ZjY1NzQtMzA0Ny00ZjUwLTk4ODItODRhNTUwODFmNjUyLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAxMDcwOVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWI5ZDBlOTdkODliYzY0MGM2ODY2NWQzM2UwYjRiMDMwMTE3ZTZkYWFmNmVkNjZiNjFlZmE0ZjA3YzQ2MTQyOGYmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0._lxm7RGS_y0hU4DQBZBFN3hna6x6e73BIGBOzqm5xOs) | ![직관 상세](https://private-user-images.githubusercontent.com/78716896/572218805-9f45c4c7-d65e-4716-8958-10a65941c74c.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDU5MjksIm5iZiI6MTc3NTAwNTYyOSwicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTg4MDUtOWY0NWM0YzctZDY1ZS00NzE2LTg5NTgtMTBhNjU5NDFjNzRjLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAxMDcwOVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWQ0ZGEyMTNlM2ZkOTQxZjU5Yzk5ZjEwYzlkOTc4YzRlM2IzM2YxYzA3OTBiMDYxYTYwODI3YzA1NTU0MWNjZjcmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.K0SUnQdMI8OWUCWlzfJNPaHfMIj5ScLVQQK8qVpHUh0) | ![경기 일정](https://private-user-images.githubusercontent.com/78716896/572218802-2577f60f-5fcb-4781-bc2a-d11ab358c6ad.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDYxMTMsIm5iZiI6MTc3NTAwNTgxMywicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTg4MDItMjU3N2Y2MGYtNWZjYi00NzgxLWJjMmEtZDExYWIzNThjNmFkLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAxMTAxM1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWIzNzA1ODA0NGVhNjk0OGY5OGExZGNmMDliMDM4MDRjZjM4OTU5NTI0NTI1MTg3ZWRjNmY4YWNjMjkyNzI0ODgmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.7BCV6Pnvc7SH1FLtb9TPj8oG0FzmofeHCbyc18PZaLQ) |
| 경기 정보, 사진 등을 담은 직관 기록 작성 | 텍스트, 슬라이더 이미지로 구성한 UI | 경기 일정 및 나의 직관 경기를 달력으로 제공 |

| **랭킹(Ranking)** | **선수 후원(Donation)** | **베팅(Betting)** |
| :---------------: | :-------------------: | :---------------: |
| ![랭킹](https://private-user-images.githubusercontent.com/78716896/572218800-c590244e-47f6-43b2-8e33-3cec95c094eb.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDYzMTcsIm5iZiI6MTc3NTAwNjAxNywicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTg4MDAtYzU5MDI0NGUtNDdmNi00M2IyLThlMzMtM2NlYzk1YzA5NGViLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAxMTMzN1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTk2NTFkM2RjYTdjZGM1NTJmODhjYmFkYTE4NWU3NzNhMDAwNTYwMWJhYjc1ODE4ZjU5MDU2MTFhNDA1MTUwYTImWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.wJ04Z1rhqS6J5DBU_dAPjR24a2ZWUmYhNjIXRV0ilIU) | ![선수 후원](https://private-user-images.githubusercontent.com/78716896/572218298-a1a6dcc5-1eb3-49bb-b499-3ecf7b9f78f7.GIF?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NzUwMDY1MzcsIm5iZiI6MTc3NTAwNjIzNywicGF0aCI6Ii83ODcxNjg5Ni81NzIyMTgyOTgtYTFhNmRjYzUtMWViMy00OWJiLWI0OTktM2VjZjdiOWY3OGY3LkdJRj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNjA0MDElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjYwNDAxVDAxMTcxN1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTVmYjkyYTJkOTk4ODUwNzhlNDczNzhlZTA1MjExMzc1ZjFlNTI1NjNkZDM1Y2JjYjdlNzRmNjhlMWUzM2IyNjQmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.w7ZdzYdu63BgdUknY0A3B2WBD9eEJ2iHv7MGfZlADo8) | ![베팅](https://dummyimage.com/900x520/0f172a/ffffff&text=Betting+Page) |
| 직관 기록 및 승률 기반 랭킹 시스템 | 포인트로 좋아하는 선수에게 후원 | 경기 예측 및 베팅 내역 확인 |

<br/>


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
