package com.example.diplom.LoginAndRegistration

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.diplom.MainAdminInActivity
import com.example.diplom.WorkMenu.MainActivity_AllMenu
import com.example.diplom.R
import com.example.diplom.admin.MainAdminActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private val emailAuth = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+" // Регулярное выражение для проверки email
    private lateinit var auth: FirebaseAuth
    private lateinit var signEmail: EditText
    private lateinit var signPassword: EditText
    private lateinit var signIn: Button
    private lateinit var Bar: ProgressBar
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация Firebase Authentication
        auth = FirebaseAuth.getInstance()

        signEmail = findViewById<EditText>(R.id.login)
        signPassword = findViewById<EditText>(R.id.password)
        Bar = findViewById<ProgressBar>(R.id.progressBar)
        signIn = findViewById<Button>(R.id.button)
        text = findViewById<TextView>(R.id.textReset)


        signIn.setOnClickListener {
            OnClickSignIn(it)
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Если пользователь авторизован, переходим на MainActivity_AllMenu
            val intent = Intent(this, MainActivity_AllMenu::class.java)
            startActivity(intent)
            finish()
             // Закрываем текущую активити, чтобы пользователь не мог вернуться назад
        }

    }

    fun OnClickRecordReg(view: View) {
        val intent = Intent(this, MainActivityReg::class.java)
        startActivity(intent)
    }
    // Обработка входа пользователя
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
            signIn.visibility = View.GONE
            // Вход с использованием Firebase Authentication
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.visibility = View.VISIBLE
                    Bar.visibility = View.GONE
                    Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity_AllMenu::class.java)
                    startActivity(intent)
                } else {
                    signIn.visibility = View.VISIBLE
                    Bar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Неверный логин или пароль, попробуйте снова",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

        text.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.recoverypassword, null)
            val userEmail = view.findViewById<EditText>(R.id.resetpasswordEmail)
            builder.setView(view)
            val dialog = builder.create()
            // Обработчик кнопки восстановления пароля
            view.findViewById<Button>(R.id.resetpasswordBtn).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }
            // Обработчик кнопки выхода из данной активности
            view.findViewById<Button>(R.id.resetpasswordBtnEsc).setOnClickListener {
                dialog.dismiss()
            }
            // Установка прозрачного фона
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
    }
    // Восстановление пароля через отправку email
    private fun compareEmail(email: EditText) {
        if (email.text.toString().isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            return
        }
        // Отправка запроса на восстановление пароля
        auth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {
            if (it.isSuccessful)
                Toast.makeText(this, "Проверьте свой Email", Toast.LENGTH_SHORT).show()
        }
    }

    // Обработка клика и переход в админ панель
    fun OnClickSignInAdmin(view: View) {
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
    }

}
