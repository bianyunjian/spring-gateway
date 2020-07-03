FROM java:8
VOLUME /tmp
ADD target/eureka-server-0.0.1-SNAPSHOT.jar /app.jar
ENV TimeZone Asia/Shanghai
RUN bash -c  'touch /app.jar'
EXPOSE 9100
ENTRYPOINT ["sh","-c","java ${EZML_EUREKA_JAVA_OPTS} -jar /app.jar ${EZML_EUREKA_EXT_CONFIG}"]