# Label extractors

The label extractor module extracts labels from AST trees and processes them to avoid data leaks.
You can assign a certain label type to each tree.
Also, the selected type defines the granularity level of label extraction for the whole pipeline.

You can define only one label extractor or skip this module at all. Then identifier names will be preserved.

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
