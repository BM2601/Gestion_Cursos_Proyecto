package com.example.gestioncursos.activities.alumno

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.adapters.CursoAdapter
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class AlumnoDashboardActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvCursos: RecyclerView
    private lateinit var tvBienvenida: TextView
    private var alumnoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno_dashboard)
        db = DatabaseHelper(this)

        alumnoId = intent.getIntExtra("usuario_id", 0)
        val nombre = intent.getStringExtra("usuario_nombre") ?: "Alumno"

        tvBienvenida = findViewById(R.id.tvBienvenida)
        rvCursos = findViewById(R.id.rvMisCursos)
        val fabBuscar = findViewById<ExtendedFloatingActionButton>(R.id.fabBuscarCursos)

        tvBienvenida.text = "¡Hola, $nombre! 👋"
        rvCursos.layoutManager = LinearLayoutManager(this)

        cargarMisCursos()

        fabBuscar.setOnClickListener {
            startActivity(Intent(this, BuscarCursosActivity::class.java).apply {
                putExtra("alumno_id", alumnoId)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMisCursos()
    }

    private fun cargarMisCursos() {
        val cursos = db.obtenerCursosDelAlumno(alumnoId)
        val adapter = CursoAdapter(cursos, onItemClick = { curso ->
            startActivity(Intent(this, DetalleCursoActivity::class.java).apply {
                putExtra("curso_id", curso.id)
                putExtra("alumno_id", alumnoId)
            })
        })
        rvCursos.adapter = adapter
        val tvCount = findViewById<TextView>(R.id.tvCursosCount)
        tvCount.text = "${cursos.size} cursos inscritos"
    }
}