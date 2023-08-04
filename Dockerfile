FROM cloudnativek8s/microservices-java17-alpine-u10k:v1.0.27

RUN mkdir -p /opt/app/lib && mkdir -p /opt/app/plugins && mkdir -p /opt/app/data

COPY --chown=user:app cassandra/build/libs/cassandra*uber.jar /opt/app/lib/cassandra-uber.jar

COPY --chown=user:app docker-executor.yaml /opt/app/executor.yaml
COPY --chown=user:app entrypoint.sh /opt/app/entrypoint.sh

WORKDIR "/opt/app"

ENTRYPOINT ["/opt/app/entrypoint.sh"]

CMD ["/opt/app/executor.yaml"]

