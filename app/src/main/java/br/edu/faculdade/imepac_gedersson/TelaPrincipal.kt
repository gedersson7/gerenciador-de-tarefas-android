package br.edu.faculdade.imepac_gedersson

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class TelaPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Vinculando os cartões do XML
        val cardVerTarefas = findViewById<MaterialCardView>(R.id.cardVerTarefas)
        val cardNovaTarefa = findViewById<MaterialCardView>(R.id.cardNovaTarefa)
        val cardIrPerfil = findViewById<MaterialCardView>(R.id.cardIrPerfil)
        val cardSobreApp = findViewById<MaterialCardView>(R.id.cardSobreApp)

        // 2. Configurando as ações de clique para abrir as telas

        // Abre a Tela de Perfil
        cardIrPerfil.setOnClickListener {
            val intent = Intent(this, TelaPerfil::class.java)
            startActivity(intent)
        }

        // Abre a Tela de Cadastrar Nova Tarefa
        cardNovaTarefa.setOnClickListener {
            val intent = Intent(this, FormNovaTarefa::class.java)
            startActivity(intent)
        }

        // Abre a Tela de Lista de Tarefas (Agora Ativado!)
        cardVerTarefas.setOnClickListener {
            val intent = Intent(this, TelaListaTarefas::class.java)
            startActivity(intent)
        }

        // Abre a Tela Sobre (Código de teste removido)
        cardSobreApp.setOnClickListener {
            val intent = Intent(this, TelaSobre::class.java)
            startActivity(intent)
        }
    }
}