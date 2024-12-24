
어뷰징 차단기는 하단 참고 </br>
<a href="https://github.com/sh970901/traffic-abuse">어뷰징 차단기</a>

<h2>Limiter-API</h2> 유입 제어

---
<h3>Introduction</h3>
<p>개요</p>

- 예측 불가능한 유입에 대한 서비스 가용성 확보 
- 동시 접속(이벤트, 쿠폰 발급 등) 폭주 방지

<p>개발</p>

(1) 요구
- 특정 과도한 트래픽이 발생하기 전까지는 트래픽 허용 
- 트래픽 과부하 시 사용자 요청을 순차적으로 처리
- 한번 유입된 사용자는 지정한 시간(TTL)동안 대기없이 진입 가능
- 트래픽 과부하에 대한 기준 설정 가능
- 사용자는 실시간으로 자신의 순서를 확인

(2) 설계
- JDK 23, Gradle, SpringBoot 3.4.0, Spring MVC, Spring WebFux, Spring Scheduler, Redis, Consul DB
- <a href="https://www.baeldung.com/spring-bucket4j">Spring bucket4j</a>를 활용하여 유입량(TPS) 관리, 분산 환경 처리를 위해 Bucket을 레디스로 관리 
- 사용자를 순차적으로 처리,  <a href="https://redis.io/docs/latest/commands/zscore/">Redis SortedSet</a> 을 Queue 처럼 활용하고 Score로 사용자 순서(순번) 출력 가능
- GATE ID 별 TPS 제한 등의 설정 값은 <a href="https://cloud.spring.io/spring-cloud-consul/reference/html/">Spring Cloud Consul</a>을 활용, 변경 값에 대한 실시간 적용


<p>효과</p>

- 시스템 처리량 과부하 방지로 인한 서버 다운타임 예방
- 리소스 보호
- 스케일링 비용 절감
- 사용자 경험 개선(응답 시간 안정화, 에러 발생 감소 등)


<p>기타</p>

- 어노테이션 기반 간단한 유입 제어 적용 


<p> 추가 개발 예정 </p>

- DB CPU 등 추가적인 필터링을 통한 유입 제어 적용 
- 비정상 요청 및 정책 기반 필터링
- 사용자 백오피스 개발
- 버전 별 라이브러리 개발

---
<h3>DownLoad</h3>

```
1. Limiter-API
2. Limiter-Batch
3. Limiter-Order-API
4. Consul 
5. Redis 
```

<h4> Docker</h4>

```
1. 
```

<h4>Docker Compose</h4>
1. docker-compose.yml
```

```

<h4>Helm Charts</h4>

```
```


---
<h3>Application Architecture</h3>

![limiter-api-aa](https://github.com/user-attachments/assets/2a56c694-5e4a-4252-8bf4-2654818ae4ab)



---


<h3> Request Data </h3>

```

```

---

<h3> Response Data </h3>

```
```
---

---
