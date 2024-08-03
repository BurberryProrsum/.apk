package com.example.diplom.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.diplom.R
import androidx.appcompat.widget.Toolbar
import com.example.diplom.MainAdminInActivity
import com.google.firebase.auth.FirebaseAuth

class MainAdminActivity : AppCompatActivity() {
    private lateinit var signbuttonadmin: Button
    private val emailAuth = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var auth: FirebaseAuth
    private lateinit var signEmail: EditText
    private lateinit var signPassword: EditText
    private lateinit var Bar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        auth = FirebaseAuth.getInstance()
        signEmail = findViewById<EditText>(R.id.login)
        signPassword = findViewById<EditText>(R.id.password)
        Bar = findViewById<ProgressBar>(R.id.progressBar)
        signbuttonadmin = findViewById<Button>(R.id.buttonadmin)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        signbuttonadmin.setOnClickListener {
            OnClickSignIn(it)
        }



    }
    fun OnClickSignIn(view: View) {
        val email = signEmail.text.toString()
        val password = signPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                signEmail.error = "Введите Email"
            }
            if (password.isEmpty()) {
                signPassword.error = "Введите пароль"
            }
            Toast.makeText(this, "Введите свои данные", Toast.LENGTH_SHORT).show()
        } else if (!email.matches(emailAuth.toRegex())) {
            signEmail.error = "Где-то ошибочка, проверьте корректность"
        } else if (password.length < 6) {
            signPassword.error = "Пароль должен состоять минимум из 6 символов"
        } else {
            Bar.visibility = View.VISIBLE
            signbuttonadmin.visibility = View.GONE
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.email == "admin@mail.ru") {
                        signbuttonadmin.visibility = View.VISIBLE
                        Bar.visibility = View.GONE
                        Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainAdminInActivity::class.java)
                        startActivity(intent)
                    } else {
                        signbuttonadmin.visibility = View.VISIBLE
                        Bar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Неверный логин или пароль, попробуйте снова",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }
    }
}