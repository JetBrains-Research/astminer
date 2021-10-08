# Tree sitter support

Astminer supports easy use of tree sitter grammars through python bindings. 
Astminer has some wrappers already implemented. This section explains how to install grammars for these wrappers 
(or how to add new grammars for use in your own wrappers).

1) In the root of the project run 
   ```
   pip install src/main/python/parse/tree_sitter
   ```
   This package is able to parse file with tree sitter and translate it in the
   format understandable to `astminer`. 

3) Find repos with grammars you want to use (for example [tree sitter java grammar](https://github.com/tree-sitter/tree-sitter-java))
   or create your own. Then `git clone` them anywhere on your computer.

4) To install grammars in wrapper run
   ```
   aw_tree_sitter -b <path_to_grammar_1> <path_to_grammar_2> <path_to_grammar_n>
   ```
   Be aware that this process IS NOT INCREMENTAL. It means that when you run this command all previous grammars will be 
   replaced by what you type here. After installation, you can delete grammar repos.

5) After this you can use command
   ```
   aw_tree_sitter -l <language> -f <path_to_file>
   ```
   to parse files with your grammars! For more information what to do next check tree sitter wrapper for java
   in `src/main/kotlin/parse/tree_sitter/kotlin`