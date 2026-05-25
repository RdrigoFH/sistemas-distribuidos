from flask import Flask, request, jsonify
from flasgger import Swagger

app = Flask(__name__)

# Configuración base para Swagger
swagger_template = {
    "swagger": "2.0",
    "info": {
        "title": "API de Estudiantes",
        "description": "API para administrar estudiantes (demo Flasgger)",
        "version": "1.0"
    },
    "basePath": "/",
    "schemes": ["http"]
}

swagger = Swagger(app, template=swagger_template)

estudiantes = []
next_id = 1

def validar_payload(payload):
    required = ["nombre", "edad", "carrera"]
    for key in required:
        if key not in payload:
            return f"campo requerido: {key}"
    if not isinstance(payload.get("edad"), int):
        return "campo edad debe ser entero"
    return None

def buscar_por_id(estudiante_id):
    for estudiante in estudiantes:
        if estudiante["id"] == estudiante_id:
            return estudiante
    return None

@app.route("/estudiantes", methods=["GET"])
def listar():
    """
    Listar todos los estudiantes
    ---
    responses:
      200:
        description: Lista de estudiantes
        schema:
          type: array
          items:
            type: object
            properties:
              id:
                type: integer
              nombre:
                type: string
              edad:
                type: integer
              carrera:
                type: string
    """
    return jsonify(estudiantes)

@app.route("/estudiantes", methods=["POST"])
def registrar():
    """
    Registrar un nuevo estudiante
    ---
    parameters:
      - in: body
        name: body
        required: true
        schema:
          type: object
          required:
            - nombre
            - edad
            - carrera
          properties:
            nombre:
              type: string
            edad:
              type: integer
            carrera:
              type: string
    responses:
      201:
        description: Estudiante creado
        schema:
          type: object
          properties:
            id:
              type: integer
            nombre:
              type: string
            edad:
              type: integer
            carrera:
              type: string
    """
    global next_id
    if not request.is_json:
        return jsonify({"error": "JSON requerido"}), 400

    payload = request.json
    error = validar_payload(payload)
    if error:
        return jsonify({"error": error}), 400

    estudiante = {
        "id": next_id,
        "nombre": payload["nombre"],
        "edad": payload["edad"],
        "carrera": payload["carrera"],
    }
    next_id += 1
    estudiantes.append(estudiante)
    return jsonify(estudiante), 201

@app.route("/estudiantes/<int:estudiante_id>", methods=["GET"])
def consultar(estudiante_id):
    """
    Consultar estudiante por id
    ---
    parameters:
      - in: path
        name: estudiante_id
        type: integer
        required: true
        description: ID del estudiante
    responses:
      200:
        description: Estudiante encontrado
        schema:
          type: object
          properties:
            id:
              type: integer
            nombre:
              type: string
            edad:
              type: integer
            carrera:
              type: string
      404:
        description: No encontrado
    """
    estudiante = buscar_por_id(estudiante_id)
    if not estudiante:
        return jsonify({"error": "estudiante no encontrado"}), 404

    return jsonify(estudiante)

@app.route("/estudiantes/<int:estudiante_id>", methods=["PUT"])
def actualizar(estudiante_id):
    """
    Actualizar estudiante por id
    ---
    parameters:
      - in: path
        name: estudiante_id
        type: integer
        required: true
        description: ID del estudiante
      - in: body
        name: body
        required: true
        schema:
          type: object
          required:
            - nombre
            - edad
            - carrera
          properties:
            nombre:
              type: string
            edad:
              type: integer
            carrera:
              type: string
    responses:
      200:
        description: Estudiante actualizado
        schema:
          type: object
          properties:
            id:
              type: integer
            nombre:
              type: string
            edad:
              type: integer
            carrera:
              type: string
      404:
        description: No encontrado
    """
    estudiante = buscar_por_id(estudiante_id)
    if not estudiante:
        return jsonify({"error": "estudiante no encontrado"}), 404
    if not request.is_json:
        return jsonify({"error": "JSON requerido"}), 400

    payload = request.json
    error = validar_payload(payload)
    if error:
        return jsonify({"error": error}), 400

    estudiante["nombre"] = payload["nombre"]
    estudiante["edad"] = payload["edad"]
    estudiante["carrera"] = payload["carrera"]
    return jsonify(estudiante)

@app.route("/estudiantes/<int:estudiante_id>", methods=["DELETE"])
def eliminar(estudiante_id):
    """
    Eliminar estudiante por id
    ---
    parameters:
      - in: path
        name: estudiante_id
        type: integer
        required: true
        description: ID del estudiante
    responses:
      200:
        description: Estudiante eliminado
        schema:
          type: object
          properties:
            eliminado:
              type: boolean
      404:
        description: No encontrado
    """
    estudiante = buscar_por_id(estudiante_id)
    if not estudiante:
        return jsonify({"error": "estudiante no encontrado"}), 404

    estudiantes.remove(estudiante)
    return jsonify({"eliminado": True})

if __name__ == "__main__":
    print("Swagger UI disponible en http://localhost:5000/apidocs")
    app.run(host="0.0.0.0", port=5000, debug=True)