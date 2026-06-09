package com.example.gestioncursos.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioncursos.R
import com.example.gestioncursos.activities.admin.AdminDashboardActivity
import com.example.gestioncursos.activities.alumno.AlumnoDashboardActivity
import com.example.gestioncursos.activities.profesor.ProfesorDashboardActivity
import com.example.gestioncursos.database.DatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = DatabaseHelper(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val usuario = db.login(email, password)
            if (usuario != null) {
                val intent = when (usuario.rol) {
                    "admin" -> Intent(this, AdminDashboardActivity::class.java)
                    "profesor" -> Intent(this, ProfesorDashboardActivity::class.java)
                    else -> Intent(this, AlumnoDashboardActivity::class.java)
                }
                intent.putExtra("usuario_id", usuario.id)
                intent.putExtra("usuario_nombre", usuario.nombre)
                intent.putExtra("usuario_rol", usuario.rol)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}