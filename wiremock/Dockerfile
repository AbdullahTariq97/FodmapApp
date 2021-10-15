FROM registry.tools.cosmic.sky/core-engineering/release/alpine-java11:v0.14.0

ADD build/libs/*-all.jar /data/service.jar

EXPOSE 9000

CMD ["java", "-jar", "/data/service.jar", "--port=9000", "--max-request-journal-entries=10", "--root-dir=/data"]