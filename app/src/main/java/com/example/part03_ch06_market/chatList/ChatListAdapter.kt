package com.example.part03_ch06_market.chatList


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.part03_ch06_market.databinding.ItemChatlistBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(val onItemClicked: (ChatListModel) -> Unit): ListAdapter<ChatListModel, ChatListAdapter.ViewHolder>(diffUtill) {

    inner class ViewHolder(private val binding: ItemChatlistBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListModel: ChatListModel) {
            // root -> 아이템 모든 부위 중 아무곳이나 눌렀을 경우
            // onItemClicked 실행 , 메서드 구현은 HomeFragment에서 람다 함수로 구현
            binding.root.setOnClickListener {
                onItemClicked(chatListModel)
            }

            binding.chatRoomTextView.text = chatListModel.itemTitle

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {
        return ViewHolder(ItemChatlistBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtill = object  : DiffUtil.ItemCallback<ChatListModel>() {
            // 현재 노출되는 아이템과 새로운 아이템이 같은지 비교
            override fun areItemsTheSame(oldItem: ChatListModel, newItem: ChatListModel): Boolean {
                return oldItem.key == newItem.key
            }

            // 현재 아이템들과 새로운 아이템들이 같은지 비교
            override fun areContentsTheSame(oldItem: ChatListModel, newItem: ChatListModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}