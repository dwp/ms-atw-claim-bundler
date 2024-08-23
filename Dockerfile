FROM gcr.io/distroless/java17@sha256:2578479b0d22bdf9dba8320de62969793b32e3226c9327b1f5e1c9f2bd3f1021
EXPOSE 9020

COPY target/ms-claim-bundler-*.jar /ms-claim-bundler.jar
ENTRYPOINT ["java", "-jar",  "/ms-claim-bundler.jar"]
