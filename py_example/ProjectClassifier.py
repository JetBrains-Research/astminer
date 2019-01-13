import torch
from torch import nn
from Code2Vec import Code2Vec


# Vectorizes code snippets paths in format of contexts via Code2Vec and performs classification
class ProjectClassifier(nn.Module):

    def __init__(self, n_tokens, n_paths, dim):
        super(ProjectClassifier, self).__init__()
        self.model = nn.Sequential(Code2Vec(n_tokens, n_paths, dim), nn.Linear(dim, 1), nn.Sigmoid())

    def forward(self, contexts):
        return self.model(contexts)
