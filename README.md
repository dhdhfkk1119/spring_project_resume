
제목
---
# ㅁㄴㅇㄹ
## ㅁㄴㅇㄹ
### ㅁㄴㅇㄹ
#### ㅁㄴㅇㄹ
---
# 📝 이력서(Resume) 및 경력(Career) 기능 명세

> 담당자: 조충희

## 1. 개요

이 프로젝트의 핵심 기능인 **이력서 및 경력 관리** 파트입니다.

사용자는 자신의 이력서를 생성, 조회, 수정, 삭제할 수 있으며, 각 이력서에 종속되는 상세 경력 정보들을 관리할 수 있습니다. 모든 기능은 **로그인 기반**으로 동작하며, **자신이 작성한 이력서에만 접근**할 수 있도록 제어됩니다.

## 2. 주요 기능 목록

-   [x] 이력서 목록 조회
-   [x] 이력서 상세 조회 (경력 정보 포함)
-   [x] 이력서 작성 및 저장
-   [x] 이력서 수정
-   [x] 이력서 삭제
-   [x] 이력서 소유자 확인을 통한 인가 처리

## 3. 화면-API 매핑

| 화면 (기능)             | HTTP Method | URL                               | Controller Method         | `@Auth` 필요 여부 |
| ----------------------- | ----------- | --------------------------------- | ------------------------- | ----------------- |
| 이력서 목록 페이지        | `GET`       | `/resume`                         | `resumeList()`            | O                 |
| 이력서 상세 페이지        | `GET`       | `/resume/{resumeIdx}`             | `resumeDetail()`          | O                 |
| 이력서 작성 페이지        | `GET`       | `/resume/save-form`               | `resumeSaveForm()`        | O                 |
| **이력서 작성 처리**      | `POST`      | `/resume/save`                    | `resumeSave()`            | O                 |
| 이력서 수정 페이지        | `GET`       | `/resume/{resumeIdx}/update-form` | `resumeUpdateForm()`      | O                 |
| **이력서 수정 처리**      | `POST`      | `/resume/{resumeIdx}/update`      | `resumeUpdate()`          | O                 |
| **이력서 삭제 처리**      | `POST`      | `/resume/{resumeIdx}/delete`      | `resumeDelete()`          | O                 |

![list](https://github.com/user-attachments/assets/983ff860-5186-4fce-bc11-328cf71a91c1)
![detail](https://github.com/user-attachments/assets/c01a650a-e2e0-409a-bf12-ed578e37ac15)
![corp](https://github.com/user-attachments/assets/68937970-70df-4c99-8803-964576096c0a)
![save](https://github.com/user-attachments/assets/08ea6f46-b2c7-4833-87fc-2a2eac31adc8)
![update](https://github.com/user-attachments/assets/8fcf770a-18c6-45f8-9184-9e06ecde267f)

## 4. 핵심 로직 및 설계 결정

### 가. 이력서 소유권 검증: `Resume.isOwner()`

-   **목적**: 특정 이력서가 현재 로그인한 사용자의 소유인지 명확하게 판단하기 위해 `Resume` 엔티티 내부에 `isOwner(memberIdx)` 메서드를 구현했습니다.
-   **동작**: `Service` 계층에서 이력서 수정 및 삭제 로직을 수행하기 전, 이 메서드를 호출하여 권한을 검사합니다. 이를 통해 비즈니스 로직의 응집도를 높였습니다.

### 나. 데이터 무결성 유지: `cascade = CascadeType.REMOVE`

-   **목적**: 사용자가 이력서를 삭제할 때, 해당 이력서에 종속된 경력 데이터들이 DB에孤兒(Orphan) 데이터로 남는 것을 방지합니다.
-   **동작**: `Resume` 엔티티의 `careerList` 필드에 `cascade = CascadeType.REMOVE` 옵션을 적용했습니다. 이로써 JPA는 `Resume` 엔티티가 삭제될 때, 연관된 `Career` 엔티티들을 자동으로 함께 삭제하여 데이터의 일관성을 보장합니다.

### 다. 선언적 인증/인가 처리: `@Auth` 와 `AuthInterceptor`

-   **목적**: "로그인한 사용자만 접근 가능" 이라는 공통 보안 요구사항을 모든 컨트롤러 메서드에서 중복으로 구현하는 것을 피하기 위해 인터셉터 방식을 도입했습니다.
-   **동작**:
    1.  권한이 필요한 컨트롤러 메서드에 `@Auth` 어노테이션을 붙입니다.
    2.  `AuthInterceptor`는 컨트롤러가 실행되기 전 요청을 가로채, `@Auth` 어노테이션의 존재 여부를 확인합니다.
    3.  어노테이션이 있다면, 세션 정보를 확인하여 로그인 상태가 아니거나 권한이 없으면 `Exception403`을 발생시켜 접근을 차단합니다.
    4.  이를 통해 인증/인가 로직을 비즈니스 로직과 분리하여 코드의 가독성과 유지보수성을 향상시켰습니다.
---
