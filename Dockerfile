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

# Install tree sitter, tree sitter grammars and copy scripts
COPY ./src/main/python/parse/tree_sitter astminer/src/main/python/parse/tree_sitter
RUN cd astminer/src/main/python/parse/tree_sitter &&\
    git clone https://github.com/tree-sitter/tree-sitter-java.git && \
    pip install tree-sitter && \
    python3 main.py -b tree-sitter-java

# Install srcML
RUN wget http://131.123.42.38/lmcrs/v1.0.0/srcml_1.0.0-1_ubuntu20.04.deb && \
    apt-get install -y libarchive13 && \
    apt-get install -y libcurl4 && \
    dpkg -i srcml_1.0.0-1_ubuntu20.04.deb

# Copy astminer shadow jar
WORKDIR astminer
COPY ./build/shadow/astminer.jar .

ENTRYPOINT ["java", "-jar", "astminer.jar"]
