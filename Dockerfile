FROM johnmcconnell/lein:2.7.1
MAINTAINER John McConnell "johnnyillinois@gmail.com"

RUN /usr/sbin/useradd --create-home --home-dir /opt/optylist-api --shell /bin/bash optylist-api
USER optylist-api
WORKDIR /opt/optylist-api

USER root
COPY . /opt/optylist-api
RUN chown -R optylist-api:optylist-api /opt/optylist-api
USER optylist-api

RUN lein ring uberjar

ENV PORT "3000"
ENV CLJ_ENV "production"
ENV POSTGRES_PASSWORD="mysecretpassword"
ENV JDBC_DATABASE_URL "jdbc:postgresql://optylist-api-db:5432/postgres?user=postgres&password=$POSTGRES_PASSWORD"

ENTRYPOINT ["/opt/optylist-api/bin/docker/entry"]
CMD ["web"]
