# Filters

Each filter dedicate to remove *bad* trees from data, e.g. too large trees.
Also, each filter works only for certain levels of granulaity.
Here we describe all implemented filters.
Each description contains corresponding YAML config.

Since filters may be language or parser specific, `astminer` should support all this zoo.
And since we **do not** use any of intermediate representation it is impossible to unify filtering.
Therefore some languages or parsers may not support needed filter 
(`FunctionInfoPropertyNotImplementedException` appears).
To handle this user should manually add specific logic of parsing AST to get info about function or code at 
all. 

Filter config classes are defined in [FilterConfigs.kt](../src/main/kotlin/astminer/config/FilterConfigs.kt).

## by tree size
**granularity**: files, functions

Exclude ASTs that are too small or too big.

 ```yaml
 name: 'by tree size'
 minTreeSize: 1
 maxTreeSize: 100
 ```

## by words number
**granularity**: files, functions

Exclude ASTs that have too many words in any token.

 ```yaml
 name: by words number
 maxTokenWordsNumber: 10
 ```

## by function name length
**granularity**: functions

Exclude functions that have too many words in their name.

 ```yaml
 name: by function name length
 maxWordsNumber: 10
 ```

## no constructors
**granularity**: functions

Exclude constructors

 ```yaml
 name: no constructors
 ```

## by annotations
**granularity**: functions

Exclude functions that have certain annotations (e.g. `@Override`)

 ```yaml
 name: by annotations
 annotations: [ override ]
 ```

## by modifiers
**granularity**: functions

Exclude functions with certain modifiers (e.g. `private` functions)

 ```yaml
 name: by modifiers
 modifiers: [ private ]
 ```
