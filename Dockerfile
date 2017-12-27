FROM gradle:4.4.1-jdk8-alpine
COPY . /app
WORKDIR /app

USER root
RUN chown -R gradle /app
USER gradle

CMD ["gradle", "run"]
