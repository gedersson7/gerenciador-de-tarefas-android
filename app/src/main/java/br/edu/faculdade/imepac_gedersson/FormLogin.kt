package br.edu.faculdade.imepac_gedersson

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class FormLogin : AppCompatActivity() {

    private lateinit var edit_email: EditText
    private lateinit var edit_senha: EditText
    private lateinit var bt_entrada: AppCompatButton
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_login)

        supportActionBar?.hide()

        IniciarComponentes()

        bt_entrada.setOnClickListener { it: View ->
            val email = edit_email.text.toString()
            val senha = edit_senha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                val mensagemErro = "Campos não preenchidos, tente novamente"
                val snackbar = Snackbar.make(it, mensagemErro, Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                AutenticarUsuario()
            }
        }

        val linkFormCadastro = findViewById<TextView>(R.id.text_tela_cadastro)

        linkFormCadastro.setOnClickListener {
            val telaCadastro = Intent(this, FormCadastro::class.java)
            startActivity(telaCadastro)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun AutenticarUsuario() {
        val email = edit_email.text.toString()
        val senha = edit_senha.text.toString()

        progressbar.visibility = View.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressbar.visibility = View.GONE

                // AQUI FOI FEITA A ALTERAÇÃO: Redirecionando para TelaPrincipal
                val intent = Intent(this@FormLogin, TelaPrincipal::class.java)
                startActivity(intent)
                finish()

            } else {

                progressbar.visibility = View.GONE
                val mensagemErro = task.exception?.message
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Erro ao autenticar usuário: $mensagemErro",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun IniciarComponentes() {
        edit_email = findViewById(R.id.edit_email_login)
        edit_senha = findViewById(R.id.edit_senha_login)
        bt_entrada = findViewById(R.id.bt_entrada)
        progressbar = findViewById(R.id.progressbar)
    }

    // Excelente detalhe de UX esse dispatchTouchEvent para esconder o teclado!
    override fun dispatchTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is android.widget.EditText) {
                val outRect = android.graphics.Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}