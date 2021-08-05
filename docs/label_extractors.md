# Label extractors

Label extractors are required for correct extraction of labels from raw ASTs.
Internally, they extract labels from the tree and process the tree to avoid data leaks.
Also, label extractors define the granularity level for the whole pipeline.

Label extractor config classes are defined in [LabelExtractorConfigs.kt](src/main/kotlin/astminer/config/LabelExtractorConfigs.kt).

## file name
**granularity**: files

Use file name of source file as a label.

 ```yaml
 name: file name
 ```

## folder name
**granularity**: files

Use the name of the parent folder of source file as a label.
May be useful for code classification datasets, e.g., POJ-104.

 ```yaml
 name: folder name
 ```

## function name
**granularity**: functions

Use name of each function as a label.
This label extractor will also hide the function name in the AST and all recursive calls to prevent data leaks.

 ```yaml
 name: function name
 ```
