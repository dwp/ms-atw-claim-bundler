FROM gcr.io/distroless/java11@sha256:c72d801a69d7a4996ce4f9d9d6fb1473867a6f778b5f39be7a7a7d6457f655ab
EXPOSE 9020

COPY target/ms-claim-bundler-*.jar /ms-claim-bundler.jar
ENTRYPOINT ["java", "-jar",  "/ms-claim-bundler.jar"]
