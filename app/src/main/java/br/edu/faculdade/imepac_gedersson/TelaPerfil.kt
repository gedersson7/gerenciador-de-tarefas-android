package br.edu.faculdade.imepac_gedersson

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class TelaPerfil : AppCompatActivity() {

    private lateinit var emailUser: TextView
    private lateinit var usuarioUser: TextView
    private lateinit var bt_sair: Button
    private lateinit var bt_sobre: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_perfil)

        supportActionBar?.hide()

        IniciarComponentes()
        db = FirebaseFirestore.getInstance()

        fetchAllNames()

        bt_sobre.setOnClickListener {
            val intent = Intent(this, TelaSobre::class.java)
            startActivity(intent)
        }

        bt_sair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (userEmail != null) {
            emailUser.text = userEmail
            buscarNomeDoEmail(userEmail)
        }
    }

    private fun buscarNomeDoEmail(email: String) {
        val usuariosRef = db.collection("Usuarios")
        val query = usuariosRef.whereEqualTo("email", email)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents.first() as DocumentSnapshot
                    val nome = documento.getString("nome")

                    if (nome != null) {
                        usuarioUser.text = nome
                    } else {
                        println("Nome não encontrado para o e-mail $email")
                    }
                } else {
                    println("Nenhum documento encontrado para o e-mail $email")
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao buscar documento: $e")
            }
    }

    private fun IniciarComponentes() {
        emailUser = findViewById(R.id.textEmailUser)
        usuarioUser = findViewById(R.id.textNomeUser)
        bt_sair = findViewById(R.id.bt_sair)
        bt_sobre = findViewById(R.id.bt_sobre)
    }

    private fun fetchAllNames() {
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("Usuarios")

        usuariosRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val nome = document.getString("nome")
                println("Nome: $nome")
            }
        }.addOnFailureListener { exception ->
            println("Erro ao buscar os nomes: ${exception.message}")
        }
    }
}