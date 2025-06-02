from sentence_transformers import SentenceTransformer
from ..config import MODEL_NAME

model = SentenceTransformer(MODEL_NAME)

def embed_data(sentence):

    embeddings = model.encode(sentence).tolist()
    return embeddings