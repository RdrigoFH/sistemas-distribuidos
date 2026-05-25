# API de Estudiantes (Flask)

Aplicacion Flask sencilla para administrar una lista en memoria de estudiantes. La API permite registrar, consultar, actualizar y eliminar estudiantes con JSON.

## Requisitos

- Python 3.8+
- Flask

Instalacion rapida:

```bash
pip install flask
```

## Ejecucion

```bash
python app.py
```

La app inicia en:

```
http://localhost:5000
```

## Explicacion del codigo

- Se crea una instancia de Flask con `app = Flask(__name__)`.
- `estudiantes` es una lista en memoria que almacena objetos con `id` autogenerado.
- **POST /estudiantes**: registra un estudiante nuevo y responde 201.
- **GET /estudiantes**: devuelve toda la lista.
- **GET /estudiantes/<int:id>**: consulta un estudiante por `id`.
- **PUT /estudiantes/<int:id>**: actualiza un estudiante por `id`.
- **DELETE /estudiantes/<int:id>**: elimina un estudiante por `id`.
- Validaciones:
  - El cuerpo debe ser JSON.
  - Campos requeridos: `nombre`, `edad`, `carrera`.
  - `edad` debe ser entero.
  - El `id` debe existir.

## Pruebas de API (curl)

### 1) Listar (inicio vacio)

```bash
curl -i http://localhost:5000/estudiantes
```

### 2) Registrar

```bash
curl -i -X POST http://localhost:5000/estudiantes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","edad":21,"carrera":"Sistemas"}'
```

### 3) Listar (con datos)

```bash
curl -i http://localhost:5000/estudiantes
```

### 4) Consultar por id (id 1)

```bash
curl -i http://localhost:5000/estudiantes/1
```

### 5) Actualizar (id 1)

```bash
curl -i -X PUT http://localhost:5000/estudiantes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","edad":22,"carrera":"Sistemas"}'
```

### 6) Eliminar (id 1)

```bash
curl -i -X DELETE http://localhost:5000/estudiantes/1
```

### 7) Error de id

```bash
curl -i -X DELETE http://localhost:5000/estudiantes/999
```

## Notas

- Los datos se pierden al reiniciar la app porque se guardan en memoria.
- Si necesitas persistencia, usa una base de datos.
