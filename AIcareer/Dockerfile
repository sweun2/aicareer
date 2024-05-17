FROM tomcat:10-jdk17

# 톰캣 타임존 설정
RUN ["rm", "/etc/localtime"]
RUN ["ln", "-sf", "/usr/share/zoneinfo/Asia/Seoul", "/etc/localtime"]

# 필요한 파일을 복사합니다. 예를들어 war 파일
COPY ./out/artifacts/ROOT/ROOT.war /usr/local/tomcat/webapps

# 외부에서 사용하는 포트 지정
EXPOSE 8080

# 톰캣 시작
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
