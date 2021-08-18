# Label extractors

Label extractors are required for correct extraction of labels from raw ASTs.
Internally, they extract labels from the tree and process the tree to avoid data leaks.
Also, label extractors define the granularity level for the whole pipeline.

[//]: # "How do you use these labels? What does it replace? Are ALL identifier names are replaced with the SAME label or different abstract labels, e.g. 'identifier'?"

[//]: # "Can I specify in the YAML several label extractors or just one?"

Label extractor config classes are defined in [LabelExtractorConfigs.kt](src/main/kotlin/astminer/config/LabelExtractorConfigs.kt).

## file name
**granularity**: files

Use the file name of the source file as a label.

 ```yaml
 name: file name
 ```

## folder name
**granularity**: files

Use the name of the parent folder of the source file as a label.
May be useful for code classification datasets, e.g., POJ-104.

 ```yaml
 name: folder name
 ```

## function name
**granularity**: functions

Use the name of each function as a label.
This label extractor will also hide the function name in the AST and all recursive calls to prevent data leaks.

 ```yaml
 name: function name
 ```
