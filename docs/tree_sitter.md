# Tree sitter support

Astminer supports easy use of tree sitter grammars through python bindings. 
Astminer has some wrappers already implemented. This section explains how to install grammars for these wrappers 
(or how to add new grammars for use in your own wrappers).

1) Install tree sitter python bindings with `pip install tree-sitter`.
2) Navigate to `src/main/python/parse/tree_sitter`. You should see files `main.py` and `ast.py`.
3) Find repos with grammars you want to use (for example [tree sitter java grammar](https://github.com/tree-sitter/tree-sitter-java))
or create your own.
4) Git clone them into `tree_sitter` folder.
5) Run `main.py -b <grammar folder name>`. If you want to use more than one grammar then git clone multiple repos with grammars
   and run `python3 main.py -b <grammar folder 1> <grammar folder 2> <grammar folder n>` and so on. Now you should see file `my-languages.so` in the `build` folder. 
After you have built the library, the grammar folders can be deleted.
6) Copy `tree_sitter` folder in `tmp` in the root folder.