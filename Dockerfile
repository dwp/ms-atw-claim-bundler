FROM gcr.io/distroless/java17@sha256:97891db4665b347af579b94f324f94f984a1e2e6d009b0015a1d2d214a0389d3

USER nonroot

COPY --from=pik94420.live.dynatrace.com/linux/oneagent-codemodules:java / /
ENV LD_PRELOAD /opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

EXPOSE 9020

COPY target/ms-claim-bundler-*.jar /ms-claim-bundler.jar

HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:9020/healthcheck || exit 1

ENTRYPOINT ["java", "-jar",  "/ms-claim-bundler.jar"]
