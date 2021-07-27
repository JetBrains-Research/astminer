# `astminer` CLI usage

You can run `astminer` through command-line interface.
CLI allow to run the tool on any implemented parser with specifying filtering, label extracting and storage options.

## How to
You can prepare and run CLI on any branch you want. Just navigate to it and do follow steps:
1. Build shadow jar for `astminer`:
```shell
gradle shadowJar 
```
2. [Optionally] Pull docker image with all parsers dependencies installed:
```shell
docker pull voudy/astminer
```
3. Run `astminer` with specified config:
```shell
./cli.sh <path-to-yaml-config>
```

## Config

CLI usage of the `astminer` completely configured by YAML config.
The config should contain next values:
- `inputDir` — path to directory with input data
- `outputDir` — path to output directory 
- `parser` — parser name and list of target languages
- `filters` — list of filters with their parameters
- `label` — label extractor strategy
- `storage` — storage format

[configs](../configs) already contain some config examples, look at them for more structure details.

## Docker

Since some parsers have additional dependencies,
e.g. G++ must be installed for Fuzzy parser (see [parsers](./parsers.md)).
We introduce Docker image with already installed parser dependencies.
To use this image you should only pull this image from DockerHub and run CLI by `./cli.sh`.
