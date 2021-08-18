# Filters

Each filter is dedicated to removing *bad* trees from the data, e.g. trees that are too big.
Moreover, each filter works only for certain levels of granularity.
Here we describe all filters provided by `astminer` and provide corresponding YAML examples.

You can apply several filters at once. 

Some filters are specific to a language or a parser.
If a language or parser does not support a certain filter,
`FunctionInfoPropertyNotImplementedException` appears.
To get the necessary information about a function or file, extend `astminer` with the specific logic of parsing an AST.

[//]: # " todo Please write more specifically what the developer should do and what result they would get, e.g. To do this, ...add a custom filter ... in ... "

Filter config classes are defined in [FilterConfigs.kt](../src/main/kotlin/astminer/config/FilterConfigs.kt).

## Filter by tree size
**granularity**: files, functions

Exclude ASTs that are too small or too big.

 ```yaml
 name: 'by tree size'
 minTreeSize: 1
 maxTreeSize: 100
 ```

## Filter by words count
**granularity**: files, functions

Exclude ASTs that have too many words in any token.

 ```yaml
 name: by words number
 maxTokenWordsNumber: 10
 ```

## Filter by function name length
**granularity**: functions

Exclude functions that have too many words in their name.

 ```yaml
 name: by function name length
 maxWordsNumber: 10
 ```

## Exclude constructors
**granularity**: functions

Exclude constructors

 ```yaml
 name: no constructors
 ```

## Filter by annotation
**granularity**: functions

Exclude functions that have certain annotations (e.g. `@Override`)

 ```yaml
 name: by annotations
 annotations: [ override ]
 ```

## Filter by modifiers
**granularity**: functions

Exclude functions with certain modifiers (e.g. `private` functions)

 ```yaml
 name: by modifiers
 modifiers: [ private ]
 ```

## Exclude blank functions
**granularity**: functions

Exclude functions with empty body and functions without body

```yaml
name: no blank functions
```
