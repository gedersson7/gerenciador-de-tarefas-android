package br.edu.faculdade.imepac_gedersson

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class TelaCategorias : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_categorias)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun abrirListaFiltrada(categoria: String) {
            val intent = Intent(this, TelaListaTarefas::class.java)
            intent.putExtra("CATEGORIA_FILTRO", categoria)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnCatDev).setOnClickListener { abrirListaFiltrada("Desenvolvimento") }
        findViewById<MaterialButton>(R.id.btnCatFacul).setOnClickListener { abrirListaFiltrada("Faculdade") }
        findViewById<MaterialButton>(R.id.btnCatTreino).setOnClickListener { abrirListaFiltrada("Treino / Saúde") }
        findViewById<MaterialButton>(R.id.btnCatTrabalho).setOnClickListener { abrirListaFiltrada("Trabalho") }
        findViewById<MaterialButton>(R.id.btnCatCasa).setOnClickListener { abrirListaFiltrada("Casa") }
        findViewById<MaterialButton>(R.id.btnCatLazer).setOnClickListener { abrirListaFiltrada("Lazer") }
    }
}