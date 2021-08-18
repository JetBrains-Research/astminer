# `astminer` CLI usage

You can run `astminer` via its command line interface (CLI).
The CLI allows you to run the tool on any implemented parser with specified options for filtering, label extraction, and storage of the results.

## Getting started
You can build and run the CLI with any version of `astminer`:
1. Check out the relevant version of `astminer` sources (for example, the `master` branch).
2. Build a shadow jar for `astminer`:
```shell
gradle shadowJar 
```
3. (Optional) Pull a Docker image with all parser dependencies installed:
```shell
docker pull voudy/astminer
```
4. Run `astminer` with a specified configuration file:
```shell
./cli.sh <path-to-yaml-config>
```

## Configuration file

The CLI of `astminer` is fully configured via a YAML file.
The config should contain the following values:
- `inputDir` — path to the directory with input data
- `outputDir` — path to the output directory 
- `parser` — parser name and list of target languages
- `filters` — list of filters and parameters
- `label` — label extraction strategy
- `storage` — storage format

We prepared several [YAML config examples](../configs) so that you can use them as a reference. Possible parameter values for the respective entities can be found in the [docs](../docs) or [definitions](../src/main/kotlin/astminer/config/) of the config classes.

## Docker

Some parsers have non-trivial environment requirements.
For example, g++ must be installed for the Fuzzy parser (see [parsers](./parsers.md)).

To simplify dealing with such cases, we provide a Docker image with all parser dependencies.
This image can be pulled from DockerHub:
```shell
docker pull voudy/astminer
```
