FROM openjdk:8-jre-slim
WORKDIR /app
COPY . /app

RUN apt-get update && \
    apt-get install -y scala

RUN scalac Artifact.scala
# Run the Scala program
CMD ["scala", "Artifact"]
