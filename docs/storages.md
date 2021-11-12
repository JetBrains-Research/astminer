# Storages

The storage defines how the ASTs should be saved on disk.
For now, `astminer` supports several tree-based and path-based storage formats.

`astminer` also knows how to find the structure of the dataset and can 
save trees or path contexts in the appropriate holdout folders. (`train`, `val` and `test`). 
If the data is not split, all trees will be saved in the `data` folder. 
Description files for trees or paths will be saved along with holdouts in the same `outputPath` directory.

Storage config classes are defined in [StorageConfigs.kt](../src/main/kotlin/astminer/config/StorageConfigs.kt).

## Tree formats

### CSV

Saves the trees with labels to a comma-separated file.
Each tree is encoded to a single line using parentheses sequences.

 ```yaml
 name: csv AST
 ```

### Dot

Saves each tree in a separate file using the [dot](https://graphviz.org/doc/info/lang.html) syntax.
Along with dot files, this storage also saves `description.csv` with a mapping between files with trees, source files, and labels.


 ```yaml
 name: dot AST
 ```

### JSON lines

Saves each tree with its label in the JSON lines format inspired by the [150k Python](https://www.sri.inf.ethz.ch/py150) dataset.

 ```yaml
 name: json AST
 withPaths: true # can be omitted
 withRanges: true # can be omitted
 ```

In this format, each line represents an AST with its [label](label_extractors.md), path, and all vertices:

```json lines
{
  "label": "1.java",
  "path": "src/test/resources/examples/1.java",
  "ast": [
    { "token": "EMPTY", "typeLabel": "CompilationUnit", "children": [1] },
    { "token": "class", "typeLabel": "TypeDeclaration", "children": [2, 3, 4] },
    ...
  ]
}
```

Possible configuration options for Json storage:
1. `withPaths` allows for each tree to save the path to the file where it appears. Default: `false`.
2. `withRanges` allows for each node to save start and end positions in the corresponding source code. Default: `false`.

## Path-based representations

Path-based representation was introduced by [Alon et al.](https://arxiv.org/abs/1803.09544).
It is used in popular code representation models such as `code2vec` and `code2seq`.

### Code2vec

Extracts paths from each AST. The output is stored in 4 files:
1. `node_types.csv` contains numeric IDs and corresponding node types with directions (up/down, as described in this [paper by Uri Alon et al.](https://arxiv.org/pdf/1803.09544.pdf)).
2. `tokens.csv` contains numeric IDs and corresponding tokens.
3. `paths.csv` contains numeric IDs and AST paths in the form of space-separated sequences of node type IDs.
4. `path_contexts.c2s` contains the labels and sequences of path-contexts (each representing two tokens and a path between them).
    This file is generated for every holdout.

    Each line in `path_contexts.c2s` starts with a label followed by a sequence of space-separated triples. Each triple contains comma-separated IDs of the start token, path, and end token.

 ```yaml
 name: code2vec
 maxPathLength: 10
 maxPathWidth: 2
 maxTokens: 1000 # can be omitted
 maxPaths: 1000 # can be omitted
 maxPathContextsPerEntity: 200 # can be omitted
 ```


### Code2seq

Extracts paths from each AST and save in the code2seq format.
The output is `path_context.c2s` file, which is generated for every holdout.
Each line starts with a label followed by a sequence of space-separated triples.
Each triple contains comma-separated IDs of the start token, path node types, and end token.

To reduce memory usage, you can enable the `nodesToNumbers` option.
If `nodesToNumbers` is set to `true`, all types are converted into numbers and `node_types.csv` with the node-number vocabulary is added to the output files.

 ```yaml
 name: code2seq
 length: 10
 width: 2
 maxPathContextsPerEntity: 200 # can be omitted
 nodesToNumbers: true # can be omitted
 ```
`length` stands for the maximum length of a path inclusively; `width` stands for the maximum distance between the children of the least common ancestor of a path.