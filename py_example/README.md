# PathMiner: usage

Example of PathMiner's output usage written in [PyTorch](https://pytorch.org/).

## Getting started

To train a toy model on your local machine follow the instructions.

### Setup python environment

Conda users:

~~~~
conda install pytorch torchvision -c pytorch
conda install pandas
conda install parse -c conda-forge
conda install scikit-learn -c anaconda
~~~~

Pip users:
~~~~
pip install torch torchvision
pip install pandas parse scikit-learn
~~~~

### Load data

Data will be loaded as one of the steps of `run_example.py` script. 
However, to use custom projects you can put them in `data/` folder
as `project1` and `project2`.

### Run the example

To run the example execute `run_example.py` script. 
Data loading and processing take several minutes.


## Useful modules

### Data processing

In `data_processing` package you can find several classes that are capable of loading
PathMiner's generated output into easy-to-integrate format.

### Model

`model/Code2Vec` contains a model to vectorize snippets of code based on their path-context representation.
model that vectorizes snippets of code based on their path-contexts representation. 
It is implemented as a PyTorch module and can be easily reused.

A usage example can be found in `model/ProjectClassifier`.
It is a linear classifier that decides from which project does file come from based on file's vectorization.
