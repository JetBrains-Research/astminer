FROM ubuntu:20.04

LABEL desc="Docker container to run ASTMiner with all preinstalled requirements"

# Instal OpenJDK8
RUN apt-get update && apt-get install -y openjdk-8-jdk

# Install G++ (required for Fuzzy parser)
RUN apt-get update && apt-get install -y g++

# Install PythonParser for GumTree
RUN apt-get install -y --no-install-recommends -y python3.8 python3-pip git && \
    git clone https://github.com/JetBrains-Research/pythonparser && \
    pip3 install -r pythonparser/requirements.txt && \
    mv pythonparser/src/main/python/pythonparser/pythonparser_3.py /tmp/pythonparser && \
    chmod +x /tmp/pythonparser && \
    rm -rf pythonparser
ENV PATH="/tmp:${PATH}"

# Copy astminer sources
WORKDIR astminer
COPY . .

# Prepare shadow jar
RUN ./gradlew shadowJar

CMD ["java", "-jar", "build/shadow/astminer.jar"]
