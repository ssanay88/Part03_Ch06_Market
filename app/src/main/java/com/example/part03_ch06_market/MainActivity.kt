package com.example.part03_ch06_market

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.part03_ch06_market.chatList.ChatListFragment
import com.example.part03_ch06_market.databinding.ActivityMainBinding
import com.example.part03_ch06_market.home.HomeFragment
import com.example.part03_ch06_market.myPage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프래그먼트 인스턴스 선언
        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        replaceFragment(homeFragment)    // 초기 프래그먼트 설정

        // bottomNavigation 아이템 클릭 시 프래그먼트 변경
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }


    }


    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .apply {
                // fragmentContainer에 있는 fragment를 교체하겠다.
                replace(R.id.fragmentContainer , fragment)
                commit()
            }

    }



}



/*

RecyclerView 사용하기
ViewBinding 사용하기
Fragment 사용하기
BottomNavigationView 사용하기
Firebase Storage 사용하기
Firebase Realtime Database 사용하기
Firebase Authentication 사용하기

중고거래 앱
Firebase Authentication 기능을 사용하여 로그인 회원가입 기능을 구현할 수 있음
회원 기반으로 중고거래 아이템을 등록할 수 있음
아이템 등록 시 사진 업로드를 위해 Firebase Storage를 사용할 수 있음
회원 기반으로 채팅 화면을 구현할 수 있음
Fragment를 사용하여 하단 탭 화면 구조를 구현할 수 있음
FloatingAcitionButton을 사용하기


 */