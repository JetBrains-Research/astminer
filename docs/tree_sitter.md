# Tree sitter support

Astimner supports the easy use of tree-sitter grammars through combination of tree-sitter python bindings, our self-written 
translator package and kotlin wrappers. This section explains how to install our package and grammars to begin writing 
kotlin wrappers. 

1) In the root of the project run 
   ```
   pip install src/main/python/parse/tree_sitter
   ```

2) Find repos with grammars you want to use (for example [tree sitter java grammar](https://github.com/tree-sitter/tree-sitter-java))
   or create your own. Then `git clone` them anywhere on your computer.

3) To install grammars in wrapper run
   ```
   aw_tree_sitter -b <path_to_grammar_1> <path_to_grammar_2> <path_to_grammar_n>
   ```
   Be aware that this process IS NOT INCREMENTAL. It means that when you run this command all previous grammars will be 
   replaced by what you type here. After installation, you can delete grammar repos.

4) After this you can use command
   ```
   aw_tree_sitter -l <language> -f <path_to_file>
   ```
   to parse files with your grammars.

Now you can write kotlin wrappers to use grammars you just added in `astminer`. 