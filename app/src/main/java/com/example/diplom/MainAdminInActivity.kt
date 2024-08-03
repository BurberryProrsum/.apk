package com.example.diplom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.diplom.LoginAndRegistration.MainActivity
import com.example.diplom.LoginAndRegistration.MainActivityReg
import com.example.diplom.WorkMenu.MainActivity_AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainAdminInActivity : AppCompatActivity() {

    private lateinit var ordersRef: DatabaseReference
    private lateinit var orderButton: Button
    private lateinit var addressEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var buttonLogOut: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin_in)

        auth = FirebaseAuth.getInstance()



        orderButton = findViewById(R.id.orderbutton)
        addressEditText = findViewById(R.id.addressEditText)
        timeEditText = findViewById(R.id.timeEditText)
        priceEditText = findViewById(R.id.priceEditText)
        buttonLogOut = findViewById(R.id.buttonLogOut)



        orderButton.setOnClickListener {
            onClickOrder()
        }

        buttonLogOut.setOnClickListener {
            onLogout()
        }

        ordersRef = FirebaseDatabase.getInstance().getReference("orders")
    }

    private fun onClickOrder() {
        val address = addressEditText.text.toString().trim()
        val time = timeEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()

        if (address.isNotEmpty() && time.isNotEmpty() && price.isNotEmpty()) {
            createOrder(address, time, price)
        } else {

        }
    }

    private fun createOrder(address: String, time: String, price: String) {
        val newOrderRef = ordersRef.push()
        val orderId = newOrderRef.key ?: return

        val order = Order(orderId, "Свободен", address, time, price, null)

        newOrderRef.setValue(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Заказ создан!", Toast.LENGTH_LONG).show()
                resetFields()

            }
            .addOnFailureListener {
                // Ошибка при создании заказа
            }

    }

    private fun resetFields() {
        addressEditText.text.clear()
        timeEditText.text.clear()
        priceEditText.text.clear()
    }


    private fun onLogout() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}


