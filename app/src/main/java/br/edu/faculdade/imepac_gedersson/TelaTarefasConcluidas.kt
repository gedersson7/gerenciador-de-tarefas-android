package br.edu.faculdade.imepac_gedersson

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TelaTarefasConcluidas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAnterior: MaterialButton
    private lateinit var btnProximo: MaterialButton
    private lateinit var txtPaginaAtual: TextView
    private lateinit var adapter: TarefaAdapter

    private val listaTarefas = ArrayList<Tarefa>()
    private val db = FirebaseFirestore.getInstance()

    private var currentPage = 1
    private val LIMIT_PAGINA = 5L
    private var firstVisible: DocumentSnapshot? = null
    private var lastVisible: DocumentSnapshot? = null

    enum class Direcao { INICIAL, PROXIMO, ANTERIOR }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_tarefas_concluidas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewConcluidas)
        btnAnterior = findViewById(R.id.btnAnteriorHist)
        btnProximo = findViewById(R.id.btnProximoHist)
        txtPaginaAtual = findViewById(R.id.txtPaginaAtualHist)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // AQUI ESTÁ A CORREÇÃO: Nomeando explicitamente o parâmetro onDeleteClick
        adapter = TarefaAdapter(
            listaTarefas = listaTarefas,
            isConcluida = true,
            onDeleteClick = { tarefa, posicao -> deletarTarefaDoFirestore(tarefa, posicao) }
        )
        recyclerView.adapter = adapter

        carregarDados(Direcao.INICIAL)

        btnProximo.setOnClickListener { carregarDados(Direcao.PROXIMO) }
        btnAnterior.setOnClickListener { carregarDados(Direcao.ANTERIOR) }
    }

    private fun carregarDados(direcao: Direcao) {
        var query = db.collection("tarefas")
            .whereEqualTo("status", "concluída")
            .orderBy("titulo", Query.Direction.ASCENDING)

        when (direcao) {
            Direcao.INICIAL -> {
                currentPage = 1
                query = query.limit(LIMIT_PAGINA)
            }
            Direcao.PROXIMO -> {
                if (lastVisible != null) {
                    query = query.startAfter(lastVisible!!).limit(LIMIT_PAGINA)
                }
            }
            Direcao.ANTERIOR -> {
                if (firstVisible != null) {
                    query = query.endBefore(firstVisible!!).limitToLast(LIMIT_PAGINA)
                }
            }
        }

        query.get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    listaTarefas.clear()

                    for (doc in documentos) {
                        val tarefa = doc.toObject(Tarefa::class.java)
                        tarefa.id = doc.id
                        listaTarefas.add(tarefa)
                    }

                    firstVisible = documentos.documents[0]
                    lastVisible = documentos.documents[documentos.size() - 1]

                    adapter.notifyDataSetChanged()
                    atualizarUIPaginacao(direcao, documentos.size())

                } else {
                    if (direcao == Direcao.PROXIMO) {
                        Toast.makeText(this, "Fim do histórico.", Toast.LENGTH_SHORT).show()
                        btnProximo.isEnabled = false
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar página: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarUIPaginacao(direcao: Direcao, itensRetornados: Int) {
        if (direcao == Direcao.PROXIMO) currentPage++
        else if (direcao == Direcao.ANTERIOR) currentPage--

        txtPaginaAtual.text = "Página $currentPage"
        btnAnterior.isEnabled = currentPage > 1
        btnProximo.isEnabled = itensRetornados == LIMIT_PAGINA.toInt()
    }

    private fun deletarTarefaDoFirestore(tarefa: Tarefa, posicao: Int) {
        val id = tarefa.id
        if (id != null) {
            db.collection("tarefas").document(id).delete()
                .addOnSuccessListener {
                    listaTarefas.removeAt(posicao)
                    adapter.notifyItemRemoved(posicao)
                    adapter.notifyItemRangeChanged(posicao, listaTarefas.size)
                    Toast.makeText(this, "Tarefa excluída permanentemente!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao excluir: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}