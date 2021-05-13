FROM alpine:3.13.5

LABEL desc="Docker container to run ASTMiner with all preinstalled requirements"

# Install java
RUN apk add openjdk8

# Install G++ (required for Fuzzy parser)
RUN apk add g++

# Copy astminer sources
WORKDIR astminer
COPY . .

# Prepare shadow jar
RUN ./gradlew shadowJar

ENTRYPOINT ["java", "-jar", "build/shadow/astminer.jar"]
