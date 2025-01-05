
어뷰징 차단기는 하단 참고 </br>
<a href="https://github.com/sh970901/traffic-abuse">어뷰징 차단기</a>

<h2>Traffic Gateway</h2> 유입 제어

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
1. Limiter-Api
2. Limiter-Batch
3. Limiter-Order-API
4. Consul 
5. Redis 
```

<h4> Docker</h4>

```

```

<h4>Docker Compose</h4>
docker-compose.yml

```

```

<h4>Helm Charts</h4>

```
```


---
<h3>Application Architecture</h3>

![limiter-api-aa](https://github.com/user-attachments/assets/2a56c694-5e4a-4252-8bf4-2654818ae4ab)



---

<h3>유입 제어</h3>

<h4> Request </h4>
gateId: 생성한 GateID 값 </br>
userId: 사용자 식별 값 (예시 lib/core/AbstractDefaultWebGate 참고)

```
GET /api/v1/limiter/{gateId}/{userId}
```


<h4> Response </h4>

```
{
    "resultCode": "200",
    "resultMessage": "success",
    "data": {
        "order": 0,
        "message": "접속 완료"
    }
}
```
```

{
    "resultCode": "200",
    "resultMessage": "success",
    "data": {
        "order": 1,
        "message": "대기열 진입"
    }
}
```
---
<h3>순번 확인</h3>

<h4> Request </h4>
gateId: 생성한 GateID 값 </br>
userId: 사용자 식별 값 (예시 lib/core/AbstractDefaultWebGate 참고)

```
GET /api/v1/limiter/order/{gateId}/{userId}
```

<h4> Response </h4>

```
{
    "resultCode": "200",
    "resultMessage": "success",
    "data": 1
}
```
---
<h3>Consul 설정</h3> 

<h4>key</h3> 

```
config/{application-name}/gate/gateA/{gatename}
```
<h4>value</h3> 

```
{
    "GateId": "생성할 게이트 ID 값",
    "GateName": "B 경로",
    "ServiceId": 2,
    "GateTps": 5
}
```
---
<h3>적용</h3>

적용은 AOP, Interceptor, Filter 등 다양하게 활용 가능

어노테이션을 활용한 간단 적용 (예시 lib/aop/TrafficLimiterAspect 참고)
```
@GetMapping("/")
@TrafficLimiter(waitingPagePath = "대기 시 노출할 페이지 Path", gateId = "{고유한 게이트 ID 값}")
public String main(){
    return "limiter/home";
}
```

---
결과 화면

1초에 5번의(GateTps) 요청 발생 시 대기

사용자에게 대기 순번을 표시하고 차례대로 접근

![Limiter-api-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/12ef4b83-ff53-42cc-8144-2b44052fe517)

한번 유입된 사용자는 지정한 시간(TTL)동안 대기없이 접근 가능
![Limiter-api-order-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/bf037c2a-ce48-4179-b889-de65ed1d0501)
