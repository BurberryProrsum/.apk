package com.example.diplom

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecycleViewClickOrderActivity : Fragment() {
    private lateinit var courierIdTextView: TextView
    private lateinit var buttongo: Button
    private lateinit var addressTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var orderIdTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycle_view_click_order, container, false)

        // Инициализация view элементов
        orderIdTextView = view.findViewById(R.id.numbertime)
        statusTextView = view.findViewById(R.id.status)
        addressTextView = view.findViewById(R.id.address)
        timeTextView = view.findViewById(R.id.time)
        priceTextView = view.findViewById(R.id.price)
        buttongo = view.findViewById(R.id.buttongo)
        courierIdTextView = view.findViewById(R.id.courierId)

        val orderId = requireArguments().getString("orderId") ?: ""
        val status = requireArguments().getString("status") ?: ""
        val address = requireArguments().getString("address") ?: ""
        val time = requireArguments().getString("time") ?: ""
        val price = requireArguments().getString("price") ?: ""
        val courierId = requireArguments().getString("courierId")

        orderIdTextView.text = orderId
        statusTextView.text = status
        addressTextView.text = address
        timeTextView.text = time
        priceTextView.text = price
        courierIdTextView.text = courierId

        auth = FirebaseAuth.getInstance()
        val order = Order(orderId, status, address, time, price)

        buttongo.setOnClickListener {
            goActiveOnClick(order)
            navigateBack()
        }

        return view
    }
    private fun navigateBack() {
        requireActivity().supportFragmentManager.popBackStack()
    }


    private fun goActiveOnClick(order: Order) {
        val address = addressTextView.text.toString().trim()
        val time = timeTextView.text.toString().trim()
        val price = priceTextView.text.toString().trim()

        if (address.isNotEmpty() && time.isNotEmpty() && price.isNotEmpty()) {
            moveOrderToActive(order.copy(address = address, time = time, price = price))
        } else {
        }
    }


    private fun moveOrderToActive(order: Order) {
        val context = context ?: return
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")
        val ordersActiveRef = database.getReference("ordersActive")
        val orderId = order.orderId



        // Сохранение заказа в "ordersActive"
        val newOrderRef = ordersActiveRef.child(orderId ?: "")
        newOrderRef.setValue(order.copy(status = "Активный"))

            .addOnSuccessListener {

                // Удаление заказа из "orders"
                ordersRef.child(orderId ?:"").removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Заказ перемещен в активные!", Toast.LENGTH_LONG).show()

            }
                    .addOnFailureListener {
                        Toast.makeText(context, "Ошибка при удалении заказа", Toast.LENGTH_LONG).show()
                    }
            }
    }

    companion object {
        fun newInstance(
            orderId: String,
            status: String,
            address: String,
            time: String,
            price: String,
            courierId: String? = null
        ): RecycleViewClickOrderActivity {
            val fragment = RecycleViewClickOrderActivity()
            val args = Bundle().apply {
                putString("orderId", orderId)
                putString("status", status)
                putString("address", address)
                putString("time", time)
                putString("price", price)
                putString("courierId", courierId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
