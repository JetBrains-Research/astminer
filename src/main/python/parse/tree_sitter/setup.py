from setuptools import setup

setup(
    name="tree_sitter_astminer_wrapper",
    version="1.0.0",
    description="Wrapper for tree sitter python bindings for using with astminer",
    packages=["aw_tree_sitter"],
    license="MIT",
    author="Ilya Utkin",
    entry_points={"console_scripts": ["aw_tree_sitter = aw_tree_sitter.main:main"]},
    install_requires=["tree_sitter~=0.19.0"],
)
