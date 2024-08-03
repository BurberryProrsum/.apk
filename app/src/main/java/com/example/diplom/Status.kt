package com.example.diplom


data class Order(
    val orderId: String? = null,
    var status: String? = null,
    var address: String? = null,
    var time: String? = null,
    var price: String? = null,
    var courierId: String? = null,
    var additionalInfo: String? = null,

) {
    // Пустой конструктор без аргументов
    constructor() : this(null, null, null, null, null,)
}