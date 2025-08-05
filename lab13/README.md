# Lab13 - jQuery, Handlebars 사용하기

## 개요
이번 실습에서는 jQuery와 Handlebars를 사용하여 동적인 웹 페이지를 구현하는 방법을 배웁니다. jQuery는 HTML 문서 탐색, 이벤트 처리, 애니메이션 및 Ajax 상호 작용을 쉽게 할 수 있도록 도와주는 JavaScript 라이브러리입니다. Handlebars는 템플릿 엔진으로, HTML을 동적으로 생성하는 데 사용됩니다.

## 목표

- jQuery 사용법
- Handlebars 사용법

## 실습

### 1. jQuery 및 Handlebars 자바스크립트 라이브러리 적용하기

- `board/index.html` 파일에 jQuery와 Handlebars 라이브러리를 추가합니다.
  ```html
  <!-- CDN으로 라이브러리 로드 -->
  <head>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/handlebars@latest/dist/handlebars.min.js"></script>
  </head>
  ```
    
### 2. 게시글 목록 템플릿 작성하기
- `board/index.html` 파일에 Handlebars 템플릿을 추가합니다.
  ```html
  <script id="boards-template" type="text/x-handlebars-template">
      {{#each boards}}
          <tr>
              <td>{{no}}</td>
              <td><a href="/board/view.html?no={{no}}">{{title}}</a></td>
              <td>{{formatDate createdDate}}</td>
              <td>{{viewCount}}</td>
          </tr>
      {{/each}}
  </script>
