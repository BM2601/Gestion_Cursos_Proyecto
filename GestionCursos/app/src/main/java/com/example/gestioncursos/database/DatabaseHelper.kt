package com.example.gestioncursos.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gestioncursos.models.Curso
import com.example.gestioncursos.models.Usuario
import com.example.gestioncursos.models.Material

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "gestion_cursos.db"
        const val DATABASE_VERSION = 1

        // Tablas
        const val TABLE_USUARIOS = "usuarios"
        const val TABLE_CURSOS = "cursos"
        const val TABLE_INSCRIPCIONES = "inscripciones"
        const val TABLE_MATERIALES = "materiales"

        // Columnas Usuarios
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_ROL = "rol" // alumno, profesor, admin

        // Columnas Cursos
        const val COL_TITULO = "titulo"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_PROFESOR_ID = "profesor_id"
        const val COL_CATEGORIA = "categoria"
        const val COL_ACTIVO = "activo"
        const val COL_IMAGEN_URL = "imagen_url"
        const val COL_DURACION = "duracion"
        const val COL_NIVEL = "nivel"

        // Columnas Materiales
        const val COL_CURSO_ID = "curso_id"
        const val COL_NOMBRE_ARCHIVO = "nombre_archivo"
        const val COL_RUTA_ARCHIVO = "ruta_archivo"
        const val COL_FECHA_SUBIDA = "fecha_subida"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_EMAIL TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_ROL TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_CURSOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITULO TEXT NOT NULL,
                $COL_DESCRIPCION TEXT,
                $COL_PROFESOR_ID INTEGER,
                $COL_CATEGORIA TEXT,
                $COL_ACTIVO INTEGER DEFAULT 1,
                $COL_IMAGEN_URL TEXT,
                $COL_DURACION TEXT,
                $COL_NIVEL TEXT,
                FOREIGN KEY($COL_PROFESOR_ID) REFERENCES $TABLE_USUARIOS($COL_ID)
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_INSCRIPCIONES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                alumno_id INTEGER,
                $COL_CURSO_ID INTEGER,
                fecha_inscripcion TEXT,
                FOREIGN KEY(alumno_id) REFERENCES $TABLE_USUARIOS($COL_ID),
                FOREIGN KEY($COL_CURSO_ID) REFERENCES $TABLE_CURSOS($COL_ID)
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_MATERIALES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CURSO_ID INTEGER,
                $COL_NOMBRE_ARCHIVO TEXT,
                $COL_RUTA_ARCHIVO TEXT,
                $COL_FECHA_SUBIDA TEXT,
                FOREIGN KEY($COL_CURSO_ID) REFERENCES $TABLE_CURSOS($COL_ID)
            )
        """)

        // Insertar datos de prueba
        insertarDatosPrueba(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MATERIALES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INSCRIPCIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CURSOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    private fun insertarDatosPrueba(db: SQLiteDatabase) {
        // Usuarios de prueba
        val usuarios = listOf(
            ContentValues().apply {
                put(COL_NOMBRE, "Admin Principal"); put(COL_EMAIL, "admin@edu.com")
                put(COL_PASSWORD, "admin123"); put(COL_ROL, "admin")
            },
            ContentValues().apply {
                put(COL_NOMBRE, "Prof. García"); put(COL_EMAIL, "profesor@edu.com")
                put(COL_PASSWORD, "prof123"); put(COL_ROL, "profesor")
            },
            ContentValues().apply {
                put(COL_NOMBRE, "Juan Alumno"); put(COL_EMAIL, "alumno@edu.com")
                put(COL_PASSWORD, "alum123"); put(COL_ROL, "alumno")
            }
        )
        usuarios.forEach { db.insert(TABLE_USUARIOS, null, it) }

        // Cursos de prueba
        val cursos = listOf(
            ContentValues().apply {
                put(COL_TITULO, "Kotlin Avanzado"); put(COL_DESCRIPCION, "Aprende Kotlin desde cero hasta experto")
                put(COL_PROFESOR_ID, 2); put(COL_CATEGORIA, "Programación")
                put(COL_ACTIVO, 1); put(COL_DURACION, "40 horas"); put(COL_NIVEL, "Avanzado")
            },
            ContentValues().apply {
                put(COL_TITULO, "Diseño UI/UX"); put(COL_DESCRIPCION, "Fundamentos del diseño de interfaces")
                put(COL_PROFESOR_ID, 2); put(COL_CATEGORIA, "Diseño")
                put(COL_ACTIVO, 1); put(COL_DURACION, "25 horas"); put(COL_NIVEL, "Intermedio")
            },
            ContentValues().apply {
                put(COL_TITULO, "Base de Datos SQL"); put(COL_DESCRIPCION, "Manejo completo de bases de datos")
                put(COL_PROFESOR_ID, 2); put(COL_CATEGORIA, "Datos")
                put(COL_ACTIVO, 0); put(COL_DURACION, "30 horas"); put(COL_NIVEL, "Básico")
            }
        )
        cursos.forEach { db.insert(TABLE_CURSOS, null, it) }

        // Inscripción de prueba
        val inscripcion = ContentValues().apply {
            put("alumno_id", 3); put(COL_CURSO_ID, 1); put("fecha_inscripcion", "2024-01-15")
        }
        db.insert(TABLE_INSCRIPCIONES, null, inscripcion)
    }

    // ============ USUARIOS ============
    fun login(email: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COL_EMAIL=? AND $COL_PASSWORD=?",
            arrayOf(email, password)
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL))
            )
        }
        cursor.close()
        return usuario
    }

    fun obtenerTodosUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIOS", null)
        while (cursor.moveToNext()) {
            lista.add(Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL))
            ))
        }
        cursor.close()
        return lista
    }

    fun obtenerProfesores(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COL_ROL='profesor'", null)
        while (cursor.moveToNext()) {
            lista.add(Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                rol = "profesor"
            ))
        }
        cursor.close()
        return lista
    }

    // ============ CURSOS ============
    fun obtenerTodosCursos(): List<Curso> {
        val lista = mutableListOf<Curso>()
        val db = readableDatabase
        val query = """
            SELECT c.*, u.$COL_NOMBRE as nombre_profesor
            FROM $TABLE_CURSOS c
            LEFT JOIN $TABLE_USUARIOS u ON c.$COL_PROFESOR_ID = u.$COL_ID
        """
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            lista.add(cursorToCurso(cursor))
        }
        cursor.close()
        return lista
    }

    fun obtenerCursosActivos(): List<Curso> {
        val lista = mutableListOf<Curso>()
        val db = readableDatabase
        val query = """
            SELECT c.*, u.$COL_NOMBRE as nombre_profesor
            FROM $TABLE_CURSOS c
            LEFT JOIN $TABLE_USUARIOS u ON c.$COL_PROFESOR_ID = u.$COL_ID
            WHERE c.$COL_ACTIVO = 1
        """
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            lista.add(cursorToCurso(cursor))
        }
        cursor.close()
        return lista
    }

    fun obtenerCursosDelProfesor(profesorId: Int): List<Curso> {
        val lista = mutableListOf<Curso>()
        val db = readableDatabase
        val query = """
            SELECT c.*, u.$COL_NOMBRE as nombre_profesor
            FROM $TABLE_CURSOS c
            LEFT JOIN $TABLE_USUARIOS u ON c.$COL_PROFESOR_ID = u.$COL_ID
            WHERE c.$COL_PROFESOR_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(profesorId.toString()))
        while (cursor.moveToNext()) {
            lista.add(cursorToCurso(cursor))
        }
        cursor.close()
        return lista
    }

    fun obtenerCursosDelAlumno(alumnoId: Int): List<Curso> {
        val lista = mutableListOf<Curso>()
        val db = readableDatabase
        val query = """
            SELECT c.*, u.$COL_NOMBRE as nombre_profesor
            FROM $TABLE_CURSOS c
            LEFT JOIN $TABLE_USUARIOS u ON c.$COL_PROFESOR_ID = u.$COL_ID
            INNER JOIN $TABLE_INSCRIPCIONES i ON c.$COL_ID = i.$COL_CURSO_ID
            WHERE i.alumno_id = ?
        """
        val cursor = db.rawQuery(query, arrayOf(alumnoId.toString()))
        while (cursor.moveToNext()) {
            lista.add(cursorToCurso(cursor))
        }
        cursor.close()
        return lista
    }

    fun buscarCursos(query: String): List<Curso> {
        val lista = mutableListOf<Curso>()
        val db = readableDatabase
        val sql = """
            SELECT c.*, u.$COL_NOMBRE as nombre_profesor
            FROM $TABLE_CURSOS c
            LEFT JOIN $TABLE_USUARIOS u ON c.$COL_PROFESOR_ID = u.$COL_ID
            WHERE c.$COL_ACTIVO = 1
            AND (c.$COL_TITULO LIKE ? OR c.$COL_DESCRIPCION LIKE ? OR c.$COL_CATEGORIA LIKE ?)
        """
        val param = "%$query%"
        val cursor = db.rawQuery(sql, arrayOf(param, param, param))
        while (cursor.moveToNext()) {
            lista.add(cursorToCurso(cursor))
        }
        cursor.close()
        return lista
    }

    fun insertarCurso(curso: Curso): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITULO, curso.titulo)
            put(COL_DESCRIPCION, curso.descripcion)
            put(COL_PROFESOR_ID, curso.profesorId)
            put(COL_CATEGORIA, curso.categoria)
            put(COL_ACTIVO, if (curso.activo) 1 else 0)
            put(COL_DURACION, curso.duracion)
            put(COL_NIVEL, curso.nivel)
        }
        return db.insert(TABLE_CURSOS, null, values)
    }

    fun actualizarCurso(curso: Curso): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITULO, curso.titulo)
            put(COL_DESCRIPCION, curso.descripcion)
            put(COL_PROFESOR_ID, curso.profesorId)
            put(COL_CATEGORIA, curso.categoria)
            put(COL_ACTIVO, if (curso.activo) 1 else 0)
            put(COL_DURACION, curso.duracion)
            put(COL_NIVEL, curso.nivel)
        }
        return db.update(TABLE_CURSOS, values, "$COL_ID=?", arrayOf(curso.id.toString()))
    }

    fun eliminarCurso(cursoId: Int): Int {
        val db = writableDatabase
        db.delete(TABLE_INSCRIPCIONES, "$COL_CURSO_ID=?", arrayOf(cursoId.toString()))
        db.delete(TABLE_MATERIALES, "$COL_CURSO_ID=?", arrayOf(cursoId.toString()))
        return db.delete(TABLE_CURSOS, "$COL_ID=?", arrayOf(cursoId.toString()))
    }

    fun toggleEstadoCurso(cursoId: Int, activo: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply { put(COL_ACTIVO, if (activo) 1 else 0) }
        return db.update(TABLE_CURSOS, values, "$COL_ID=?", arrayOf(cursoId.toString()))
    }

    fun asignarProfesor(cursoId: Int, profesorId: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply { put(COL_PROFESOR_ID, profesorId) }
        return db.update(TABLE_CURSOS, values, "$COL_ID=?", arrayOf(cursoId.toString()))
    }

    // ============ INSCRIPCIONES ============
    fun inscribirAlumno(alumnoId: Int, cursoId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("alumno_id", alumnoId)
            put(COL_CURSO_ID, cursoId)
            put("fecha_inscripcion", java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault()).format(java.util.Date()))
        }
        return db.insert(TABLE_INSCRIPCIONES, null, values)
    }

    fun estaInscrito(alumnoId: Int, cursoId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID FROM $TABLE_INSCRIPCIONES WHERE alumno_id=? AND $COL_CURSO_ID=?",
            arrayOf(alumnoId.toString(), cursoId.toString())
        )
        val result = cursor.moveToFirst()
        cursor.close()
        return result
    }

    fun obtenerAlumnosDelCurso(cursoId: Int): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val query = """
            SELECT u.* FROM $TABLE_USUARIOS u
            INNER JOIN $TABLE_INSCRIPCIONES i ON u.$COL_ID = i.alumno_id
            WHERE i.$COL_CURSO_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(cursoId.toString()))
        while (cursor.moveToNext()) {
            lista.add(Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                rol = "alumno"
            ))
        }
        cursor.close()
        return lista
    }

    // ============ MATERIALES ============
    fun insertarMaterial(material: Material): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CURSO_ID, material.cursoId)
            put(COL_NOMBRE_ARCHIVO, material.nombreArchivo)
            put(COL_RUTA_ARCHIVO, material.rutaArchivo)
            put(COL_FECHA_SUBIDA, material.fechaSubida)
        }
        return db.insert(TABLE_MATERIALES, null, values)
    }

    fun obtenerMaterialesDeCurso(cursoId: Int): List<Material> {
        val lista = mutableListOf<Material>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MATERIALES WHERE $COL_CURSO_ID=?",
            arrayOf(cursoId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(Material(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursoId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CURSO_ID)),
                nombreArchivo = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_ARCHIVO)),
                rutaArchivo = cursor.getString(cursor.getColumnIndexOrThrow(COL_RUTA_ARCHIVO)),
                fechaSubida = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_SUBIDA))
            ))
        }
        cursor.close()
        return lista
    }

    fun eliminarMaterial(materialId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_MATERIALES, "$COL_ID=?", arrayOf(materialId.toString()))
    }

    private fun cursorToCurso(cursor: android.database.Cursor): Curso {
        return Curso(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITULO)),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION)) ?: "",
            profesorId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROFESOR_ID)),
            nombreProfesor = try { cursor.getString(cursor.getColumnIndexOrThrow("nombre_profesor")) } catch (e: Exception) { "" },
            categoria = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORIA)) ?: "",
            activo = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVO)) == 1,
            duracion = cursor.getString(cursor.getColumnIndexOrThrow(COL_DURACION)) ?: "",
            nivel = cursor.getString(cursor.getColumnIndexOrThrow(COL_NIVEL)) ?: ""
        )
    }
}