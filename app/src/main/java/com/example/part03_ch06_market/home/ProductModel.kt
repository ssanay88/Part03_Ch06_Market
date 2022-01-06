package com.example.part03_ch06_market.home

data class ProductModel(

    val sellerId:String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {
    // Firebase 모델 클래스를 그대로 사용하기 위해서는 빈 생성자가 있어야 한다.
    // tinder에서는 코드로 Firebase에서 직접 불러왔지만 여기선 Data Class형태로 바로
    // 사용한다.
    constructor(): this("","",0,"","")
}
