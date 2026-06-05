package br.edu.faculdade.imepac_gedersson

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class FormNovaTarefa : AppCompatActivity() {

    private lateinit var editTitulo: TextInputEditText
    private lateinit var editDescricao: TextInputEditText
    private lateinit var btnSalvar: MaterialButton

    // Inicializa o banco Firestore
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

        // Vincula os componentes do XML
        editTitulo = findViewById(R.id.editTituloTarefa)
        editDescricao = findViewById(R.id.editDescricaoTarefa)
        btnSalvar = findViewById(R.id.btnSalvarTarefa)

        btnSalvar.setOnClickListener {
            salvarTarefaNoFirestore()
        }
    }

    private fun salvarTarefaNoFirestore() {
        val titulo = editTitulo.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()

        // Validação simples de campos obrigatórios
        if (titulo.isEmpty()) {
            editTitulo.error = "O título é obrigatório"
            return
        }
        if (descricao.isEmpty()) {
            editDescricao.error = "A descrição é obrigatória"
            return
        }

        // Criando o mapa de dados (Objeto que vai pro Firestore)
        val tarefa = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "status" to "pendente" // Define um status padrão para a nova tarefa
        )

        // Executa o INSERT (add cria um ID único aleatório automaticamente)
        db.collection("tarefas")
            .add(tarefa)
            .addOnSuccessListener {
                Toast.makeText(this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha esta tela e volta para a anterior automaticamente
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao salvar tarefa: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }
}