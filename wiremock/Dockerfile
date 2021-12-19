FROM openjdk:11

ADD build/libs/*-all.jar /data/service.jar
ADD mappings /data/mappings

EXPOSE 9000

CMD ["java", "-jar", "/data/service.jar", "--port=9000", "--max-request-journal-entries=10", "--root-dir=/data"]