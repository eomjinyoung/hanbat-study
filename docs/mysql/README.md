# MySQL을 k8s에 설치하기

- 설치 및 확인 명령
    ```bash
    bash# manifest 파일 적용
    kubectl apply -f mysql-deployment.yml

    # 배포 상태 확인
    kubectl get pods,svc,pvc

    # MySQL 컨테이너 로그 확인 
    kubectl logs deployment/mysql

    # 설정 파일 확인
    kubectl exec -it deployment/mysql -- cat /etc/mysql/conf.d/my.cnf

    # manifest 파일로 생성된 모든 리소스를 한번에 삭제
    kubectl delete -f mysql-deployment.yml
    ```
- 접속 정보
    - 클러스터 내부:
        - 호스트: mysql-service
        - 포트: 3306
        - 데이터베이스: studydb
        - 사용자: study / 패스워드: study
        - 루트: root / 패스워드: root
    - 외부 접속 (개발용):
        - 호스트: localhost
        - 포트: 30306
## MySQL 서버 접속

- 컨테이너 내부로 직접 접속 (가장 일반적)
    ```bash
    bash# MySQL 컨테이너의 bash 쉘로 접속
    kubectl exec -it deployment/mysql -- bash

    # Pod 이름으로 직접 접속
    kubectl get pods  # Pod 이름 확인
    kubectl exec -it mysql-xxxxxxxxx-xxxxx -- bash
    ```
- MySQL 클라이언트로 직접 접속
    ```bash
    # 컨테이너 내에서 MySQL 클라이언트 실행
    kubectl exec -it deployment/mysql -- mysql -u root -proot

    # 또는 study 사용자로 접속
    kubectl exec -it deployment/mysql -- mysql -u study -pstudy studydb

    # 특정 데이터베이스로 직접 접속
    kubectl exec -it deployment/mysql -- mysql -u study -pstudy -D studydb
    ```
- 한글 입력이 깨지는 경우
    - kubectl 명령에서 환경 변수 설정)
        ```bash
        # 환경변수를 설정하고 MySQL 클라이언트로 직접 접속
        kubectl exec -it deployment/mysql -- env LANG=C.UTF-8 LC_ALL=C.UTF-8 mysql -ustudy -pstudy studydb

        # 또는 bash 쉘로 접속하면서 환경변수 설정
        kubectl exec -it deployment/mysql -- env LANG=C.UTF-8 LC_ALL=C.UTF-8 bash
        ```
    - 컨테이너 접속 후 환경변수 설정
        ```bash 
        # 먼저 컨테이너에 접속
        kubectl exec -it deployment/mysql -- bash

        # 컨테이너 내부에서 환경변수 설정 후 MySQL 접속
        export LANG=C.UTF-8
        export LC_ALL=C.UTF-8
        mysql -ustudy -pstudy studydb
        ```