import torch
from torch import nn
from model.Code2Vec import Code2Vec


# Classifier distinguishing files between two projects based on code2vec vectorization for files.
class ProjectClassifier(nn.Module):

    def __init__(self, n_tokens, n_paths, dim):
        super(ProjectClassifier, self).__init__()
        self.vectorization = Code2Vec(n_tokens, n_paths, dim)
        self.classifier = nn.Linear(dim, 1)

    def forward(self, contexts):
        vectorized_contexts = self.vectorization(contexts)
        predictions = torch.sigmoid(self.classifier(vectorized_contexts))
        predictions = predictions.squeeze(-1)
        return predictions
