package com.example.gestioncursos.activities.profesor

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.adapters.AlumnoAdapter
import com.example.gestioncursos.database.DatabaseHelper

class AlumnosInscritosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumnos_inscritos)
        val db = DatabaseHelper(this)

        val cursoId = intent.getIntExtra("curso_id", 0)
        val cursoTitulo = intent.getStringExtra("curso_titulo") ?: ""

        val rvAlumnos = findViewById<RecyclerView>(R.id.rvAlumnos)
        val layoutVacio = findViewById<LinearLayout>(R.id.layoutSinAlumnos)

        findViewById<TextView>(R.id.tvTituloCursoAlumnos).text = cursoTitulo

        val alumnos = db.obtenerAlumnosDelCurso(cursoId)
        rvAlumnos.layoutManager = LinearLayoutManager(this)
        rvAlumnos.adapter = AlumnoAdapter(alumnos)
        findViewById<TextView>(R.id.tvTotalAlumnos).text = "${alumnos.size} alumnos inscritos"

        // Botón volver
        findViewById<ImageButton>(R.id.btnBackAlumnos).setOnClickListener { finish() }

        // Estado vacío
        if (alumnos.isEmpty()) {
            layoutVacio.visibility = View.VISIBLE
            rvAlumnos.visibility = View.GONE
        } else {
            layoutVacio.visibility = View.GONE
            rvAlumnos.visibility = View.VISIBLE
        }
    }
}