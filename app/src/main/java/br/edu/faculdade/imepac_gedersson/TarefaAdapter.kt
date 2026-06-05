package br.edu.faculdade.imepac_gedersson

import android.content.Intent
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
    // Adicionado o parâmetro para tratar o clique do botão de concluir
    private val onConcluirClick: ((Tarefa, Int) -> Unit)? = null
): RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    class TarefaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txtItemTitulo)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtItemDescricao)
        val txtStatus: TextView = itemView.findViewById(R.id.txtItemStatus)
        val btnDeletar: ImageView = itemView.findViewById(R.id.btnDeletarTarefa)
        // MAPEAMENTO DOS NOVOS BOTÕES
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarTarefa)
        val btnConcluir: ImageView = itemView.findViewById(R.id.btnConcluirTarefa)
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

        if (isConcluida) {
            // TELA DE CONCLUÍDAS: Cor verde, mostra apenas a lixeira
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"))

            holder.btnDeletar.visibility = View.VISIBLE
            holder.btnEditar.visibility = View.GONE
            holder.btnConcluir.visibility = View.GONE

            holder.btnDeletar.setOnClickListener {
                onDeleteClick?.invoke(tarefa, position)
            }

            holder.itemView.setOnClickListener(null)
        } else {
            // TELA PRINCIPAL (PENDENTES): Cor laranja, mostra todos os botões funcionais
            holder.txtStatus.setTextColor(Color.parseColor("#FF9800"))

            holder.btnDeletar.visibility = View.VISIBLE
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnConcluir.visibility = View.VISIBLE

            // Ação do botão Concluir (Check)
            holder.btnConcluir.setOnClickListener {
                onConcluirClick?.invoke(tarefa, position)
            }

            // Ação do botão Editar (Lápis)
            holder.btnEditar.setOnClickListener { view ->
                val intent = Intent(view.context, TelaDetalhesTarefa::class.java)
                intent.putExtra("idTarefa", tarefa.id)
                view.context.startActivity(intent)
            }

            // Ação do botão Deletar (Lixeira)
            holder.btnDeletar.setOnClickListener {
                onDeleteClick?.invoke(tarefa, position)
            }

            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = listaTarefas.size
}