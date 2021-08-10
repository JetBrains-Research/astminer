# `astminer` CLI usage

You can run `astminer` through a command line interface (CLI).
The CLI allows to run the tool on any implemented parser with specified options for filtering, label extraction, and storage of the results.

## How to
You can build and run the CLI with any version of `astminer`:
1. Check out the relevant version of `astminer` sources (for example, the `master-dev` branch)
2. Build a shadow jar for `astminer`:
```shell
gradle shadowJar 
```
3. [optional] Pull a docker image with all parsers dependencies installed:
```shell
docker pull voudy/astminer
```
4. Run `astminer` with specified config:
```shell
./cli.sh <path-to-yaml-config>
```

## Config

CLI of `astminer` is fully configured by a YAML config.
The config should contain next values:
- `inputDir` — path to the directory with input data
- `outputDir` — path to the output directory 
- `parser` — parser name and list of target languages
- `filters` — list of filters and parameters
- `label` — label extraction strategy
- `storage` — storage format

[configs](../configs) contains some config examples that could be used as a reference for the YAML structure.

## Docker

Some parsers have non-trivial environment requirements.
For example, g++ must be installed for Fuzzy parser (see [parsers](./parsers.md)).

To ease dealing with such cases, we provide a Docker image with all parser dependencies.
This image can be pulled from DockerHub:
```shell
docker pull voudy/astminer
```
