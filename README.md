![logotitle](https://user-images.githubusercontent.com/28921379/129575489-6a2d6f97-640f-4bc0-9039-ddcb37af1c4f.png)
<br>

## **1. 서비스 소개**
<br>

![Untitled](https://user-images.githubusercontent.com/28921379/117543950-09a0a400-b05a-11eb-8a2a-b5054fa86afa.png)

사진 출처 : 코메디 닷컴 - 공포영화 '좋은 스트레스'로 작용 (연구)

### **심박수 기반 영화 순위 제공서비스 'Movie by Heart'**
<br>

리뷰와 별점 외에 실시간으로 저장되는 심박수를 분석해서 사용자가 영화의 재미를 판단할 수 있도록 하는 새로운 지표를 제공해주는 서비스입니다. 

</br>

## **2. 서비스 이용 방법**
<br>

 **스마트폰 어플리케이션만 실행**

    Movie by Heart_mobile.apk 파일을 설치해서 실행
    
    * 동작되지 않는다면 'WearOS 기기도 실행' 수행

**WearOS 기기도 실행**

1. Android Studio 설치
2. Android Studio 에서 레포지토리를 clone 하여, Application 디렉토리를 프로젝트 폴더로 Import
3. 스마트폰 Application은 에뮬레이터(Oreo 이상) 설치 후, Run
4. WearOS Application은 WatchOS 기기의 블루투스 디버깅 모드 활성화
5. adb.exe 디렉토리에서 CLI 명령어 실행

    adb forward tcp:4444 localabstract:/adb-hub

    adb connect 127.0.0.1:4444

6. WearOS Run

<br>


서비스 이용시에는 어플리케이션의 회원가입을 수행 또는 아래에 데이터가 쌓인 계정을 사용하시면 됩니다!

    ID : garam0410
    PW : rlarkfka

</br>

**QR 인식**

1. QR 코드 생성 사이트 접속

2. JSON 형식으로 Text 작성

    {"title":"컨저링 3: 악마가 시켰다","date":"2021-08-05","time":"18:11:00~14:59:00"}

3. QR 생성 후, 어플리케이션 우측 상단의 QR 버튼으로 인식 수행

4. 측정 예약을 누르면, 해당시간에 자동으로 WearOS 기기로 신호를 보내서 측정 시작

<br>

## **3. 개발환경**
<br>

![image](https://user-images.githubusercontent.com/28921379/129545011-1ed7ae72-9fa4-452c-a09e-daf0995635fb.png)

<br>

🍎**FrontEnd**

- Android Studio

🍏**BackEnd**

- Spring Boot
- DRF(Django Rest Framework)
- MariaDB

🔳**DevOps**

- NCP(Naver Cloud Platform) - Compute Sever
- Jenkins
- Docker
- Git / GitHub
- Portainer (Container Log Tool)

</br>

## **4. DB 스키마**
<br>

![image](https://user-images.githubusercontent.com/28921379/129549222-35322337-8a42-46b4-9361-d0e68187fb28.png)

<br>

- **USERINFO** : 사용자 정보를 저장하는 테이블

- **MOVIEINFO** : 영화 정보를 저장하는 테이블

- **MOVIECOMMENT** : 영화 댓글을 저장하는 테이블

- **MOVIELOVE** : 사용자별 영화 좋아요 상태를 저장하는 테이블

- **MOVIERANK** : 분석을 통해 도출된 영화 순위를 저장하는 테이블

- **BPMTEST** : 심박수 측정한 사용자 목록

- **BPMDATA** : 사용자 및 영화별 심박수 데이터

- **SCORING** : 분석을 통해 도출된 영화 별 스코어

</br>

## **5. 서비스 구성**
<br>

![image](https://user-images.githubusercontent.com/28921379/129558739-3091e98e-81d9-4c44-9844-4433dd97e3d8.png)

<br>

- ### **[사용자 관리 서비스](https://github.com/garam0410/OIDC_User_Management_Service)**
    - 로그인

    - 회원가입
    - ID/PW 찾기
    
<br>

- ### **[영화 정보 서비스](https://github.com/garam0410/OIDC_Movie_Information_Service)**
    - 영화 데이터 파싱

    - 영화 상세정보 제공

    - 영화 랭킹 데이터 제공

    - 영화 좋아요

    - 관람한 영화 및 좋아요 한 영화 정보 제공

<br>

- ### **[소셜 서비스](https://github.com/garam0410/OIDC_Social_Service)**
    - 댓글 추가

    - 댓글 수정

    - 댓글 삭제

    - 영화 별 댓글 리스트 제공

    - 심박수 처리

<br>

- ### **[분석 서비스](https://github.com/garam0410/OIDC_Analysis_Service)**
    - 아웃라이어 알고리즘을 이용한 심박수 데이터 분석

    - 클러스터링 및 협업필터링 알고리즘을 이용한 사용자 추천

</br>

## **6. 기능**
<br>

![image](https://user-images.githubusercontent.com/28921379/129663798-f5f2f67d-f5bb-48f5-b442-15b9034d8f2c.png)

- 로그인 및 회원가입 : 분석을 위한 사용자 정보 제공 및 소셜 기능을 위한 절차

- 홈화면 : 심박수 분석을 통해 도출된 영화 순위 및 협업필터링을 활용한 사용자 맞춤 영화

- 검색 서비스 : 영화 정보를 제공해 주기 위한 검색 서비스

- 마이페이지 : 사용자가 좋아요 누른 영화 및 측정한 영화 목록, 측정 예약 영화 보기 가능

- 영화 상세정보 : 영화 상세정보 및 영화를 본 사람의 심박수 최대/최소 정보 및 측정자 수 제공, 리뷰(댓글) 시스템 적용 

<br><br>

![image](https://user-images.githubusercontent.com/28921379/129664586-224e1330-3625-4af7-9e6f-6ac0cba4e5df.png)

- QR 코드 인식을 통해, 영화 데이터 추출 및 측정 예약

- 측정 예약 영화는 마이페이지에서 확인 가능 및 취소 가능

- 시간이 되면, 스마트워치에 측정 시작 신호를 보내고, 영화가 끝나는 시간에 맞춰서 측정 종료

- 측정 종료 직후, 스마트폰을 통해서 데이터 베이스에 저장

</br>

## **7. 심박수 저장 로직**

<br>

![image](https://user-images.githubusercontent.com/28921379/129561745-3a68e300-7340-437e-86d4-03e3c4b64772.png)


**사진 출처 : Android WearOS Docs**

    스마트워치의 WearOS와 Android 기기에는 모두 데이터를 받을 수 있는 Listener가 있어야한다. 
    심박수 측정 시작을 위해 신호를 보낼 떄는 위와 같은 흐름으로 진행되고, 심박수 측정이 모두 끝난 후에는 반대 방향으로 진행된다. 

<br>

- **측정 시작**
    - Android 휴대폰에서 WearOS로 sendMessage - **ConnectWearableActivity.java**
    <br>

    ![image](https://user-images.githubusercontent.com/28921379/129562198-83dc5681-bba1-4e2d-8f33-c2a2154251cd.png)

    - WearOS 에서 MessageReceived - **DataLayerListenerService.java**
    <br>

    ![image](https://user-images.githubusercontent.com/28921379/129572287-f2157641-4494-43a4-9ba8-540c0acdc4ac.png)

<br>

- **측정 종료**
    - WearOS에서 Android 휴대폰으로 sendMessage - **MainActivity.java**
    <br>

    ![image](https://user-images.githubusercontent.com/28921379/129572623-07f12e24-84f5-46e6-8c68-3efd050a1eaa.png)

    - Android 휴대폰에서 MessageReceived - **BpmTransactionService.java**
    <br>

    ![image](https://user-images.githubusercontent.com/28921379/129572773-b65d3140-d717-4321-9249-045b5c44c489.png)

</br>

## **8. 머신러닝 분석 기법**

    심박수 분석의 모든 과정은 매일 00시에 스케줄링을 통해서 자동으로 진행됩니다.
<br>

측정 된 BPM 값을 DB에서 불러와서 영화 상영 초 마다 평균을 내어 중심점을 3개로 설정한K-means Clustering을 수행한다.  수행이 끝난 뒤 산출 된 Centroid와 Bpm의 거리를 계산한다. 거리가 가장 가까운 Centroid에 임의의 값 α를 곱한 값 보다 BPM 값이 높으면 Score 1점을 부여하고, 최종적으로 Score를 시청 인원수로 평균 내어 점수를 산출한다.


### **K-means Clustering**
<br>

![image](https://user-images.githubusercontent.com/28921379/129681063-8d6e3623-6fb0-472b-99f6-c7ee3af21a75.png)

 

데이터 분리에 특화된 알고리즘이다.

랜덤한 n개의 점을 중심으로 지정하고 중심 점에 가까운 점들을 할당 후에 군집화한다. (Expectation)
도출 된 3개의 군집 경계를 기준으로 중심점을 재설정한다. (Maximization)
위 Expectation 과정과 Maximization을 사용자가 설정한 횟수만큼 반복하거나, 반복을 진행함에도 중심 점(Centroid)의 변화가 거의 없는 경우 수행을 종료한다.
이렇게 K-means 클러스터링 을 진행하면 위 그림과 같이 각 데이터의 분포별로 군집화가 진행된다.

군집화를 진행하는 이유는, 사람마다 심박수가 다르기 때문에, 표준화를 하기 위해서이다.
각 군집 중심점 값을 기준으로 할당되어있는 심박수들을 비교하여 점수를 산출한다.

![image](https://user-images.githubusercontent.com/28921379/129682111-ef7716c3-10b3-4518-8f1f-dd3d2325be30.png)



</br>

## **6. 팀원 소개**
<br>

### **김도현 (팀장)**

- ML Engineer
- 분석 서비스 개발
- 결과 서빙 API 구현

### 박윤정 **(팀원)**

- ML Engineer
- 분석 서비스 개발
- 결과 서빙 API 구현

### **김가람 (팀원)**

- Backend Engineer
- 서버 관리
- 인프라 구축
- REST API 구현

### 윤혜수 **(팀원)**

- Frontend Engineer
- 클라이언트 개발
- UX/UI 구성
- 하드웨어 통신 구현
