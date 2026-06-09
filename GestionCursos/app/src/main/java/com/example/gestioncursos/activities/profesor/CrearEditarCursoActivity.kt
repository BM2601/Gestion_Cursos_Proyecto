package com.example.gestioncursos.activities.profesor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioncursos.R
import com.example.gestioncursos.database.DatabaseHelper
import com.example.gestioncursos.models.Curso
import com.example.gestioncursos.models.Material
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class CrearEditarCursoActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var etCategoria: TextInputEditText
    private lateinit var etDuracion: TextInputEditText
    private lateinit var spinnerNivel: Spinner
    private var profesorId = 0
    private var cursoId = 0
    private var modo = "crear"
    private var pdfUri: Uri? = null
    private val PDF_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_editar_curso)
        db = DatabaseHelper(this)

        profesorId = intent.getIntExtra("profesor_id", 0)
        cursoId = intent.getIntExtra("curso_id", 0)
        modo = intent.getStringExtra("modo") ?: "crear"

        etTitulo = findViewById(R.id.etTituloCurso)
        etDescripcion = findViewById(R.id.etDescripcionCurso)
        etCategoria = findViewById(R.id.etCategoriaCurso)
        etDuracion = findViewById(R.id.etDuracionCurso)
        spinnerNivel = findViewById(R.id.spinnerNivel)

        val niveles = arrayOf("Básico", "Intermedio", "Avanzado")
        spinnerNivel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, niveles)

        val tvTitulo = findViewById<TextView>(R.id.tvTituloForm)
        tvTitulo.text = if (modo == "crear") "Crear Nuevo Curso" else "Editar Curso"

        if (modo == "editar" && cursoId > 0) {
            val curso = db.obtenerTodosCursos().find { it.id == cursoId }
            curso?.let {
                etTitulo.setText(it.titulo)
                etDescripcion.setText(it.descripcion)
                etCategoria.setText(it.categoria)
                etDuracion.setText(it.duracion)
                val nivelIdx = niveles.indexOf(it.nivel)
                if (nivelIdx >= 0) spinnerNivel.setSelection(nivelIdx)
            }
        }

        findViewById<MaterialButton>(R.id.btnSeleccionarPDF).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "application/pdf" }
            startActivityForResult(intent, PDF_REQUEST)
        }

        findViewById<MaterialButton>(R.id.btnGuardarCurso).setOnClickListener {
            guardarCurso()
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            pdfUri = data?.data
            val tvPdf = findViewById<TextView>(R.id.tvNombrePDF)
            tvPdf.text = pdfUri?.lastPathSegment ?: "PDF seleccionado"
        }
    }

    private fun guardarCurso() {
        val titulo = etTitulo.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val categoria = etCategoria.text.toString().trim()
        val duracion = etDuracion.text.toString().trim()
        val nivel = spinnerNivel.selectedItem.toString()

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Título y descripción son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val curso = Curso(
            id = cursoId,
            titulo = titulo,
            descripcion = descripcion,
            profesorId = profesorId,
            categoria = categoria,
            activo = true,
            duracion = duracion,
            nivel = nivel
        )

        if (modo == "crear") {
            val id = db.insertarCurso(curso)
            // Si hay PDF, guardar material
            pdfUri?.let { uri ->
                val nombreArchivo = uri.lastPathSegment ?: "material.pdf"
                val material = Material(
                    cursoId = id.toInt(),
                    nombreArchivo = nombreArchivo,
                    rutaArchivo = uri.toString(),
                    fechaSubida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                db.insertarMaterial(material)
            }
            Toast.makeText(this, "Curso creado exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            db.actualizarCurso(curso)
            Toast.makeText(this, "Curso actualizado", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}