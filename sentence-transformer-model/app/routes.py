from flask import Blueprint, request, jsonify
from app.services.EmbedderService import embed_data

api_blueprint = Blueprint('api', __name__)

@api_blueprint.route('/batch-embed', methods=['POST'])
def batch_embed():

    data = request.json
    texts = data['texts']
    embedded_texts = embed_data(texts)
    return jsonify(embedded_texts)


@api_blueprint.route('/embed', methods=['GET'])
def embed():

    text = request.args.get('data')
    embedding_text = embed_data(text)
    return jsonify(embedding_text)

