FROM openjdk:11-jre-slim

RUN mkdir -p /opt/app/lib && mkdir -p /opt/app/plugins && mkdir -p /opt/app/data

RUN groupadd --system --gid=1000 app\
    && useradd --system --no-log-init --gid app --uid=1000 app \
    && chown -R app:app /opt/app
USER app

COPY --chown=app:app cassandra/build/libs/cassandra*uber.jar /opt/app/lib/cassandra-uber.jar
COPY --chown=app:app core/build/libs/core*uber.jar /opt/app/lib/core-uber.jar
COPY --chown=app:app jdbc/build/libs/jdbc*uber.jar /opt/app/lib/jdbc-uber.jar

COPY --chown=app:app docker-executor.yaml /opt/app/executor.yaml
COPY --chown=app:app entrypoint.sh /opt/app/entrypoint.sh

WORKDIR "/opt/app"

ENTRYPOINT ["/opt/app/entrypoint.sh"]

CMD ["/opt/app/executor.yaml"]