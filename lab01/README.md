# Lab01 - 자바 애플리케이션 개발 준비

## 개요
이번 실습에서는 자바 애플리케이션 개발에 필요한 도구를 설치하고 각 도구의 사용법을 연습합니다.

## 목표
- 자바 애플리케이션 개발 관련 도구 설치
- JDK 주요 도구 사용법 이해

## 실습

### 1. JDK 설치
- [JDK 다운로드](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- 환경 변수 설정
  - `JAVA_HOME` 변수 설정: JDK 설치 경로를 지정
  - `PATH` 변수에 `%JAVA_HOME%\bin` 추가

### 2. VSCode 설치
- [VSCode 다운로드](https://code.visualstudio.com/)
- Java Extension Pack 설치
  - VSCode에서 Extensions 탭을 열고 "Java Extension Pack" 검색 후 설치
- [Google Java Format 사이트 참조](https://github.com/google/google-java-format)
  - [google-java-format-for-vs-code 확장 설치](https://marketplace.visualstudio.com/items?itemName=JoseVSeb.google-java-format-for-vs-code)
- `settings.json` 파일에 다음 설정 추가:
  ```json
  {
    "[java]": {
      "editor.defaultFormatter": "josevseb.google-java-format-for-vs-code",
      "editor.formatOnSave": true,
      "editor.rulers": [100]
    }
  }
  ```

### 3. Git 설치
- [Git 다운로드](https://git-scm.com/downloads)
- Git Bash 설치
- Git Bash에서 다음 명령어 실행:
  ```bash
  git config --global user.name "Your Name"
  git config --global user.email "your.email@example.com"
  ```
- GitHub 계정 생성 및 SSH 키 설정
  - [GitHub SSH 키 설정 가이드](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)
  - SSH 키 생성 후 GitHub에 추가
  - Git Bash에서 다음 명령어 실행:
    ```bash
    ssh-keygen -t rsa -b 4096 -C "your.email@example.com"
    ```
  - 생성된 SSH 키를 GitHub 계정에 추가
    - GitHub 계정 설정에서 "SSH and GPG keys"로 이동하여 "New SSH key" 클릭
    - SSH 키 내용을 붙여넣고 저장
- GitHub에 저장소 생성
  - GitHub 웹사이트에 로그인 후 "New" 버튼 클릭
  - 저장소 이름 및 설명 입력 후 "Create repository" 클릭
    - 저장소 이름: 'myapp'
    - 설명: 'Java DevOps 특강 프로젝트'
- GitHub 저장소 클론
  - 로컬에 GitHub 저장소를 클론:
    ```bash
    git clone https://github.com/your-username/myapp.git
    ```
- GitHub 저장소에 변경 사항 푸시
  - 변경 사항을 커밋하고 git 서버에 푸시:
    ```bash
    git add .
    git commit -m "Your commit message"
    git push origin main
    ```

### 4. `.gitignore` 파일 생성
- `myapp` 폴더에 `.gitignore` 파일 생성
- [gitignore.io](https://gitignore.io) 사이트에 접속
  - 다음 각 검색어 입력 후 "Create" 버튼 클릭
    - `c++`,`java`,`node`,`linux`,`macos`,`maven`,`gradle`,`eclipse`,`windows`,`intellij+all`,`visualstudiocode`,`nextjs`
  - 생성된 내용을 `.gitignore` 파일에 붙여넣기

### 5. 자바 애플리케이션 작성
- VSCode에서 `myapp` 폴더를 열고 `App.java` 파일 생성
  ```java
  public class App {
      public static void main(String[] args) {
          System.out.println("hello, world");
      }
  }
  ``` 
- VSCode에서 터미널 열기
  - `Ctrl + `` (Ctrl + 백틱) 키를 눌러 터미널 열기
- 자바 애플리케이션 컴파일과 실행
  ```bash
  # 컴파일
  javac App.java

  # 실행
  java App
  ```
  - 출력 결과 확인
    - "hello, world"가 출력되어야 합니다.
  
### 6. `javap` 사용법

클래스 파일의 구조를 확인할 때 사용하는 Java 클래스 파일 역어셈블러(disassembler)입니다.

- `.class` 파일의 구조 분석
  ```bash
  javap App
  ```
- 바이트코드 분석(disassemble)
  ```bash
  javap -c App
  ```
- 클래스 파일의 상수 풀 및 메타데이터 정보 확인
  ```bash
  javap -verbose App
  ```

### 7. `javadoc` 사용법

Java 소스 코드에서 API 문서를 생성하는 도구입니다.

- 자바 소스 파일에 Javadoc 주석 추가
  - `App.java` 파일에 Javadoc 주석 추가:
  ```java
  /**
   * 이 클래스는 메인 애플리케이션이다.
   */
  public class App2 {
      /**
       * 애플리케이션 entry point 이다.
       * @param args 프로그램 아규먼트
       * @see <a href="https://docs.oracle.com/en/java/javase/21/">JDK Documentation</a>
       */
      public static void main(String[] args) {
          System.out.println("hello, world");
      }
  }
  ```
- Javadoc 생성
  ```bash
  javadoc -d doc App.java
  ```
- 생성된 문서 확인
  - `doc` 폴더에 HTML 문서가 생성됩니다.

### 8. `jar` 사용법

여러 개의 Java 클래스 파일과 리소스를 하나의 파일(.jar; Java ARchive)로 묶을 때 사용하는 프로그램입니다.

- `App.java` 파일을 컴파일
  ```bash
  javac App.java
  ```
- JAR 파일 생성  
  ```bash
  jar cfe app.jar App *.class
  ```
- JAR 파일 내용 확인
  ```bash
  jar tf app.jar
  ```
- JAR 파일 실행
  ```bash
  java -jar app.jar
  ``` 

### 9. `javac`, `java` 사용법

`javac`는 Java 소스 코드를 컴파일할 때 사용하는 프로그램입니다. `java`는 컴파일된 Java 프로그램을 실행할 때 사용하는 프로그램입니다.

- 단일 파일 컴파일 및 실행
  ```bash
  # 컴파일
  javac App.java

  # 실행
  java App
  ```
  ```bash
  $ tree                      
  .
  ├── App.class
  └── App.java
  ```
- 여러 파일 컴파일 및 실행
  ```java
  // App3.java 
  public class App3 {
    public static void main(String[] args) {
      Utils.printMessage("hello, world");
    }
  }
  // Utils.java
  public class Utils {
      public static void printMessage(String message) {
          System.out.println(message);
      }
  }
  ```
  ```bash
  # 여러 파일 컴파일
  javac App3.java Utils.java

  # 실행
  java App3
  ```
  ```bash
  $ tree                      
  .
  ├── App3.class
  ├── App3.java
  ├── Utils.class
  └── Utils.java
  ```
- 소스 파일과 클래스 파일을 분리
  ```bash
  # 컴파일
  javac -d bin src/App4.java

  # 실행
  java -classpath bin App4
  # 또는
  java -cp bin App4
  ```
  ```bash
  $ tree
  .
  ├── bin
  │   ├── App4.class
  │   └── Utils.class
  └── src
      ├── App4.java
      └── Utils.java
  
  ```
- 패키지 소속 소스 파일 컴파일
  ```bash
  # 컴파일
  javac -d bin -sourcepath src src/org/example/myapp/App5.java

  # 실행
  java -cp bin org.example.myapp.App5
  ```
  ```bash
  $ tree
  .
  ├── bin
  │   └── org
  │       └── example
  │           ├── myapp
  │           │   └── App5.class
  │           └── util
  │               └── Utils.class
  │   
  └── src
      └── org
          └── example
              ├── myapp
              │   └── App5.java
              └── util
                  └── Utils.java
  ```
- 외부 라이브러리 사용
  - [Guava JAR 파일 다운로드](https://search.maven.org/artifact/com.google.guava/guava)
    - `guava-33.4.8-jre.jar` 파일 다운로드
  - `lib` 폴더에 JAR 파일 저장
  - `App6.java` 파일에서 Guava 라이브러리 사용
  ```java
  package org.example.myapp;
  import com.google.common.base.Joiner;
  public class App6 {
      public static void main(String[] args) {
          String message = Joiner.on(", ").join("hello", "world");
          System.out.println(message);
      }
  }
  ```
  ```bash
  # 컴파일
  javac -cp lib/guava-33.4.8-jre.jar -d bin src/org/example/myapp/App6.java

  # 실행
  java -cp bin:lib/guava-33.4.8-jre.jar org.example.myapp.App6
  ```
  ```bash
  $ tree
  .
  ├── bin
  │   └── org
  │       └── example
  │           └── myapp
  │               └── App6.class
  ├── src
  │   └── org
  │       └── example
  │           └── myapp
  │               └── App6.java
  └── lib
      └── guava-33.4.8-jre.jar
  ```

### 10. Git 사용법
- Git 저장소 상태 확인
  ```bash
  # 현재 작업 디렉토리의 파일 상태 보기
  git status
  # 현재 작업 디렉토리의 파일 상태를 짤막하게 보기
  git status --short
  ```
- 커밋 이력 보기
  ```bash
  # 상세 커밋 이력 보기, 나가기는 q 키 입력
  git log

  # 짧은 커밋 이력 보기
  git log --oneline

  # 그래프 형태로 커밋 이력 보기
  git log --graph --oneline --decorate
  ```
- 변경 사항 스테이징
  ```bash
  git add .
  ```
- 변경 사항 커밋
  ```bash
  git commit -m "Your commit message"
  ```
- 원격 저장소에 푸시
  ```bash
  git push origin main
  ```
