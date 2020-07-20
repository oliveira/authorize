FROM clojure:alpine

RUN mkdir /app
WORKDIR /app
COPY . .
RUN lein uberjar
ENTRYPOINT [ "java", "-jar", "target/authorize-standalone.jar" ]
