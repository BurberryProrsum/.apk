package com.example.diplom.LoginAndRegistration
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.diplom.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivityReg() : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailAuth = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    private lateinit var LoginReg: EditText
    private lateinit var PasswordReg: EditText
    private lateinit var ButtonReg: Button
    private lateinit var Bar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_reg)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        LoginReg = findViewById(R.id.loginreg)
        PasswordReg = findViewById(R.id.passwordreg)
        ButtonReg = findViewById(R.id.buttonreg)
        Bar = findViewById(R.id.progressBar)

        ButtonReg.setOnClickListener {
            OnClickReg(it)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }


    fun OnClickReg(view: View) {
        val email = LoginReg.text.toString()
        val password = PasswordReg.text.toString()

        if (email.isEmpty() || (password.isEmpty())) {
            if (email.isEmpty()) {
                LoginReg.error = "Поле Email не заполнено"
            }
            if (password.length < 5) {
                PasswordReg.error = "Поле с паролем не заполнено"
            }
        } else if (!email.matches(emailAuth.toRegex())) {
            LoginReg.error = "Где-то ошибочка, проверьте корректность"
        } else if (password.length < 6) {
            PasswordReg.error = "Пароль должен состоять минимум из 6 символов"
        } else {
            Bar.visibility = View.VISIBLE
            ButtonReg.visibility = View.GONE

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val database =
                        database.reference.child("users").child(auth.currentUser!!.uid)
                    val users: Users = Users(email, password, auth.currentUser!!.uid)
                    database.setValue(users).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show()
                            ButtonReg.visibility = View.VISIBLE
                            Bar.visibility = View.GONE
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Что то пошло не так, попробуйте снова", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }


        }
    }

