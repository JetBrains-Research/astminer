from torch.utils.data import Dataset


class PathMinerDataset(Dataset):

    def __init__(self, loader, indices):
        self.size = len(indices)
        sample = loader.path_contexts.iloc[indices]
        self.labels = sample['project']
        self.ids = sample['id']
        # Convert PathContexts to PyTorch Tensors for further usage
        self.contexts = sample['path_contexts'].map(
            lambda contexts: list(map(lambda ctx: ctx.to_tensor(), contexts))
        )

    def __len__(self):
        return self.size

    def __getitem__(self, index):
        return {'contexts': self.contexts.iloc[index],
                'label': self.labels.iloc[index],
                'id': self.ids.iloc[index]}
