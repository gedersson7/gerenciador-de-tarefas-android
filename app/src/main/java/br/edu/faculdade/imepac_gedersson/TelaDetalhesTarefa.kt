package br.edu.faculdade.imepac_gedersson

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class TelaDetalhesTarefa : AppCompatActivity() {

    private lateinit var editTitulo: TextInputEditText
    private lateinit var editDescricao: TextInputEditText
    private lateinit var spinnerCategoria: AutoCompleteTextView // <-- Alterado para AutoCompleteTextView
    private lateinit var txtStatus: TextView
    private lateinit var btnSalvar: MaterialButton
    private lateinit var btnConcluir: MaterialButton

    private val db = FirebaseFirestore.getInstance()
    private var idTarefa: String? = null

    private val categorias = arrayOf("Desenvolvimento", "Faculdade", "Treino / Saúde", "Trabalho", "Casa", "Lazer", "Outros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_detalhes_tarefa)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTitulo = findViewById(R.id.editTituloDetalhe)
        editDescricao = findViewById(R.id.editDescricaoDetalhe)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaDetalhe)
        txtStatus = findViewById(R.id.txtStatusAtual)
        btnSalvar = findViewById(R.id.btnSalvarEdicao)
        btnConcluir = findViewById(R.id.btnConcluirTarefa)

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        spinnerCategoria.setAdapter(adapterSpinner)

        idTarefa = intent.getStringExtra("idTarefa")

        if (idTarefa != null) {
            carregarDadosDaTarefa(idTarefa!!)
        } else {
            Toast.makeText(this, "Erro: Tarefa não encontrada.", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSalvar.setOnClickListener {
            atualizarTarefa(idTarefa!!, false)
        }

        btnConcluir.setOnClickListener {
            atualizarTarefa(idTarefa!!, true)
        }
    }

    private fun carregarDadosDaTarefa(id: String) {
        db.collection("tarefas").document(id).get()
            .addOnSuccessListener { documento ->
                if (documento != null && documento.exists()) {
                    editTitulo.setText(documento.getString("titulo"))
                    editDescricao.setText(documento.getString("descricao"))

                    val categoriaSalva = documento.getString("categoria") ?: "Outros"

                    spinnerCategoria.setText(categoriaSalva, false)

                    val statusAtual = documento.getString("status") ?: "pendente"
                    txtStatus.text = "Status: ${statusAtual.uppercase()}"

                    if (statusAtual == "concluída") {
                        txtStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                        btnConcluir.isEnabled = false
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar dados.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarTarefa(id: String, apenasConcluir: Boolean) {
        val updates = hashMapOf<String, Any>()

        if (apenasConcluir) {
            updates["status"] = "concluída"
        } else {
            updates["titulo"] = editTitulo.text.toString().trim()
            updates["descricao"] = editDescricao.text.toString().trim()
            // Pega o texto diretamente do componente AutoCompleteTextView
            updates["categoria"] = spinnerCategoria.text.toString()
        }

        db.collection("tarefas").document(id).update(updates)
            .addOnSuccessListener {
                val msg = if (apenasConcluir) "Tarefa concluída!" else "Alterações salvas!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish() // Volta para a lista
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao atualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}