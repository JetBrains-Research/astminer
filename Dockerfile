FROM ubuntu:20.04

LABEL desc="Docker container to run ASTMiner with all preinstalled requirements"

# Instal OpenJDK11
RUN apt-get update && apt-get install -y openjdk-11-jdk

# Install G++ (required for Fuzzy parser)
RUN apt-get update && apt-get install -y g++

# Install PythonParser for GumTree
ARG PYTHONPARSER_REPO=https://raw.githubusercontent.com/JetBrains-Research/pythonparser/master
RUN apt-get update && \
    apt-get install -y python3.8 python3-pip git wget && \
    mkdir pythonparser && \
    cd pythonparser && \
    wget $PYTHONPARSER_REPO/requirements.txt && \
    wget $PYTHONPARSER_REPO/src/main/python/pythonparser/pythonparser_3.py -O pythonparser && \
    pip3 install -r requirements.txt && \
    chmod +x pythonparser
ENV PATH="/pythonparser:${PATH}"

# Copy astminer shadow jar
WORKDIR astminer
COPY ./build/shadow/astminer.jar .

ENTRYPOINT ["java", "-jar", "astminer.jar"]
