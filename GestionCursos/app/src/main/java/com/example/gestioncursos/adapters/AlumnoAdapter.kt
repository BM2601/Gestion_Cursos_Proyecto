package com.example.gestioncursos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.models.Usuario

class AlumnoAdapter(
    private val alumnos: List<Usuario>
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    inner class AlumnoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val tvEmail: TextView = view.findViewById(R.id.tvEmailAlumno)
        val tvIniciales: TextView = view.findViewById(R.id.tvInicialesAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.tvNombre.text = alumno.nombre
        holder.tvEmail.text = alumno.email
        holder.tvIniciales.text = alumno.nombre.take(2).uppercase()
    }

    override fun getItemCount() = alumnos.size
}