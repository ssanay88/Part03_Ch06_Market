package com.example.part03_ch06_market.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part03_ch06_market.AddProductActivity
import com.example.part03_ch06_market.DBKey.Companion.CHILD_CHAT
import com.example.part03_ch06_market.DBKey.Companion.DB_PRODUCTS
import com.example.part03_ch06_market.DBKey.Companion.DB_USERS
import com.example.part03_ch06_market.R
import com.example.part03_ch06_market.chatList.ChatListModel
import com.example.part03_ch06_market.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.fragment_home) {

    val TAG = "Log"

    private lateinit var adapter: ProductAdapter
    private lateinit var productDB:DatabaseReference
    private lateinit var userDB:DatabaseReference

    private val productList = mutableListOf<ProductModel>()

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val productModel = snapshot.getValue(ProductModel::class.java)
            productModel ?: return

            productList.add(productModel)
            adapter.submitList(productList)

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }
    private var binding: FragmentHomeBinding? = null
    private val auth:FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"onViewCreated start")

        // 뷰는 재활용이 되지만 Data를 담은 productList는 addChildEventListener가
        // 계속 실행되어서 같은 Data들이 계속 추가됩니다.
        // 따라서 뷰가 재활용될때 리스트를 비워주고 다시 추가해줍니다.
        productList.clear()

        // fragmentHomeBinding을 지역 변수로 먼저 선언해준 이유 : 위에서 전역변수 binding을 nullable로 선언했기 때문에
        // 매번 null임을 확인해야 하지만 onViewCreated 안에서는 null이 아님을 증명하기 위해 지역 변수로 우선 선언하고 설정
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        adapter = ProductAdapter(onItemClicked = { productModel ->
            if (auth.currentUser != null) {
                if (auth.currentUser!!.uid != productModel.sellerId) {

                    // TODO 채팅방 생성성
                    val chatRoom = ChatListModel(
                        buyerId = auth.currentUser!!.uid,
                        sellerId = productModel.sellerId,
                        itemTitle = productModel.title,
                        key = System.currentTimeMillis()
                    )

                    // 구매자의 DB의 Chat에 채팅 정보를 추가
                    userDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    // 판매자의 DB의 Chat에 채팅 정보를 추가
                    userDB.child(productModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.",Snackbar.LENGTH_LONG).show()

                } else {
                    // 내가 올린 제품일 경우
                    Snackbar.make(view, "내가 올린 제품입니다.",Snackbar.LENGTH_LONG).show()
                }
            } else {
                // 로그인을 안한 상태일 경우
                Snackbar.make(view, "로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }


        })

        productDB = Firebase.database.reference.child(DB_PRODUCTS)
        userDB = Firebase.database.reference.child(DB_USERS)


        // 리사이클러뷰 설정
        // 프래그먼트는 context가 아니므로 getContext로 가져와야 한다. getContext == context
        fragmentHomeBinding.productRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.productRecyclerView.adapter = adapter

        fragmentHomeBinding.addFloatingBtn.setOnClickListener {

            if (auth.currentUser != null) {
                val intent = Intent(requireContext(), AddProductActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }


        }


        productDB.addChildEventListener(listener)


    }

    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        productDB.removeEventListener(listener)
    }


}