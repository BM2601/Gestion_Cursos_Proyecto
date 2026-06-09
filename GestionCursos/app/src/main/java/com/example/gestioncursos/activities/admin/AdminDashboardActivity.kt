package com.example.gestioncursos.activities.admin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioncursos.R
import com.example.gestioncursos.activities.LoginActivity
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.card.MaterialCardView

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        db = DatabaseHelper(this)

        val nombre = intent.getStringExtra("usuario_nombre") ?: "Admin"
        findViewById<TextView>(R.id.tvNombreAdmin).text = nombre

        actualizarEstadisticas()

        findViewById<MaterialCardView>(R.id.cardGestionCursos).setOnClickListener {
            startActivity(Intent(this, GestionCursosAdminActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardCerrarSesion).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    override fun onResume() { super.onResume(); actualizarEstadisticas() }

    private fun actualizarEstadisticas() {
        val cursos = db.obtenerTodosCursos()
        val activos = cursos.count { it.activo }
        val profesores = db.obtenerProfesores()

        findViewById<TextView>(R.id.tvTotalCursos).text = cursos.size.toString()
        findViewById<TextView>(R.id.tvCursosActivos).text = activos.toString()
        findViewById<TextView>(R.id.tvTotalProfesores).text = profesores.size.toString()
    }
}