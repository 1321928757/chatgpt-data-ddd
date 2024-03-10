FROM openjdk:8
COPY *.jar /app.jar 
CMD ["--server.port=7070"]
EXPOSE 7070
ENTRYPOINT ["java","-Duser.timezone=Asia/Shanghai","-jar","/app.jar"] 
