package com.example.diplom

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActiveFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var ordersRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val orders: MutableList<Order> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_active, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ordersAdapter = OrdersAdapter(orders)
        recyclerView.adapter = ordersAdapter



        ordersRef = FirebaseDatabase.getInstance().getReference("ordersActive")


        ordersRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                snapshot.children.mapNotNullTo(orders) { it.getValue(Order::class.java) }
                ordersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
            }
        })



        return view
    }




        private inner class OrdersAdapter(private val orders: List<Order>) :
            RecyclerView.Adapter<OrderViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_order_active, parent, false)
                return OrderViewHolder(view)
            }

            override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
                val order = orders[position]
                holder.bind(order)
            }

            override fun getItemCount(): Int = orders.size
        }

        private inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            private val statusTextView: TextView = itemView.findViewById(R.id.status)
            private val addressTextView: TextView = itemView.findViewById(R.id.address)
            private val timeTextView: TextView = itemView.findViewById(R.id.time)
            private val priceTextView: TextView = itemView.findViewById(R.id.price)
            private val deleteButton: Button = itemView.findViewById(R.id.buttoncompleted)
            private val yandexMapsButton: Button? = itemView.findViewById(R.id.yandexMapsButton)

            private fun openYandexMaps(address: String) {
                if (address.isNotEmpty()) {
                    val uri = Uri.parse("https://yandex.ru/maps/?text=$address")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    itemView.context.startActivity(intent)
                } else {
                    Toast.makeText(itemView.context, "Адрес не указан", Toast.LENGTH_SHORT).show()
                }
            }

            init {
                deleteButton.setOnClickListener {
                    val order = orders[adapterPosition]
                    OrderToCompleted(order)
                }
                yandexMapsButton?.setOnClickListener {
                    val order = orders[adapterPosition]
                    openYandexMaps(order.address ?: "")
                }
            }
            private fun OrderToCompleted(order: Order) {
                val database = FirebaseDatabase.getInstance()
                val ordersActiveRef = database.getReference("ordersActive")
                val ordersCompletedRef = database.getReference("ordersCompleted")
                val orderId = order.orderId
                val newOrderRef = ordersActiveRef.child(orderId ?: "")


                newOrderRef.setValue(order.copy(status = "Активный"))

                if (orderId != null) {
                    ordersActiveRef.child(orderId).removeValue()
                        .addOnSuccessListener {
                            ordersCompletedRef.child(orderId ?: "").setValue(order.copy(status = "Завершенный"))
                                .addOnSuccessListener {

                                }
                            Toast.makeText(requireContext(), "Заказ выполнен!", Toast.LENGTH_LONG).show()
                            orders.remove(order)
                            ordersAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Какая-то ошибочка", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Неверный идентификатор заказа", Toast.LENGTH_LONG).show()
                }
            }

            fun bind(order: Order) {
                statusTextView.text = if (order.status != null) order.status else ""
                addressTextView.text = if (order.address != null) order.address else ""
                timeTextView.text = if (order.time != null) order.time else ""
                priceTextView.text = if (order.price != null) order.price else ""
            }

            override fun onClick(v: View?) {
                val order = orders[adapterPosition]
                openOrderDetails(order)
            }
        }

        private fun openOrderDetails(order: Order) {
            val orderDetailsFragment = RecycleViewClickOrderActivity.newInstance(
                order.orderId ?: "",
                order.status ?: "",
                order.address ?: "",
                order.time ?: "",
                order.price ?: ""
            )
        }
    }




