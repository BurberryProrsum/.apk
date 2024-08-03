package com.example.diplom.admin

import com.example.diplom.Order

interface OrderDataListener {
    fun onOrderDataReceived(orders: List<Order>)
    fun onOrderCreated(order: Order) // Добавляем метод в интерфейс
}