FROM mariadb:lts

# 데이터베이스 초기화 스크립트 복사
COPY member_database.sql /docker-entrypoint-initdb.d/

# 환경 변수 설정
ENV MYSQL_ROOT_PASSWORD 7228
ENV MYSQL_DATABASE member_database
ENV MYSQL_USER gw
ENV MYSQL_PASSWORD 7228