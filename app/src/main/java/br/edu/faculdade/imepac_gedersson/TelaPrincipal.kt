package br.edu.faculdade.imepac_gedersson

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TelaPrincipal : AppCompatActivity() {

    private lateinit var txtBoasVindas: TextView
    private lateinit var txtQtdPendentes: TextView
    private lateinit var txtQtdConcluidas: TextView
    private lateinit var txtStatusDashboard: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtBoasVindas = findViewById(R.id.txtBoasVindas)
        txtQtdPendentes = findViewById(R.id.txtQtdPendentes)
        txtQtdConcluidas = findViewById(R.id.txtQtdConcluidas)
        txtStatusDashboard = findViewById(R.id.txtStatusDashboard)

        recuperarNomeUsuario()

        val cardVerTarefas = findViewById<MaterialCardView>(R.id.cardVerTarefas)
        val cardNovaTarefa = findViewById<ExtendedFloatingActionButton>(R.id.cardNovaTarefa)
        val cardIrPerfil = findViewById<MaterialCardView>(R.id.cardIrPerfil)
        val cardCategorias = findViewById<MaterialCardView>(R.id.cardCategorias)

        cardCategorias.setOnClickListener {
            val intent = Intent(this, TelaCategorias::class.java)
            startActivity(intent)
        }

        cardIrPerfil.setOnClickListener {
            val intent = Intent(this, TelaPerfil::class.java)
            startActivity(intent)
        }

        cardNovaTarefa.setOnClickListener {
            val intent = Intent(this, FormNovaTarefa::class.java)
            startActivity(intent)
        }

        cardVerTarefas.setOnClickListener {
            val intent = Intent(this, TelaListaTarefas::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        calcularDashboard()
    }

    private fun recuperarNomeUsuario() {
        val emailUser = FirebaseAuth.getInstance().currentUser?.email
        val db = FirebaseFirestore.getInstance()

        if (emailUser != null) {
            db.collection("Usuarios").whereEqualTo("email", emailUser)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documento = querySnapshot.documents.first()
                        val nomeCompleto = documento.getString("nome")

                        if (nomeCompleto != null) {
                            val primeiroNome = nomeCompleto.split(" ").firstOrNull() ?: "Usuário"
                            txtBoasVindas.text = "Olá, $primeiroNome 👋"
                        }
                    }
                }
        }
    }

    private fun calcularDashboard() {
        val idUsuarioAtual = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("tarefas")
            .whereEqualTo("userId", idUsuarioAtual)
            .get()
            .addOnSuccessListener { documentos ->
                var contagemPendentes = 0
                var contagemConcluidas = 0

                for (doc in documentos) {
                    val status = doc.getString("status")
                    if (status == "pendente") {
                        contagemPendentes++
                    } else if (status == "concluída") {
                        contagemConcluidas++
                    }
                }

                txtQtdPendentes.text = contagemPendentes.toString()
                txtQtdConcluidas.text = contagemConcluidas.toString()

                if (contagemPendentes == 0 && contagemConcluidas == 0) {
                    txtStatusDashboard.text = "Crie sua primeira tarefa para começar! ✨"
                } else if (contagemPendentes == 0 && contagemConcluidas > 0) {
                    txtStatusDashboard.text = "Tudo limpo! Você concluiu tudo. 🏆"
                } else if (contagemPendentes > contagemConcluidas) {
                    txtStatusDashboard.text = "Você tem trabalho a fazer. Foco! 🚀"
                } else {
                    txtStatusDashboard.text = "Bom trabalho! Você está progredindo. 🔥"
                }
            }
            .addOnFailureListener {
                txtStatusDashboard.text = "Não foi possível carregar as métricas."
            }
    }
}