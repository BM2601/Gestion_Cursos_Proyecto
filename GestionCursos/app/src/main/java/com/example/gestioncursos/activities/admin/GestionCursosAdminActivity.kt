package com.example.gestioncursos.activities.admin

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.adapters.CursoAdapter
import com.example.gestioncursos.activities.profesor.CrearEditarCursoActivity
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class GestionCursosAdminActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var rvCursos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_cursos_admin)
        db = DatabaseHelper(this)

        rvCursos = findViewById(R.id.rvCursosAdmin)
        rvCursos.layoutManager = LinearLayoutManager(this)

        // FAB crear curso
        findViewById<FloatingActionButton>(R.id.fabCrearCursoAdmin).setOnClickListener {
            startActivity(Intent(this, CrearEditarCursoActivity::class.java).apply {
                putExtra("modo", "crear")
                putExtra("profesor_id", 0)
            })
        }

        // Botón volver
        findViewById<ImageButton>(R.id.btnBackAdmin).setOnClickListener { finish() }

        // Tabs: Todos / Activos / Inactivos
        val tabs = findViewById<TabLayout>(R.id.tabsAdmin)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> cargarCursos()
                    1 -> cargarCursosFiltrados(soloActivos = true)
                    2 -> cargarCursosFiltrados(soloActivos = false)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Filtro texto en tiempo real
        val etFiltro = findViewById<EditText>(R.id.etFiltroAdmin)
        etFiltro.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString()
                val todos = db.obtenerTodosCursos()
                val filtrados = if (query.isEmpty()) todos
                else todos.filter { it.titulo.contains(query, ignoreCase = true) }
                (rvCursos.adapter as? CursoAdapter)?.actualizarLista(filtrados)
                actualizarContador(filtrados.size)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cargarCursos()
    }

    override fun onResume() {
        super.onResume()
        cargarCursos()
    }

    private fun crearAdapter(cursos: List<com.example.gestioncursos.models.Curso>): CursoAdapter {
        return CursoAdapter(
            cursos,
            onItemClick = { curso ->
                val opciones = arrayOf(
                    "Asignar Profesor",
                    if (curso.activo) "Desactivar Curso" else "Activar Curso"
                )
                AlertDialog.Builder(this)
                    .setTitle(curso.titulo)
                    .setItems(opciones) { _, which ->
                        when (which) {
                            0 -> mostrarDialogoAsignarProfesor(curso.id)
                            1 -> {
                                db.toggleEstadoCurso(curso.id, !curso.activo)
                                val msg = if (!curso.activo) "Curso activado" else "Curso desactivado"
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                                cargarCursos()
                            }
                        }
                    }.show()
            },
            onEditClick = { curso ->
                startActivity(Intent(this, CrearEditarCursoActivity::class.java).apply {
                    putExtra("modo", "editar")
                    putExtra("curso_id", curso.id)
                    putExtra("profesor_id", curso.profesorId)
                })
            },
            onDeleteClick = { curso ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Curso")
                    .setMessage("¿Estás seguro de eliminar '${curso.titulo}'?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        db.eliminarCurso(curso.id)
                        Toast.makeText(this, "Curso eliminado", Toast.LENGTH_SHORT).show()
                        cargarCursos()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },
            showAdminOptions = true
        )
    }

    private fun cargarCursos() {
        val cursos = db.obtenerTodosCursos()
        rvCursos.adapter = crearAdapter(cursos)
        actualizarContador(cursos.size)
    }

    private fun cargarCursosFiltrados(soloActivos: Boolean) {
        val cursos = db.obtenerTodosCursos().filter { it.activo == soloActivos }
        rvCursos.adapter = crearAdapter(cursos)
        actualizarContador(cursos.size)
    }

    private fun mostrarDialogoAsignarProfesor(cursoId: Int) {
        val profesores = db.obtenerProfesores()
        if (profesores.isEmpty()) {
            Toast.makeText(this, "No hay profesores disponibles", Toast.LENGTH_SHORT).show()
            return
        }
        val nombres = profesores.map { it.nombre }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Asignar Profesor")
            .setItems(nombres) { _, which ->
                db.asignarProfesor(cursoId, profesores[which].id)
                Toast.makeText(this, "Profesor asignado", Toast.LENGTH_SHORT).show()
                cargarCursos()
            }.show()
    }

    private fun actualizarContador(count: Int) {
        findViewById<TextView>(R.id.tvTotalAdmin).text = "$count cursos"
    }
}