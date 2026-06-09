package com.example.gestioncursos.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncursos.R
import com.example.gestioncursos.models.Curso
import com.google.android.material.chip.Chip

class CursoAdapter(
    private var cursos: List<Curso>,
    private val onItemClick: (Curso) -> Unit,
    private val onEditClick: ((Curso) -> Unit)? = null,
    private val onDeleteClick: ((Curso) -> Unit)? = null,
    private val showAdminOptions: Boolean = false
) : RecyclerView.Adapter<CursoAdapter.CursoViewHolder>() {

    inner class CursoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardCurso)
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloCurso)
        val tvProfesor: TextView = view.findViewById(R.id.tvProfesorCurso)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionCurso)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoriaCurso)
        val tvNivel: TextView = view.findViewById(R.id.tvNivelCurso)
        val tvDuracion: TextView = view.findViewById(R.id.tvDuracionCurso)
        val chipEstado: Chip = view.findViewById(R.id.chipEstado)
        val btnEdit: View = view.findViewById(R.id.btnEditarCurso)
        val btnDelete: View = view.findViewById(R.id.btnEliminarCurso)
        val adminActions: View = view.findViewById(R.id.layoutAdminActions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = cursos[position]
        holder.tvTitulo.text = curso.titulo
        holder.tvProfesor.text = "Prof. ${curso.nombreProfesor}"
        holder.tvDescripcion.text = curso.descripcion
        holder.tvCategoria.text = curso.categoria
        holder.tvNivel.text = curso.nivel
        holder.tvDuracion.text = curso.duracion

        holder.chipEstado.apply {
            text = if (curso.activo) "Activo" else "Inactivo"
            setChipBackgroundColorResource(
                if (curso.activo) R.color.success else R.color.danger
            )
            setTextColor(Color.WHITE)
        }

        holder.adminActions.visibility = if (showAdminOptions) View.VISIBLE else View.GONE

        holder.card.setOnClickListener { onItemClick(curso) }
        holder.btnEdit.setOnClickListener { onEditClick?.invoke(curso) }
        holder.btnDelete.setOnClickListener { onDeleteClick?.invoke(curso) }
    }

    override fun getItemCount() = cursos.size

    fun actualizarLista(nuevaLista: List<Curso>) {
        cursos = nuevaLista
        notifyDataSetChanged()
    }
}