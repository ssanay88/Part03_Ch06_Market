package com.example.part03_ch06_market.chatdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part03_ch06_market.DBKey.Companion.DB_CHATS
import com.example.part03_ch06_market.chatList.ChatListModel
import com.example.part03_ch06_market.databinding.ActivityChatRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.system.measureNanoTime

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private var chatDB:DatabaseReference? = null

    private val chatList = mutableListOf<ChatItemModel>()
    private val adapter = ChatItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val chatKey = intent.getLongExtra("chatKey",-1)

        chatDB = Firebase.database.reference.child(DB_CHATS).child(chatKey.toString())    // 채팅방 키

        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItemModel::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.sendBtn.setOnClickListener {
            val chatItem = ChatItemModel(
                sendId = auth.currentUser!!.uid,
                message = binding.messageEditText.text.toString()
            )

            chatDB!!.push().setValue(chatItem)


        }







    }
}