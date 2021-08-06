# Storages

The storage defines how the ASTs should be saved on disk.
For now, `astminer` support tree-based and path-based storage formats.

`Astminer` also knows how to find the structure of the dataset and can 
save trees or path contexts in the appropriate holdout folders. (`train`, `val` and `test`). If the data is not structured, 
all trees will be saved in the `data` folder. Description files for trees or paths will be
saved along with holdouts in the same `outputPath` directory.

Storage config classes are defined in [StorageConfigs.kt](../src/main/kotlin/astminer/config/StorageConfigs.kt).

## Tree formats

### CSV

Saves the trees with labels to a comma-separated file.
Each tree is encoded to a single line using parentheses sequences.

 ```yaml
 name: csv AST
 ```

### Dot

Saves each tree in separate file using [dot](https://graphviz.org/doc/info/lang.html) syntax.
Along with dot files, this storage also saves `description.csv` with mapping between files, source files, and labels.


 ```yaml
 name: dot AST
 ```

### Json lines

Saves each tree with its label in the Json Lines format.
Json format of AST inspired by the [150k Python](https://www.sri.inf.ethz.ch/py150) dataset.

 ```yaml
 name: json AST
 ```

## Path-based representations

Path-based representation was introduced by [Alon et al.](https://arxiv.org/abs/1803.09544).
It is used in popular code representation models such as `code2vec` and `code2seq`.

### Code2vec

Extract paths from each AST. Output is 4 files:
1. `node_types.csv` contains numeric ids and corresponding node types with directions (up/down, as described in [paper](https://arxiv.org/pdf/1803.09544.pdf));
2. `tokens.csv` contains numeric ids and corresponding tokens;
3. `paths.csv` contains numeric ids and AST paths in form of space-separated sequences of node type ids;
4. `path_contexts.c2s` contains the labels and sequences of path-contexts (each representing two tokens and a path between them).
    This file will be generated for every holdout.

Each line in `path_contexts.c2s` starts with a label, followed by a sequence of space-separated triples. Each triple contains start token id, path id, end token id, separated with commas.

 ```yaml
 name: code2vec
 maxPathLength: 10
 maxPathWidth: 2
 maxTokens: 1000 # can be omitted
 maxPaths: 1000 # can be omitted
 maxPathContextsPerEntity: 200 # can be omitted
 ```


### Code2seq

Extract paths from each AST and save in the code2seq format.
The output is `path_context.c2s` file, which will be generated for every holdout.
Each line starts with a label, followed by a sequence of space-separated triples.
Each triple contains the start token, path node types, and end token id, separated with commas.

To reduce memory usage, you can enable `nodesToNumber` option.
If `nodesToNumber` is set to `true`, all types are converted into numbers and `node_types.csv` is added to output files.

 ```yaml
 name: code2seq
 maxPathLength: 10
 maxPathWidth: 2
 maxPathContextsPerEntity: 200 # can be omitted
 nodeToNumber: true # can be omitted
 ```
