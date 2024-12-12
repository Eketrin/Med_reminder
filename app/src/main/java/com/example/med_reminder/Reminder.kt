package com.example.med_reminder

data class Reminder(
    val id: Int,
    val text: String,
    val date: String,
    val time: String,
    val taked: Boolean,  // Поле для статуса выпито/не выпито
    val dose: String,     // Поле для дозы
    val piece: String    // Поле для текста единицы измерения
)

