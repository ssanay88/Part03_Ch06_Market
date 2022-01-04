package com.example.part03_ch06_market.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part03_ch06_market.R
import com.example.part03_ch06_market.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {

    val TAG = "Log"
    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"onViewCreated start")

        // fragmentHomeBinding을 지역 변수로 먼저 선언해준 이유 : 위에서 전역변수 binding을 nullable로 선언했기 때문에
        // 매번 null임을 확인해야 하지만 onViewCreated 안에서는 null이 아님을 증명하기 위해 지역 변수로 우선 선언하고 설정
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        adapter = ProductAdapter()


        // 리사이클러뷰 설정
        // 프래그먼트는 context가 아니므로 getContext로 가져와야 한다. getContext == context
       fragmentHomeBinding.productRecyclerView.layoutManager = LinearLayoutManager(context)
       fragmentHomeBinding.productRecyclerView.adapter = adapter

    }

}