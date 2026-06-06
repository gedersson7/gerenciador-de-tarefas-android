package br.edu.faculdade.imepac_gedersson

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TarefaAdapter(
    private val listaTarefas: ArrayList<Tarefa>,
    private val isConcluida: Boolean = false,
    private val onDeleteClick: ((Tarefa, Int) -> Unit)? = null,
    private val onConcluirClick: ((Tarefa, Int) -> Unit)? = null
): RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    class TarefaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txtItemTitulo)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtItemDescricao)
        val txtStatus: TextView = itemView.findViewById(R.id.txtItemStatus)
        val btnDeletar: ImageView = itemView.findViewById(R.id.btnDeletarTarefa)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarTarefa)
        val btnConcluir: ImageView = itemView.findViewById(R.id.btnConcluirTarefa)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtItemCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarefa, parent, false)
        return TarefaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        val tarefa = listaTarefas[position]
        holder.txtTitulo.text = tarefa.titulo
        holder.txtDescricao.text = tarefa.descricao
        holder.txtStatus.text = tarefa.status.uppercase()

        val categoriaText = tarefa.categoria ?: "Outros"

        val (textoComEmoji, corFundo, corTexto) = when (categoriaText) {
            "Desenvolvimento" -> Triple("💻 Desenvolvimento", "#E3F2FD", "#1976D2") // Azul
            "Faculdade" ->       Triple("📚 Faculdade",       "#F3E5F5", "#7B1FA2") // Roxo
            "Treino / Saúde" ->  Triple("💪 Treino / Saúde",  "#E8F5E9", "#388E3C") // Verde
            "Trabalho" ->        Triple("💼 Trabalho",        "#FFF3E0", "#F57C00") // Laranja
            "Casa" ->            Triple("🏠 Casa",            "#E0F2F1", "#00796B") // Teal
            "Lazer" ->           Triple("🎮 Lazer",           "#FFEBEE", "#D32F2F") // Vermelho
            else ->              Triple("📌 Outros",          "#F5F5F5", "#757575") // Cinza
        }

        holder.txtCategoria.text = textoComEmoji
        holder.txtCategoria.setTextColor(Color.parseColor(corTexto))
        holder.txtCategoria.backgroundTintList = ColorStateList.valueOf(Color.parseColor(corFundo))
        // -----------------------------------------------------------

        if (isConcluida) {
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"))

            holder.btnDeletar.visibility = View.VISIBLE
            holder.btnEditar.visibility = View.GONE
            holder.btnConcluir.visibility = View.GONE

            holder.btnDeletar.setOnClickListener {
                onDeleteClick?.invoke(tarefa, position)
            }

            holder.itemView.setOnClickListener(null)
        } else {
            holder.txtStatus.setTextColor(Color.parseColor("#FF9800"))

            holder.btnDeletar.visibility = View.VISIBLE
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnConcluir.visibility = View.VISIBLE

            holder.btnConcluir.setOnClickListener {
                onConcluirClick?.invoke(tarefa, position)
            }

            holder.btnEditar.setOnClickListener { view ->
                val intent = Intent(view.context, TelaDetalhesTarefa::class.java)
                intent.putExtra("idTarefa", tarefa.id)
                view.context.startActivity(intent)
            }

            holder.btnDeletar.setOnClickListener {
                onDeleteClick?.invoke(tarefa, position)
            }

            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = listaTarefas.size
}