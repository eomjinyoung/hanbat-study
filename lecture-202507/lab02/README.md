# Lab02 - 빌드 도구 설치 및 사용법

## 개요
이번 실습에서는 빌드 도구 사용법을 배웁니다. 빌드 도구는 소스 코드를 실행 가능한 프로그램으로 바꾸는 것을 자동화하는 도구입니다. 즉 소스 코드 컴파일, 테스트 실행, 패키징 등을 자동으로 처리하여 개발자의 생산성을 높여줍니다.

## 목표
- 빌드 도구의 역할 이해
- Ant vs Maven vs Gradle 비교
- Gradle 빌드 도구 설치 및 사용법 이해
- Gradle 자바 프로젝트의 디렉토리 구조 이해

## 실습

### 1. 빌드 도구의 역할 이해
- 프롬프트
  ```text
  빌드 도구에 대해 간단히 설명해줘.
  ```

### 2. Ant vs Maven vs Gradle 비교

- 프롬프트:
  ```text
  Ant 빌드 도구가 있었는데 Maven이 등장하였다. 그 이후에 Gradle이 등장하였다. 새로운 도구가 등장했다는 것은 이전 도구의 단점을 극복하기 위함일 것이다. 이 부분을 비교하여 설명해줘.
  ```

### 3. Gradle 빌드 도구 설치
- [Gradle 다운로드](https://gradle.org/install/)
- Gradle 설치 후 환경 변수 설정
  - `GRADLE_HOME` 변수 설정: Gradle 설치 경로를 지정
  - `PATH` 변수에 `%GRADLE_HOME%\bin` 추가
- Gradle 설치 확인
  ```bash
  gradle -v
  ```

### 4. Gradle 로 자바 애플리케이션 프로젝트 생성 
- Gradle 프로젝트 생성
  ```bash
  $ gradle init
  Starting a Gradle Daemon (subsequent builds will be faster)

  Select type of build to generate:
    1: Application
    2: Library
    3: Gradle plugin
    4: Basic (build structure only)
  Enter selection (default: Application) [1..4] 1

  Select implementation language:
    1: Java
    2: Kotlin
    3: Groovy
    4: Scala
    5: C++
    6: Swift
  Enter selection (default: Java) [1..6] 1

  Enter target Java version (min: 7, default: 21): 

  Project name (default: lab03): myapp

  Select application structure:
    1: Single application project
    2: Application and library project
  Enter selection (default: Single application project) [1..2] 1

  Select build script DSL:
    1: Kotlin
    2: Groovy
  Enter selection (default: Kotlin) [1..2] 2

  Select test framework:
    1: JUnit 4
    2: TestNG
    3: Spock
    4: JUnit Jupiter
  Enter selection (default: JUnit Jupiter) [1..4] 

  Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no] 
  ```
- Gradle 로 생성한 디렉토리 구조
  ```bash
  $ tree
  .
  ├── app
  │   ├── build.gradle
  │   └── src
  │       ├── main
  │       │   ├── java
  │       │   │   └── org
  │       │   │       └── example
  │       │   │           └── App.java
  │       │   └── resources
  │       └── test
  │           ├── java
  │           │   └── org
  │           │       └── example
  │           │           └── AppTest.java
  │           └── resources
  ├── gradle
  │   ├── libs.versions.toml
  │   └── wrapper
  │       ├── gradle-wrapper.jar
  │       └── gradle-wrapper.properties
  ├── gradle.properties
  ├── gradlew
  ├── gradlew.bat
  └── settings.gradle
  ```
  - 프롬프트
  ```text
  다음은 Gradle로 생성한 자바 애플리케이션 프로젝트의 디렉토리 구조다. 각 디렉토리와 파일의 역할을 설명해줘.
  (tree 명령어로 출력한 디렉토리 구조를 붙여넣기)
  ```

### 5. Gradle 빌드 도구 사용법

- Gradle Wrapper 사용
  - Gradle Wrapper는 프로젝트에 포함된 `gradlew` 스크립트와 `gradle/wrapper/gradle-wrapper.jar` 파일로 구성됩니다.
  - Gradle Wrapper를 사용하면 다음과 같은 장점이 있습니다:
    - 개발자가 Gradle을 별도로 설치할 필요가 없습니다.
    - Gradle Wrapper를 사용하여 빌드를 실행하면, Gradle 버전이 자동으로 다운로드되고 설정됩니다.
    - 프로젝트에 포함된 Gradle 버전을 사용하므로, 개발 환경에 따라 Gradle 버전이 달라지는 문제를 방지할 수 있습니다.
    - 즉 모든 팀원이 빌드 환경을 일관되게 유지할 수 있습니다.
- Gradle Wrapper 생성:
  ```bash
  gradle wrapper --gradle-version 8.14.3
  ```
- Gradle 을 사용하여 수행할 수 있는 작업 목록 확인:
  ```bash
  ./gradlew tasks
  ```
- 자바 소스 코드 컴파일:
  ```bash
  ./gradlew compileJava
  ``` 
- 자바 리소스 파일 처리:
  ```bash
  ./gradlew processResources
  ```
- 자바 애플리케이션 컴파일 및 리소스 처리 = `compileJava` + `processResources`:
  ```bash
  ./gradlew classes
  ```
- 단위 테스트 소스 파일 컴파일:
  ```bash
  ./gradlew compileTestJava
  ```
- 단위 테스트 리소스 파일 처리:
  ```bash
  ./gradlew processTestResources
  ```
- 자바 애플리케이션 실행:
  ```bash
  # 단순 실행
  ./gradlew run
  # 디버그 모드로 실행
  ./gradlew run --debug
  # 실행 계획만 확인
  ./gradlew run --dry-run
  # 실행 시 콘솔 출력 상세 모드
  ./gradlew run --console=verbose
  # 실행 시 콘솔 출력 상세 모드 + 정보 출력
  ./gradlew run --info --console=verbose
  # Gradle 실행 로그를 최소화하여 출력. 즉 애플리케이션 출력에 집중
  ./gradlew run --quiet
  # Gradle 실행 로그를 최소화하여 출력. 색상 빼고 단순 텍스트로 출력
  ./gradlew run --quiet --console=plain
  ``` 
- 테스트 실행:
  ```bash
  ./gradlew test  
  ```
- JAR 파일(app.jar) 생성:
  ```bash
  ./gradlew jar --console=plain
  ```
- 전체 빌드 실행:
  ```bash
  ./gradlew build --console=plain
  ```

### 6. `App6.java` 파일을 Gradle 프로젝트로 변환

- `src/org/example/myapp/App6.java` 파일을 `src/main/java/org/example/myapp/App.java`로 이동
  - 클래스 이름을 `App6`에서 `App`으로 변경
- 단위 테스트 자바 소스 파일(`src/test/java/org/example/AppTest.java`) 삭제
- `myapp/app/build.gradle` 파일 수정
  ```groovy
  application {
    mainClass = 'org.example.myapp.App' // 클래스 이름 변경
  }

  dependencies {
    implementation 'com.google.guava:guava:33.4.8-jre' // Guava 라이브러리 추가
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.0'
  }
  ```
- 자바 애플리케이션 실행
  ```bash
  # 단계 별 실행 작업 로그 출력
  ./gradlew run --console=plain
  # 애플리케이션 출력만 허용
  ./gradlew run --quiet
  ```
