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

    private lateinit var adapter: ProductAdapter    // 제품들을 보여줄 RecyclerView 어댑터
    private lateinit var productDB:DatabaseReference    // ㅁ
    private lateinit var userDB:DatabaseReference

    private val productList = mutableListOf<ProductModel>()

    // child에 변화가 있을 경우 불러오는 listener
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // getValue를 통해 모델 자체를 푸시하고 받아오도록 했다. -> 데이터 클래스에 빈생성자를 무조건 생성해야한다.
            // tinder에서는 모델의 값들만을 이용해서 접근했다.
            val productModel = snapshot.getValue(ProductModel::class.java)
            productModel ?: return

            productList.add(productModel)    // 전체 품목 리스트에 새로운 제품 데이터 추가
            adapter.submitList(productList)    // 어댑터 리스트에 추가

        }

        // 미사용
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentHomeBinding? = null    // 프래그먼트 뷰바인딩

    private val auth:FirebaseAuth by lazy {    // Firebase 인증 인스턴스
       Firebase.auth
    }

    // 뷰가 생성될 때 마다 호출
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰는 재활용이 되지만 Data를 담은 productList는 addChildEventListener가
        // 계속 실행되어서 같은 Data들이 계속 추가됩니다.
        // 따라서 뷰가 재활용될때 리스트를 비워주고 다시 추가해줍니다.
        productList.clear()

        // fragmentHomeBinding을 지역 변수로 먼저 선언해준 이유 : 위에서 전역변수 binding을 nullable로 선언했기 때문에
        // 매번 null임을 확인해야 하지만 onViewCreated 안에서는 null이 아님을 증명하기 위해 지역 변수로 우선 선언하고 설정
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        // RecyclerView 어댑터 선언
        adapter = ProductAdapter(onItemClicked = { productModel ->
            // 로그인이 되어있는 경우
            if (auth.currentUser != null) {
                // 로그인 유저와 판매 유저 ID가 다를 경우 채팅방 생성
                if (auth.currentUser!!.uid != productModel.sellerId) {

                    // TODO 채팅방 생성
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

        productDB = Firebase.database.reference.child(DB_PRODUCTS)    // 제품에 관한 DB
        userDB = Firebase.database.reference.child(DB_USERS)    // 사용자에 대한 DB


        // 리사이클러뷰 설정
        // 프래그먼트는 context가 아니므로 getContext로 가져와야 한다(this X). getContext == context
        fragmentHomeBinding.productRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.productRecyclerView.adapter = adapter

        // 제품 추가 플로팅 버튼
        fragmentHomeBinding.addFloatingBtn.setOnClickListener {

            if (auth.currentUser != null) {
                // requireContext : 프래그먼트가 연관된 context를 가져온다.
                    // 제품 추가 액티비티로 이동
                val intent = Intent(requireContext(), AddProductActivity::class.java)
                startActivity(intent)
            } else {
                // 로그인이 안되어 있을 경우
                Snackbar.make(view, "로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }


        }

        // onViewCreated가 실행될 때마다 진행
        productDB.addChildEventListener(listener)


    }

    override fun onResume() {
        super.onResume()
        // 다른 프래그먼트에서 다시 돌아올 경우 어댑터 리스트에 변경점을 UI에 적용
        adapter.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // listener가 중복으로 설정되는 것을 막기 위해
        productDB.removeEventListener(listener)
    }


}