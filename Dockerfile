FROM gcr.io/distroless/java11@sha256:a83115dc78baf90e1ae41bf6eaa2bfe1e5b0f8afb91bd7330a488d413322fcb2
EXPOSE 9020

COPY target/ms-claim-bundler-*.jar /ms-claim-bundler.jar
ENTRYPOINT ["java", "-jar",  "/ms-claim-bundler.jar"]
