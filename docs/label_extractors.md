# Label extractors

The label extractor module defines the logic of assigning labels to each AST.
The selected label type also defines the granularity level of label extraction for the whole pipeline. Currently, 3 types of labels are supported. You can specify only one.

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

 ```yaml
 name: function name
 ```

> If a function name is used as the label, the module additionally processes the AST to avoid data leaks. It looks for all recursive calls of this function and replaces the function name in the `token` value of the respective vertices with `METHOD_NAME`.