package com.example.part03_ch06_market.chatList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part03_ch06_market.DBKey.Companion.CHILD_CHAT
import com.example.part03_ch06_market.DBKey.Companion.DB_USERS
import com.example.part03_ch06_market.R
import com.example.part03_ch06_market.chatdetail.ChatRoomActivity
import com.example.part03_ch06_market.databinding.FragmentChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment: Fragment(R.layout.fragment_chatlist) {

    private lateinit var chatListAdapter: ChatListAdapter

    private var binding: FragmentChatlistBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val chatRoomList = mutableListOf<ChatListModel>()
    private lateinit var userDB: DatabaseReference


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)

        binding = fragmentChatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatListModel ->
            // 채팅방으로 이동하는 코드
            // context null 예외처리
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatListModel.key)
                startActivity(intent)
            }

        })

        chatRoomList.clear()

        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser == null) {
            return
        }

        val chatDB = Firebase.database.reference.child(DB_USERS)
            .child(auth.currentUser!!.uid)
            .child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            // sigleValueEvent의 경우 하위 목록들이 잘려서 오는것이 아닌 묶여서 한번에 오기 때문에
            // forEach문으로 구분해서 처리한다.
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListModel::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }

}