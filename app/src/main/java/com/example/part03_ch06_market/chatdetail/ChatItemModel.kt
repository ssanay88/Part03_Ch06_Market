package com.example.part03_ch06_market.chatdetail

data class ChatItemModel(
    val sendId: String,
    val message: String
) {
    constructor() : this("","")
}
