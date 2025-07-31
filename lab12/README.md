# Lab12 - 웹 애플리케이션 아키텍처를 백엔드와 프론트엔드로 전환하기

## 개요
이번 실습에서는 웹 애플리케이션 아키텍처를 백엔드와 프론트엔드로 전환하는 방법을 배웁니다.

## 목표

- 백엔드와 프론트엔드 아키텍처의 특징
- 백엔드에서 Rest API를 구축하는 방법
- 프론트엔드에서 AJAX를 이용하여 Rest API를 호출하는 방법

## 실습

### 1. 백엔드에서 Rest API 구축하기

#### 1.1 Rest API 테스트 도구 준비

- Postman 도구를 설치합니다.

#### 1.2 Lombok 라이브러리 설치

- `build.gradle` 파일에 다음 의존성을 추가합니다:
  ```groovy
  dependencies {
      implementation 'org.projectlombok:lombok:1.18.38'
      annotationProcessor 'org.projectlombok:lombok:1.18.38'
  }
  ```
- Intellij IDEA에서 Lombok 플러그인을 설치합니다.
  - Intellij IDEA에서 `Preferences > Plugins`로 이동하여 `Lombok` 플러그인을 검색하고 설치합니다.
- Intellij IDEA에서 `Preferences > Build, Execution, Deployment > Compiler > Annotation Processors`로 이동하여 `Enable annotation processing`을 체크합니다.



### 2. 프론트엔드에서 AJAX 호출하기

- XMLHttpRequest를 이용하여 Rest API를 호출하는 방법을 배웁니다.
- Fetch API를 이용하여 Rest API를 호출하는 방법을 배웁니다.
- jQuery를 이용하여 Rest API를 호출하는 방법을 배웁니다.
- Axios를 이용하여 Rest API를 호출하는 방법을 배웁니다.