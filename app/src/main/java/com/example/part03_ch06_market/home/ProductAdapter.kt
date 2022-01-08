package com.example.part03_ch06_market.home

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.part03_ch06_market.databinding.ItemProductBinding
import java.util.*

// productModel : 제품 데이터 모델
class ProductAdapter(val onItemClicked: (ProductModel) -> Unit): ListAdapter<ProductModel,ProductAdapter.ViewHolder>(diffUtill) {

    inner class ViewHolder(private val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(productModel: ProductModel) {

            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(productModel.createdAt)

            binding.titleTextView.text = productModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = productModel.price
            // 이미지가 있을 경우에만 연결
            if (productModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(productModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            // root -> 아이템 모든 부위 중 아무곳이나 눌렀을 경우
            // onItemClicked 실행 , 메서드 구현은 HomeFragment에서 람다 함수로 구현
            binding.root.setOnClickListener {
                onItemClicked(productModel)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ViewHolder {
        return ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtill = object  : DiffUtil.ItemCallback<ProductModel>() {
            // 현재 노출되는 아이템과 새로운 아이템이 같은지 비교
            override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            // 현재 아이템들과 새로운 아이템들이 같은지 비교
            override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}