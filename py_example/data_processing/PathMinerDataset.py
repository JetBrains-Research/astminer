import numpy as np
import torch
from torch.utils.data import Dataset


# Transforms data, loaded from PathMiner's output, to format suitable for model training.
class PathMinerDataset(Dataset):

    # Converts data to PyTorch Tensors for further usage in the model
    # Number of contexts per file is limited to allow fast computations
    def __init__(self, loader, indices, keep_contexts=200):
        self.size = len(indices)
        sample = loader.path_contexts.iloc[indices]
        self.ids = sample['id']
        self.labels = torch.FloatTensor(sample['project'].values)
        self.starts, self.paths, self.ends = self._cut_contexts(sample['path_contexts'], keep_contexts)

    # Pick random contexts from each file if there are too many of them
    def _cut_contexts(self, all_contexts, keep_contexts):
        starts = np.zeros((self.size, keep_contexts))
        paths = np.zeros((self.size, keep_contexts))
        ends = np.zeros((self.size, keep_contexts))
        for i, contexts in enumerate(all_contexts):
            if len(contexts) > keep_contexts:
                np.random.shuffle(contexts)
                contexts = contexts[:keep_contexts]
            for j, context in enumerate(contexts):
                starts[i, j] = context.start_token
                paths[i, j] = context.path
                ends[i, j] = context.end_token
        return torch.LongTensor(starts), torch.LongTensor(paths), torch.LongTensor(ends)

    def __len__(self):
        return self.size

    def __getitem__(self, index):
        return {'contexts': (self.starts[index], self.paths[index], self.ends[index]),
                'labels': self.labels[index],
                'ids': self.ids.iloc[index]}
