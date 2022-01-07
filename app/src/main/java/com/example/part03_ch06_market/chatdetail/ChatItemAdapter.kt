package com.example.part03_ch06_market.chatdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.part03_ch06_market.chatList.ChatListAdapter
import com.example.part03_ch06_market.chatList.ChatListModel
import com.example.part03_ch06_market.databinding.ActivityChatRoomBinding
import com.example.part03_ch06_market.databinding.ItemChatBinding
import com.example.part03_ch06_market.databinding.ItemChatlistBinding

class ChatItemAdapter : ListAdapter<ChatItemModel, ChatItemAdapter.ViewHolder>(diffUtill) {

    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItemModel: ChatItemModel) {
            binding.senderTextView.text = chatItemModel.sendId
            binding.messageTextView.text = chatItemModel.message
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemAdapter.ViewHolder {
        return ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatItemAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtill = object  : DiffUtil.ItemCallback<ChatItemModel>() {
            // 현재 노출되는 아이템과 새로운 아이템이 같은지 비교
            override fun areItemsTheSame(oldItem: ChatItemModel, newItem: ChatItemModel): Boolean {
                return oldItem == newItem
            }

            // 현재 아이템들과 새로운 아이템들이 같은지 비교
            override fun areContentsTheSame(oldItem: ChatItemModel, newItem: ChatItemModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}