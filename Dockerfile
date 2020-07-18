FROM clojure:alpine

RUN mkdir /app
WORKDIR /app
COPY . .
RUN lein uberjar
RUN ls -la
ENTRYPOINT [ "java", "-jar", "target/authorize-standalone.jar" ]
