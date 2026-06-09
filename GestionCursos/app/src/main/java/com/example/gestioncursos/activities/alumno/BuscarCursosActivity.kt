package com.example.gestioncursos.activities.alumno

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.adapters.CursoAdapter
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.chip.ChipGroup

class BuscarCursosActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etBuscar: EditText
    private lateinit var rvCursos: RecyclerView
    private lateinit var tvContador: TextView
    private lateinit var layoutVacio: View
    private var alumnoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_cursos)
        db = DatabaseHelper(this)
        alumnoId = intent.getIntExtra("alumno_id", 0)

        // Inicializar vistas UNA sola vez
        etBuscar    = findViewById(R.id.etBuscarCurso)
        rvCursos    = findViewById(R.id.rvCursosDisponibles)
        tvContador  = findViewById(R.id.tvContadorResultados)
        layoutVacio = findViewById(R.id.layoutVacio)

        rvCursos.layoutManager = LinearLayoutManager(this)

        // Botón volver
        findViewById<ImageButton>(R.id.btnBackBuscar).setOnClickListener { finish() }

        // Chips de categoría
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupCategoria)
        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            cargarCursos(etBuscar.text.toString())
        }

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { cargarCursos(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarCursos("")
    }

    private fun cargarCursos(query: String) {
        val cursos = if (query.isEmpty()) db.obtenerCursosActivos()
        else db.buscarCursos(query)

        val adapter = CursoAdapter(cursos, onItemClick = { curso ->
            if (!db.estaInscrito(alumnoId, curso.id)) {
                db.inscribirAlumno(alumnoId, curso.id)
                Toast.makeText(this, "¡Inscrito en ${curso.titulo}!", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, DetalleCursoActivity::class.java).apply {
                    putExtra("curso_id", curso.id)
                    putExtra("alumno_id", alumnoId)
                })
            }
        })

        rvCursos.adapter = adapter

        // Actualizar contador
        tvContador.text = "${cursos.size} resultados"

        // Mostrar/ocultar estado vacío
        layoutVacio.visibility = if (cursos.isEmpty()) View.VISIBLE else View.GONE
        rvCursos.visibility    = if (cursos.isEmpty()) View.GONE    else View.VISIBLE
    }
}