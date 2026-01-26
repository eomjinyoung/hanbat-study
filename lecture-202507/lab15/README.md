# Lab15 - Rest API의 CRUD 경로

## 개요
이번 실습에서는 REST API의 CRUD(Create, Read, Update, Delete) 경로를 지정하는 방법을 배웁니다.

## 목표

- CRUD 경로를 지정하는 방법

## 실습

### 1. myapp-api 프로젝트

게시글 Rest API CRUD 경로
- 목록: `GET /boards`
- 상세: `GET /boards/{id}`
- 생성: `POST /boards`
- 수정: `PATCH /boards/{id}`
- 삭제: `DELETE /boards/{id}`

#### 1.0 BoardController 클래스 변경
- `BoardController` 클래스에 CRUD 경로를 지정합니다.
  ```java
  @RestController
  @RequestMapping("/boards") // <--- 복수형으로 변경
  public class BoardController {
      ...
  }
  ```

#### 1.1 게시글 목록 조회
- `BoardController` 클래스의 `list()` 메서드를 수정합니다.
  ```java
  @GetMapping
  public JsonResult list() {
    ...
  }
  ```

#### 1.2 게시글 상세 조회
- `BoardController` 클래스의 `view()` 메서드를 수정합니다.
  ```java
  @GetMapping("/{no}")
  public JsonResult view(@PathVariable("no") Long no) {
    ...
  }
  ```

#### 1.3 게시글 등록
- `BoardController` 클래스의 `add()` 메서드를 수정합니다.
  ```java
  @PostMapping
  public JsonResult add(
      @Valid @RequestBody BoardCreateRequest request, BindingResult bindingResult) {
    ...
  }
  ```

#### 1.4 게시글 변경
- `BoardController` 클래스의 `update()` 메서드를 수정합니다.
```java
  @PatchMapping("/{no}")
  public JsonResult update(
      @PathVariable("no") Long no,
      @Valid @RequestBody BoardUpdateRequest request,
      BindingResult bindingResult) {
    ...
  }
  ```

#### 1.5 게시글 삭제
- `BoardController` 클래스의 `delete()` 메서드를 수정합니다.
```java
  @DeleteMapping("/{no}")
  public JsonResult delete(@PathVariable("no") Long no) {
    ...
  } 
  ```

#### 1.5 게시글 삭제
- `BoardController` 클래스의 `view()` 메서드를 수정합니다.
  ```java
  @DeleteMapping("/{no}")
  public JsonResult delete(@PathVariable("no") Long no) {
  ```

### 2. myapp-ui 프로젝트

#### 2.1 index.html 파일 수정
```javascript
function loadBoards() {
    axios.get('http://localhost:9999/boards')
```

#### 2.2 form.html 파일 수정
```javascript
axios.post('http://localhost:9999/boards', {
    title: formData.get('title'),
    content: formData.get('content')
}, {
```

#### 2.3 view.html 파일 수정

- 게시글 상세 조회
    ```javascript
    function loadBoard() {
        ...
        axios.get(`http://localhost:9999/boards/${boardNo}`)
            .then(function(response) {
    ```
- 게시글 변경
    ```javascript
    function updateBoard(formData) {
        axios.patch(`http://localhost:9999/boards/${formData.get("no")}`, {
            title: formData.get('title'),
            content: formData.get('content')
        }, {
    ```
- 게시글 삭제
    ```javascript
    function deleteBoard() {
        ...
        axios.delete(`http://localhost:9999/boards/${currentBoardNo}`)
            .then(function(response) {
    ```        