# 🎓 내일 (Nae-Il) LMS  
> 당신의 오늘이 나의 일이 되는 곳, 통합 학습 관리 시스템

---

## 📖 Project Overview  
**내일(Nae-Il) LMS**는 학습자, 강사, 관리자 간의 상호작용을 하나의 흐름으로 연결하기 위해 설계된 통합 교육 관리 시스템입니다.  

단순 강의 제공 플랫폼을 넘어,  
**강의 탐색 → 결제 → 수강 → 학습 관리 → 정산**까지 이어지는  
전체 교육 비즈니스 흐름을 하나의 시스템으로 통합하였습니다.

---

## 🎯 Problem Definition  

기존 LMS 및 온라인 강의 플랫폼의 한계:

- 강의 품질 검증 없이 누구나 강의 등록 가능  
- 결제, 수강, 정산 흐름이 분리되어 있음  
- 구독 / 단건 결제 구조가 분리되어 사용자 경험이 복잡함  
- 관리자 개입 없이 운영되어 서비스 신뢰도 저하  

👉 본 프로젝트는 이를 해결하기 위해  
**“검증 기반 + 통합 흐름형 LMS”**를 목표로 설계되었습니다.

---

## 🧩 Key Features  

### 🔐 Authentication  
- 로그인 / 회원가입  
- Spring Security 기반 인증 처리  

### 👤 User Management  
- 사용자 정보 조회 및 수정  
- 강사 신청 및 관리자 승인 시스템  

### 📚 Course Management  
- 강의 등록 및 수정 (강사)  
- 주차별 강의(Session) 관리  
- 강의 조회 및 상세 페이지 제공  

### 🛒 Cart & Payment  
- 장바구니 기능 및 바로 결제  
- 크레딧 기반 결제 시스템  
- 결제 상태 관리 (READY / SUCCESS / FAILED)  

### 💳 Subscription System  
- 구독권 (1개월 / 12개월)  
- 구독 시 강의 무료 수강 (최대 3개)  
- 실시간 강의 선예약 기능  

### 🎓 Learning Management  
- 내 강의 목록 조회  
- 강의 영상 시청 및 진도율 관리  

### 📡 Live Class  
- 실시간 강의 개설 및 예약  
- 예약자 관리 기능  

### 🏦 Settlement System  
- 강사 정산 신청  
- 관리자 승인/반려 처리  
- 정산 상태 관리  

### 🛠️ Admin System  
- 강의 승인/반려  
- 회원 및 블랙리스트 관리  
- 정산 관리 및 통계 확인  

---

## 🧠 Key Design Concept  

### 1. Role-Based Structure  
사용자 / 강사 / 관리자 역할 분리  
→ 각 역할별 UI 및 기능 흐름 분리

---

### 2. Quality Control Workflow  
강의 등록 → 관리자 승인 → 서비스 노출  

👉 무분별한 강의 등록 방지 및 품질 보장  

---

### 3. Hybrid Payment Model  
- 단건 구매 + 구독 모델 동시 지원  
- 구독 시 일부 강의 무료 제공  

👉 사용자 선택 유연성 확보  

---

### 4. Data Consistency  
- Enum 기반 상태 관리  
- 내부 데이터 표준화  

👉 데이터 정합성 및 확장성 확보  

---

## 🏗️ System Architecture  

- Spring Boot 기반 웹 애플리케이션  
- MVC 패턴 (Controller / Service / Repository)  
- 계층 분리 설계  

---

## 🛠️ Tech Stack  

- **Backend**: Java, Spring Boot, Spring Security, JPA  
- **Database**: MySQL  
- **Frontend**: Thymeleaf, HTML, CSS  
- **Build Tool**: Gradle  
- **Version Control**: Git, GitHub  

---

## 📂 Project Structure  

```bash
src
 └── main
     ├── java/com/wanted/naeil
     │    ├── domain
     │    │    ├── user
     │    │    ├── course
     │    │    ├── learning
     │    │    ├── payment
     │    │    ├── live
     │    │    └── admin
     │    │
     │    └── global
     │         ├── config
     │         ├── auth
     │         └── common
     │
     └── resources
          ├── templates
          ├── static
          └── application.yml
