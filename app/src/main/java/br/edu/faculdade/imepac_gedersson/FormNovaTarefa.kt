package br.edu.faculdade.imepac_gedersson

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FormNovaTarefa : AppCompatActivity() {

    private lateinit var editTitulo: TextInputEditText
    private lateinit var editDescricao: TextInputEditText
    private lateinit var spinnerCategoria: AutoCompleteTextView // <-- Agora é AutoCompleteTextView
    private lateinit var btnSalvar: MaterialButton

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_nova_tarefa)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTitulo = findViewById(R.id.editTituloTarefa)
        editDescricao = findViewById(R.id.editDescricaoTarefa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnSalvar = findViewById(R.id.btnSalvarTarefa)

        val categorias = arrayOf("Desenvolvimento", "Faculdade", "Treino / Saúde", "Trabalho", "Casa", "Lazer", "Outros")

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        spinnerCategoria.setAdapter(adapter)

        btnSalvar.setOnClickListener {
            salvarTarefaNoFirestore()
        }
    }

    private fun salvarTarefaNoFirestore() {
        val titulo = editTitulo.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val categoriaSelecionada = spinnerCategoria.text.toString() // Pega o texto do componente

        if (titulo.isEmpty()) {
            editTitulo.error = "O título é obrigatório"
            return
        }
        if (descricao.isEmpty()) {
            editDescricao.error = "A descrição é obrigatória"
            return
        }
        if (categoriaSelecionada.isEmpty()) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show()
            return
        }

        val idUsuarioAtual = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val tarefa = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "status" to "pendente",
            "categoria" to categoriaSelecionada,
            "dataCriacao" to System.currentTimeMillis(),
            "userId" to idUsuarioAtual
        )

        db.collection("tarefas")
            .add(tarefa)
            .addOnSuccessListener {
                Toast.makeText(this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao salvar tarefa: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }
}