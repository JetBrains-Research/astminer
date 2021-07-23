# Label extractors

Label extractors are required for correct extracting of labels from raw ASTs.
Inside themselves they extract label from tree and process tree to avoid data leak.
Also, label extractors define granularity level for the whole pipeline.

Label extractor config classes are defined in [LabelExtractorConfigs.kt](src/main/kotlin/astminer/config/LabelExtractorConfigs.kt).

## file name
**granularity**: files

Use file name of source file as label.

 ```yaml
 name: file name
 ```

## folder name
**granularity**: files

Use name of the parent folder of source file as label.
May be useful for code classification datasets, e.g., POJ-104.

 ```yaml
 name: folder name
 ```

## function name
**granularity**: functions

Use name of each function as label.
This label extractor will also hide the function name in the AST and all recursive calls.

 ```yaml
 name: function name
 ```
