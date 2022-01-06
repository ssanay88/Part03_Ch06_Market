package com.example.part03_ch06_market.myPage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.part03_ch06_market.R
import com.example.part03_ch06_market.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// 프래그먼트 파라미터에
class MyPageFragment: Fragment(R.layout.fragment_mypage) {

    private lateinit var binding: FragmentMypageBinding

    private val auth:FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding



        binding.signInOutBtn.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                // 로그인이 안되어있을 경우는 로그인
                if (auth.currentUser == null) {

                    auth.signInWithEmailAndPassword(email, password)    // 로그인
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                successSignIn()
                            } else {
                                Toast.makeText(context,"로그인에 실패했습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {    // 로그인 되어있는 경우는 로그아웃
                    auth.signOut()    // 로그아웃

                    // editText 초기화
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    // 버튼 초기화
                    binding.signInOutBtn.text = "로그인"
                    binding.signInOutBtn.isEnabled = false
                    binding.signUpBtn.isEnabled = false
                }
            }
        }


        binding.signUpBtn.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "회원가입에 성공했습니다. 로그인 버튼을 클릭하여 주세요",Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "회원가입에 실패했습니다. 이미 가입한 회원이거나 다시 확인하여 주세요",Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }

        // EditText에 입력이 있어야 버튼들 활성화
        binding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signUpBtn.isEnabled = enable
                binding.signInOutBtn.isEnabled = enable
            }
        }

        binding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signUpBtn.isEnabled = enable
                binding.signInOutBtn.isEnabled = enable
            }
        }

    }

    override fun onStart() {
        super.onStart()

        // start()시 로그인이 안되어있을 경우
        if (auth.currentUser == null) {
            binding?.let {binding ->
                // editText 초기화
                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.passwordEditText.isEnabled = true

                // 버튼 초기화
                binding.signInOutBtn.text = "로그인"
                binding.signInOutBtn.isEnabled = false
                binding.signUpBtn.isEnabled = false
            }

        } else {    // 로그인되어 있을 경우
            binding?.let { binding ->
                // editText 로그인 중으로 설정
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.setText("********")
                binding.passwordEditText.isEnabled = false

                binding.signInOutBtn.text = "로그아웃"
                binding.signInOutBtn.isEnabled = true
                binding.signUpBtn.isEnabled = false
            }
        }

    }


    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시작해주세요",Toast.LENGTH_SHORT).show()
            return
        }

        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpBtn?.isEnabled = false
        binding?.signInOutBtn?.text = "로그아웃"

    }


}