from flask import Blueprint, request, jsonify
from app.services.EmbedderService import embed_data

api_blueprint = Blueprint('api', __name__)

@api_blueprint.route('/embed', methods=['POST'])
def embed():
    data = request.json
    text = data['query']
    vector = embed_data(text)
    return jsonify(vector)