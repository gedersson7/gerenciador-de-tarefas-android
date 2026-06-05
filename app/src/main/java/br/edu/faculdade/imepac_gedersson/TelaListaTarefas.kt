package br.edu.faculdade.imepac_gedersson

import android.content.Intent
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

class TelaListaTarefas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAnterior: MaterialButton
    private lateinit var btnProximo: MaterialButton
    private lateinit var btnIrConcluidas: MaterialButton
    private lateinit var txtPaginaAtual: TextView
    private lateinit var adapter: TarefaAdapter

    private val listaTarefas = ArrayList<Tarefa>()
    private val db = FirebaseFirestore.getInstance()

    // Variáveis de controle de Paginação
    private var currentPage = 1
    private val LIMIT_PAGINA = 5L
    private var firstVisible: DocumentSnapshot? = null
    private var lastVisible: DocumentSnapshot? = null

    // Controle de direção da busca
    enum class Direcao { INICIAL, PROXIMO, ANTERIOR }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_lista_tarefas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewTarefas)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnProximo = findViewById(R.id.btnProximo)
        btnIrConcluidas = findViewById(R.id.btnIrConcluidas)
        txtPaginaAtual = findViewById(R.id.txtPaginaAtual)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 1. INICIALIZANDO O ADAPTER COM AS FUNÇÕES DE CLIQUE
        adapter = TarefaAdapter(
            listaTarefas,
            isConcluida = false,
            onDeleteClick = { tarefa, posicao -> deletarTarefaDoFirestore(tarefa, posicao) },
            onConcluirClick = { tarefa, posicao -> alterarStatusParaConcluido(tarefa, posicao) }
        )
        recyclerView.adapter = adapter

        // Ao abrir a tela, carrega a página 1
        carregarDados(Direcao.INICIAL)

        btnProximo.setOnClickListener {
            carregarDados(Direcao.PROXIMO)
        }

        btnAnterior.setOnClickListener {
            carregarDados(Direcao.ANTERIOR)
        }

        // Abre a tela de Histórico (Lista Alternativa)
        btnIrConcluidas.setOnClickListener {
            val intent = Intent(this, TelaTarefasConcluidas::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (listaTarefas.isNotEmpty()) {
            carregarDados(Direcao.INICIAL)
        }
    }

    private fun carregarDados(direcao: Direcao) {
        // 2. FILTRO ADICIONADO: Puxar apenas as tarefas "pendentes" na tela principal
        var query = db.collection("tarefas")
            .whereEqualTo("status", "pendente")
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
                        Toast.makeText(this, "Esta já é a última página.", Toast.LENGTH_SHORT).show()
                        btnProximo.isEnabled = false
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar página: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarUIPaginacao(direcao: Direcao, itensRetornados: Int) {
        if (direcao == Direcao.PROXIMO) {
            currentPage++
        } else if (direcao == Direcao.ANTERIOR) {
            currentPage--
        }

        txtPaginaAtual.text = "Página $currentPage"

        btnAnterior.isEnabled = currentPage > 1
        btnProximo.isEnabled = itensRetornados == LIMIT_PAGINA.toInt()
    }

    // 3. NOVA FUNÇÃO: Deleta a tarefa do Firebase
    private fun deletarTarefaDoFirestore(tarefa: Tarefa, posicao: Int) {
        tarefa.id?.let { id ->
            db.collection("tarefas").document(id).delete().addOnSuccessListener {
                listaTarefas.removeAt(posicao)
                adapter.notifyItemRemoved(posicao)
                adapter.notifyItemRangeChanged(posicao, listaTarefas.size)
                Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 4. NOVA FUNÇÃO: Atualiza o status para "concluída" no Firebase
    private fun alterarStatusParaConcluido(tarefa: Tarefa, posicao: Int) {
        tarefa.id?.let { id ->
            db.collection("tarefas").document(id).update("status", "concluída").addOnSuccessListener {
                listaTarefas.removeAt(posicao)
                adapter.notifyItemRemoved(posicao)
                adapter.notifyItemRangeChanged(posicao, listaTarefas.size)
                Toast.makeText(this, "Tarefa concluída!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}