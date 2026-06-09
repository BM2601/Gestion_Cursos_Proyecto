package com.example.gestioncursos.activities.alumno

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioncursos.R
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class DetalleCursoActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_curso)
        db = DatabaseHelper(this)

        val cursoId = intent.getIntExtra("curso_id", 0)
        val curso = db.obtenerTodosCursos().find { it.id == cursoId } ?: return

        // Toolbar con botón volver
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarDetalle)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Datos del curso
        findViewById<TextView>(R.id.tvTituloDetalle).text = curso.titulo
        findViewById<TextView>(R.id.tvDescripcionDetalle).text = curso.descripcion
        findViewById<TextView>(R.id.tvProfesorDetalle).text = "Profesor: ${curso.nombreProfesor}"
        findViewById<TextView>(R.id.tvCategoriaDetalle).text = curso.categoria
        findViewById<TextView>(R.id.tvNivelDetalle).text = curso.nivel
        findViewById<TextView>(R.id.tvDuracionDetalle).text = curso.duracion
        findViewById<Chip>(R.id.chipCategoriaDet).text = curso.categoria

        // Materiales PDF — UNA sola declaración
        val materiales = db.obtenerMaterialesDeCurso(cursoId)

        val tvMateriales = findViewById<TextView>(R.id.tvMateriales)
        val btnDescargar = findViewById<MaterialButton>(R.id.btnDescargarPDF)
        val btnVolver = findViewById<MaterialButton>(R.id.btnVolver)

        if (materiales.isEmpty()) {
            tvMateriales.text = "Sin materiales disponibles aún"
            btnDescargar.isEnabled = false
            btnDescargar.text = "Sin material disponible"
        } else {
            tvMateriales.text = materiales.joinToString("\n") { "📄 ${it.nombreArchivo}" }
            btnDescargar.setOnClickListener {
                val uri = Uri.parse(materiales.first().rutaArchivo)
                val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    startActivity(pdfIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Instala un lector de PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}