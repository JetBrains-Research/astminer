import torch
from torch.nn import functional as F
from torch import nn


# Implementation of code2vec's vectorization part in PyTorch.
# Since it is a PyTorch Module, it can be reused as a part of another pipeline.
class CodeVectorizer(nn.Module):

    def __init__(self, n_tokens, n_paths, dim):
        super(CodeVectorizer, self).__init__()
        self.tokens_embed = nn.Embedding(n_tokens, dim)
        self.paths_embed = nn.Embedding(n_paths, dim)
        self.transform = nn.Sequential(nn.Linear(3 * dim, dim), nn.Tanh())
        self.attention = nn.Linear(dim, 1)
        self.classifier = nn.Sequential(nn.Linear(dim, 1), nn.Sigmoid())

    def forward(self, contexts):
        starts, paths, ends = contexts

        # (batch_size, max_contexts, dim)
        starts = self.tokens_embed(starts)
        paths = self.paths_embed(paths)
        ends = self.tokens_embed(ends)

        # (batch_size, max_contexts, 3 * dim)
        concatenated_contexts = torch.cat((starts, paths, ends), dim=2)
        # (batch_size, max_contexts, dim)
        transformed_contexts = self.transform(concatenated_contexts)
        # (batch_size, max_contexts, 1)
        context_attentions = F.softmax(self.attention(transformed_contexts), dim=1)
        # (batch_size, dim)
        aggregated_context = torch.sum(torch.mul(transformed_contexts, context_attentions), dim=1)

        return aggregated_context
