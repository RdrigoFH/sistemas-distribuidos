from flask import Flask, jsonify, request 
from flask_cors import CORS

app = Flask(__name__) 
CORS(app)

productos = [ 
    {"id": 1, "nombre": "Laptop"}, 
    {"id": 2, "nombre": "Mouse"} 
] 

@app.route('/productos', methods=['GET']) 
def obtener(): 
    return jsonify(productos) 

@app.route('/productos', methods=['POST']) 
def agregar(): 
    data = request.json 
    productos.append(data) 
    return jsonify({"mensaje": "Producto agregado"}), 201 

@app.route('/productos/<int:id>', methods=['PUT'])
def actualizar(id):
    data = request.json
    # Buscamos el producto por ID
    producto = next((p for p in productos if p["id"] == id), None)
    
    if producto:
        # Actualizamos solo el nombre si viene en el JSON
        producto['nombre'] = data.get('nombre', producto['nombre'])
        return jsonify({"mensaje": "Producto actualizado", "producto": producto})
    
    return jsonify({"mensaje": "Producto no encontrado"}), 404

@app.route('/productos/<int:id>', methods=['DELETE']) 
def eliminar(id): 
    global productos 
    # Verificamos si existe antes de decir que lo borramos
    inicial = len(productos)
    productos = [p for p in productos if p["id"] != id]
    
    if len(productos) < inicial:
        return jsonify({"mensaje": "Producto eliminado"})
    
    return jsonify({"mensaje": "Producto no encontrado"}), 404

if __name__ == '__main__': 
    app.run(debug=True)