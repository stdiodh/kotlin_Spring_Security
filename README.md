# 7주차 - ☁️ AWS S3 업로드 정리

### 1. 온라인 객체 스토리지(Online Object Storage)
> 데이터를 객체 단위로 저장하는 서비스 (ex. 구글 드라이브).  
HTTP/HTTPS API를 통해 접근, 어디서나 저장·조회 가능.

---

### 2. S3의 개념
<img width="700" height="550" alt="image" src="https://github.com/user-attachments/assets/a7f3835e-d8d3-43e5-b0a9-7e3366d2c2b8" />

- **버킷(Bucket)**: 객체를 담는 공간 (폴더 개념)
- **객체(Object)**: 파일 + 메타데이터 (저장 단위)

<img width="2000" height="760" alt="image" src="https://github.com/user-attachments/assets/960bb130-64dd-4915-abe1-5576ac5e8f57" />
- URL 형식:  
http://[BucketName].[Region].amazonaws.com/object.key

---

### 3. S3 업로드 방식

<img width="2500" height="1406" alt="image" src="https://github.com/user-attachments/assets/6f4e9b42-7a1d-426c-8626-41e0de9a25ae" />


#### (1) 스트림 업로드 (Stream Upload)
- 한 번의 API 호출로 한 파일 업로드
- 메모리 사용 적음 → 대용량에 적합
- 업로드 속도 저하 주의

<img width="2500" height="1406" alt="image" src="https://github.com/user-attachments/assets/acbc75b7-856a-497a-9002-19eb3dd0d660" />


#### (2) MultipartFile 업로드
- 여러 데이터를 한 요청에 담아 전송
- Spring 제공 객체 → 파일 다루기 편리
- 단점: 메모리/임시 디스크 사용 → 요청 많으면 OOM 위험

<img width="2500" height="1406" alt="image" src="https://github.com/user-attachments/assets/07d9691e-92a4-4718-85d2-c9a004b9a5b0" />


#### (3) AWS 멀티파트 업로드 (Multipart Upload)
- 파일을 여러 Part로 나눠 병렬 업로드
- 실패 시 해당 Part만 재전송 → 안정적
- 대용량 업로드 최적화

**프로세스:**
1. 업로드 시작 → 업로드 ID 발급
2. PresignedURL 발급
3. Part 단위 업로드
4. 업로드 완료 요청

---

### 4. S3 요금 (프리티어 기준)
- 5GB 스토리지 무료
- GET 요청 20,000건 / PUT 요청 2,000건 무료
- 불필요한 파일 방치 시 과금 발생 주의

---
## 📌 실습

따로 velog에 정리해두었으니 참고바람.
- [AWS S3 bucket을 이용한 image upload 실습 (1)](https://velog.io/@stdiodh/Spring-Boot-AWS-S3-bucket%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-image-upload-%EC%8B%A4%EC%8A%B5-1)
- [AWS S3 bucket을 이용한 image upload 실습 (2)](https://velog.io/@stdiodh/Spring-Boot-AWS-S3-bucket%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-image-upload-%EC%8B%A4%EC%8A%B5-1-wwg45olm)
---

## 📌 참고 링크
- [[AWS] 📚 S3 개념 & 버킷 · 권한 설정 방법](https://inpa.tistory.com/entry/AWS-%F0%9F%93%9A-S3-%EB%B2%84%ED%82%B7-%EC%83%9D%EC%84%B1-%EC%82%AC%EC%9A%A9%EB%B2%95-%EC%8B%A4%EC%A0%84-%EA%B5%AC%EC%B6%95)
- [Spring Boot에서 S3에 파일을 업로드하는 세 가지 방법](https://techblog.woowahan.com/11392/)
- [AWS S3 bucket을 이용한 image upload 이론](https://velog.io/@stdiodh/Spring-Boot-AWS-S3-bucket%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-image-upload-%EC%9D%B4%EB%A1%A0)
