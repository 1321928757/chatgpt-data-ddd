FROM openjdk:8
MAINTAINER kitie
COPY *.jar /app.jar 
CMD ["--server.port=7070"]
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE 7070  
ENTRYPOINT ["java","-Duser.timezone=Asia/Shanghai","-jar","/app.jar"] 