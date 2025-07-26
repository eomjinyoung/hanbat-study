# Lab03 - IntelliJ IDEA와 Gradle을 이용한 자바 애플리케이션 개발

## 개요
이번 실습에서는 IntelliJ IDEA와 Gradle을 이용한 자바 애플리케이션 개발을 연습합니다.

## 목표
- IntelliJ IDEA Community 버전 설치 및 사용법 이해
- ItelliJ IDEA에서 Gradle 다루기

## 실습

### 1. IntelliJ IDEA Community 설치 및 설정

- [IntelliJ IDEA Community 다운로드](https://www.jetbrains.com/idea/download/)
- 설정
  - Google Java Format 플러그인 설치
    - `File` > `Settings` > `Plugins`
    - `Marketplace`에서 `google-java-format` 검색 후 설치
    - Preferences > google-java-format settings에서 `Enable formatting on save` 옵션 활성화
  - 저장할 때 자동 포맷팅 설정 
    - Preferences > 도구 > 저장 시 액션
      - `코드 서식 다시 지정(Reformat code)` 옵션 활성화
      - `import 문 최적화(Optimize imports)` 옵션 활성화
      - `코드 정리 실행` 옵션 활성화

### 2. IntelliJ IDEA에서 Gradle 프로젝트 열기

- IntelliJ IDEA 실행 후 `Open` 선택
- `settings.gradle` 파일이 있는 디렉토리 선택

### 3. Gradle 빌드 도구 사용

- Gradle 빌드 도구를 사용하여 프로젝트 빌드 및 실행
  - Gradle 빌드 실행: `View` > `Tool Windows` > `Gradle`에서 `build` 태스크 실행
  - 애플리케이션 실행:
    - `View` > `Tool Windows` > `Gradle` 선택 또는 오른쪽 세로 탭에서 `Gradle` 아이콘 클릭
    - `myapp` > `Tasks` > `application` > `run` 태스크 실행
  - 애플리케이션 실행 결과 확인:
    - `build` 디렉토리에 생성된 하위 디렉토리 및 파일 구조 확인
    - 실행 창에 출력된 결과 확인
  - 애플리케이션 실행 결과에서 Gradle 실행 로그를 최소화하여 출력:
    - `실행` > `구성편집` > `Gradle` > `myapp[run]` > `실행` > `run --quiet` 옵션 추가

### 4. Google Guava 의존성 제거

- `build.gradle` 파일에서 Google Guava 의존성 제거
  ```groovy
  dependencies {
    // implementation 'com.google.guava:guava:33.4.8-jre' // 이 줄을 제거
  }
  ```
- Gradle 도구 창에서 `모든 Gradle 프로젝트 동기화` 아이콘 클릭
  - 라이브러리 의존성 제거 후 코드 오류 확인
- `App.java` 파일에서 Google Guava 관련 코드 제거
  ```java
  // import com.google.common.base.Joiner; // 이 줄을 제거

  public class App {
    public static void main(String[] args) {
      System.out.println("hello, world"); // Joiner 사용 코드 제거
    }
  }
  ```

### 5. 표준 입력 처리
- `App.java` 파일에서 표준 입력 처리 코드 추가
  ```java
    import java.util.Scanner;
    public class App {
        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("이름? ");
            String name = scanner.nextLine();
            System.out.printf("안녕하세요 %s 님!\n", name);
            scanner.close();
        }
    }
  ```
- 애플리케이션 실행 시 콘솔 입력을 받지 못하는 오류 발생
  - build.gradle 파일에서 `run` 태스크 설정 추가
    ```groovy
    tasks.named('run') {
        standardInput = System.in // 표준 입력을 시스템 입력으로 설정
    }
    ```
- 애플리케이션 실행
  ```bash
  이름? 홍길동
  안녕하세요 홍길동 님!
  ```   