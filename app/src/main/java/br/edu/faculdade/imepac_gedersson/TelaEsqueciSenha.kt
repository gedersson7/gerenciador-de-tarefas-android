package br.edu.faculdade.imepac_gedersson

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth


class TelaEsqueciSenha : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var btnRecuperar: MaterialButton
    private lateinit var txtVoltar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_esqueci_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editEmail = findViewById(R.id.edit_email_recuperacao)
        btnRecuperar = findViewById(R.id.btnRecuperarSenha)
        txtVoltar = findViewById(R.id.txtVoltarLogin)

        txtVoltar.setOnClickListener {
            finish()
        }

        btnRecuperar.setOnClickListener {
            val email = editEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, digite seu e-mail.", Toast.LENGTH_SHORT).show()
            } else {
                enviarEmailRecuperacao(email)
            }
        }
    }

    private fun enviarEmailRecuperacao(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "E-mail de recuperação enviado com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val erro = task.exception?.message
                    Toast.makeText(this, "Erro: $erro", Toast.LENGTH_LONG).show()
                }
            }
    }
}