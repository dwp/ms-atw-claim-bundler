FROM gcr.io/distroless/java17@sha256:009deffea52dc93a7563fd73ff55138fa02cdabe32c4defa8375ce1cee86ac4a
EXPOSE 9020

COPY target/ms-claim-bundler-*.jar /ms-claim-bundler.jar
ENTRYPOINT ["java", "-jar",  "/ms-claim-bundler.jar"]
