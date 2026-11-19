FROM tomcat:9.0-jdk8-temurin

ENV DEMO_MODE=true
ENV PORT=8080

RUN rm -rf /usr/local/tomcat/webapps/*
COPY SportZoneManagement/web/ /usr/local/tomcat/webapps/ROOT/
COPY SportZoneManagement/src/java/ /tmp/sportzone-src/
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    && find /tmp/sportzone-src -name '*.java' -print > /tmp/sportzone-sources.txt \
    && javac -encoding UTF-8 -source 8 -target 8 \
       -cp '/usr/local/tomcat/lib/*:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*' \
       -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes @/tmp/sportzone-sources.txt \
    && rm -rf /tmp/sportzone-src /tmp/sportzone-sources.txt

EXPOSE 8080
CMD ["catalina.sh", "run"]
