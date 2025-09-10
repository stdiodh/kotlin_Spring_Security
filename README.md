# 8~9주차 통합 - 🚀 EC2 & Docker 기반 배포 자동화 (CI/CD) 정리

### 1. 왜 내 PC로 서버를 운영하면 안될까?
로컬에서 완벽하게 동작하던 API 서버도, 실제 서비스로 공개하기 위해 개인 PC를 서버로 사용하면 여러 문제에 직면하게 됩니다.

- **유동 IP:** IP 주소가 계속 바뀌어 도메인 연결이 끊어질 수 있습니다.
- **안정성:** 24시간 365일 PC를 켜둘 수 없으며, 정전이나 인터넷 끊김 등 위험이 많습니다.
- **보안:** 해킹 시도의 표적이 되기 쉽고, 중요한 정보가 유출될 수 있습니다.
- **성능 및 확장성:** 사용자가 몰리면 서버가 다운되고, 유연한 성능 업그레이드가 어렵습니다.

---

### 2. 해결책 (1): 클라우드 서버 (AWS EC2)
> AWS에서 제공하는 원격 제어 가능 **가상 서버 대여 서비스**입니다.

거대한 데이터 센터의 컴퓨터 자원을 잘게 쪼개어 나만의 서버(인스턴스)를 빌려 쓰는 개념으로, 앞선 4가지 문제점을 모두 해결할 수 있습니다.
- **프리티어:** 신규 가입 시 1년간 `t2.micro` 사양의 서버를 월 750시간 무료로 사용할 수 있어, 24시간 운영해도 과금되지 않습니다.

---

### 3. 새로운 문제: "제 컴퓨터에선 잘 됐는데요?"
서버를 준비해도, 내 PC와 서버의 환경(Java 버전, 라이브러리 등)이 달라 배포 시 각종 오류가 발생할 수 있습니다. <br>이러한 **"환경 불일치"** 문제는 배포 실패의 주된 원인입니다.

---

### 4. 해결책 (2): 개발 환경 패키징 (Docker)
<img width="3000" height="3000" alt="image" src="https://github.com/user-attachments/assets/8b9180a2-1288-43a0-8ec6-d7f44ee3b545" />


> 애플리케이션과 실행에 필요한 모든 환경을 **컨테이너**라는 표준화된 상자에 담아, 어디서든 동일하게 실행시키는 기술입니다.

- **Dockerfile (설계도):** 컨테이너 생성 방법을 정의한 텍스트 파일입니다.
- **Image (실행 파일):** Dockerfile을 바탕으로 만들어진, 실행에 필요한 모든 것을 포함한 불변의 소프트웨어 패키지입니다.
- **Container (실행된 상태):** 이미지를 메모리에 올려 실제로 실행한 상태로, 우리가 사용하는 애플리케이션 그 자체입니다.



---

### 5. 마지막 과제: 반복적인 수동 배포
Docker를 사용해도 코드를 수정할 때마다 아래 과정을 반복하는 것은 비효율적이고 실수할 가능성이 높습니다.
1.  로컬에서 코드 수정 후 `git push`
2.  터미널로 EC2 서버에 접속 (SSH)
3.  Docker 이미지 다시 빌드 (`docker build`)
4.  기존 컨테이너 중지 (`docker stop`)
5.  새로운 이미지로 컨테이너 실행 (`docker run`)

---

### 6. 해결책 (3): 배포 자동화 (CI/CD)
> 코드를 Push하는 순간, **빌드-테스트-배포**가 자동으로 이루어지는 파이프라인입니다.

- **CI (Continuous Integration, 지속적 통합):** 여러 개발자의 코드를 주기적으로 통합하고, 자동으로 빌드 및 테스트하여 코드 품질을 유지하는 과정입니다.
- **CD (Continuous Deployment, 지속적 배포):** CI를 통과한 코드를 실제 운영 서버까지 자동으로 배포하여, 빠르고 안전하게 새로운 기능을 전달하는 과정입니다.

---

### 7. GitHub Actions를 활용한 CI/CD 파이프라인 구축
<img width="2360" height="1326" alt="image" src="https://github.com/user-attachments/assets/d5ea8bb2-0c88-4487-be27-aacfae4d049c" />

> `main` 브랜치에 코드를 `push`하면 GitHub Actions가 아래의 흐름으로 자동 배포를 진행합니다.



1.  **[GitHub Actions]** 코드 변경을 감지하고 CI/CD 작업 시작.
2.  **(CI)** 프로젝트 코드 빌드 및 테스트 실행.
3.  **(CD)** Dockerfile로 Docker 이미지 빌드.
4.  **(CD)** 생성된 이미지를 Docker Hub(이미지 저장소)에 업로드.
5.  **(CD)** SSH로 EC2 서버에 원격 접속.
6.  **(CD)** EC2 서버에서 최신 이미지를 받아와 컨테이너 실행.

---

## 📌 실습
- Spring Boot 프로젝트 생성하여 GitHub Actions를 통해 EC2에 Docker 컨테이너로 자동 배포하는 전체 파이프라인 구축
- AWS 자격 증명, Docker Hub 계정 정보 등 민감한 정보를 GitHub Secrets에 등록하여 안전하게 관리하기

---

## 📌 참고 링크
- [EC2에 본인의 API 서버 업로드하기 - 이론 (Docker, CI/CD)](https://velog.io/@stdiodh/aws-EC2%EC%97%90-%EB%B3%B8%EC%9D%B8%EC%9D%98-API-%EC%84%9C%EB%B2%84-%EC%97%85%EB%A1%9C%EB%93%9C%ED%95%98%EA%B8%B0-%EC%9D%B4%EB%A1%A0-Docker-CICD)
- [Github Actions으로 CI/CD 경험하기 (Spring boot, EC2, Docker)](https://jojoldu.tistory.com/539)
- [AWS EC2에 Docker 사용하여 Spring Boot 배포 자동화하기](https://velog.io/@leyuri/AWS-EC2%EC%97%90-Docker-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-Spring-Boot-%EB%B0%B0%ED%8F%AC-%EC%9E%90%EB%8F%99%ED%99%94%ED%95%98%EA%B8%B0)
