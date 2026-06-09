package com.example.gestioncursos.activities.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.adapters.CursoAdapter
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfesorDashboardActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var profesorId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesor_dashboard)
        db = DatabaseHelper(this)

        profesorId = intent.getIntExtra("usuario_id", 0)
        val nombre = intent.getStringExtra("usuario_nombre") ?: "Profesor"

        findViewById<TextView>(R.id.tvBienvenidaProfesor).text = "Bienvenido, $nombre"

        // FAB crear curso
        findViewById<FloatingActionButton>(R.id.fabCrearCurso).setOnClickListener {
            startActivity(Intent(this, CrearEditarCursoActivity::class.java).apply {
                putExtra("profesor_id", profesorId)
                putExtra("modo", "crear")
            })
        }

        // Logout
        findViewById<ImageButton>(R.id.btnLogoutProfesor).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Deseas salir de la sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    startActivity(Intent(this,
                        com.example.gestioncursos.activities.LoginActivity::class.java))
                    finishAffinity()
                }
                .setNegativeButton("No", null)
                .show()
        }

        cargarCursos()
    }

    override fun onResume() {
        super.onResume()
        cargarCursos()
    }

    private fun cargarCursos() {
        val rvCursos = findViewById<RecyclerView>(R.id.rvCursosProfesor)
        rvCursos.layoutManager = LinearLayoutManager(this)

        val cursos = db.obtenerCursosDelProfesor(profesorId)

        rvCursos.adapter = CursoAdapter(
            cursos,
            onItemClick = { curso ->
                startActivity(Intent(this, AlumnosInscritosActivity::class.java).apply {
                    putExtra("curso_id", curso.id)
                    putExtra("curso_titulo", curso.titulo)
                })
            },
            onEditClick = { curso ->
                startActivity(Intent(this, CrearEditarCursoActivity::class.java).apply {
                    putExtra("profesor_id", profesorId)
                    putExtra("curso_id", curso.id)
                    putExtra("modo", "editar")
                })
            },
            onDeleteClick = { curso ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Curso")
                    .setMessage("¿Eliminar '${curso.titulo}'?")
                    .setPositiveButton("Sí") { _, _ ->
                        db.eliminarCurso(curso.id)
                        cargarCursos()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },
            showAdminOptions = true
        )

        // Actualizar contadores del header — van AQUÍ, después de tener `cursos`
        val activos = cursos.count { it.activo }
        findViewById<TextView>(R.id.tvTotalCursos).text = cursos.size.toString()
        findViewById<TextView>(R.id.tvCursosActivos).text = activos.toString()
    }
}