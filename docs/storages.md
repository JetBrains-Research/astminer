# Storages

Storages defines how ASTs should be saved on a disk.
For now, `astminer` support saving in tree and path-based formats.

Storage config classes are defined in [StorageConfigs.kt](src/main/kotlin/astminer/config/StorageConfigs.kt).

## Tree formats

### CSV

Save trees with labels in comma-separated file.
Each tree encodes into line using sequence of parenthesis.

 ```yaml
 name: csv AST
 ```

### Dot

Save each tree in separate file using [dot](https://graphviz.org/doc/info/lang.html) syntax.
Along with dot files, storage also saves `description.csv` with matching between files, source file, and label.


 ```yaml
 name: dot AST
 ```

### Json lines

Save each tree with label in Json Lines format.
Json format of AST inspired by Python-150k dataset.

 ```yaml
 name: json AST
 ```

## Path-based representations

Path-based representation was introduced by [Alon et al.](https://arxiv.org/abs/1803.09544).
It uses in models like code2vec or code2seq.

### Code2vec

Extract paths from each AST. Output is 4 files:
1. `node_types.csv` contains numeric ids and corresponding node types with directions (up/down, as described in [paper](https://arxiv.org/pdf/1803.09544.pdf));
2. `tokens.csv` contains numeric ids and corresponding tokens;
3. `paths.csv` contains numeric ids and AST paths in form of space-separated sequences of node type ids;
4. `path_contexts.csv` contains labels and sequences of path contexts (triples of two tokens and a path between them).

Each line in `path_contexts.csv` starts with a label,
then it contains a sequence of space-separated triples. Each triple contains start token id, path id, end token id, separated with commas.

 ```yaml
 name: code2vec
 maxPathLength: 10
 maxPathWidth: 2
 maxTokens: 1000 # can be omitted
 maxPaths: 1000 # can be omitted
 maxPathContextsPerEntity: 200 # can be omitted
 ```
