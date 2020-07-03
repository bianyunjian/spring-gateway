FROM java:8
VOLUME /tmp
ADD target/demo-service-0.0.1-SNAPSHOT.jar /app.jar
ENV TimeZone Asia/Shanghai
RUN bash -c  'touch /app.jar'
EXPOSE 10020
ENTRYPOINT ["sh","-c","java ${EZML_DEMO_SERVICE_JAVA_OPTS} -jar /app.jar ${EZML_DEMO_SERVICE_EXT_CONFIG}"]