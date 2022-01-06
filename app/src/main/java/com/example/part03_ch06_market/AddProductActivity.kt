package com.example.part03_ch06_market

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.part03_ch06_market.DBKey.Companion.DB_PRODUCTS
import com.example.part03_ch06_market.databinding.ActivityAddProductBinding
import com.example.part03_ch06_market.home.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding    // 뷰 바인딩
    private var selectedUri: Uri?= null    // 선택한 사진의 uri
    // Firebase 인증 인스턴스
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    // Firebase 저장소 인스턴스
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    // Firebase realtime DB 인스턴스
    private val productDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_PRODUCTS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상품 이미지 추가 버튼 클릭 시
        binding.imgaeAddBtn.setOnClickListener {
            when {
                // 외부 저장소 권한이 허용된 경우
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()    // 권한이 허용된 경우 외부저장소에서 사진을 띄울수있도록 접근
                }

                // 추가 버튼 클릭했지만 권한이 없는 경우 팝업을 띄워준다.
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 교육용 팝업을 띄운다.
                    showPermissionContextPopUp()
                }

                // 권한을 요청
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
                }

            }
        }

        // 제품 등록 버튼 클릭 시
        binding.submitBtn.setOnClickListener {

            showProgress()    // 등록 버튼 클릭 시 등록 종료때까지 progressBar 띄워둔다.

            val title = binding.titleEditText.text.toString()
            val price = binding.priceEditText.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 이미지가 있다면 이미지도 DB에 추가하는 과정 추가
            if (selectedUri != null) {
                // 선택한 이미지 uri를 불러온다.
                val photoUri = selectedUri ?: return@setOnClickListener
                // 사진을 DB에 업로드
                uploadPhoto(photoUri,
                    // uploadPhoto에서 successHandler가 실행될 경우 uploadProduct를 실행한다.
                    successHandler = { uri ->
                        uploadProduct(sellerId, title, price, uri)
                    },
                    // 이미지를 업로드못했기 때문에 Toast문을 띄운다.
                    errorHandler =  {
                        Toast.makeText(this,"사진 업로드에 실패했습니다.",Toast.LENGTH_SHORT).show()
                        hideProgress()
                    })
            } else {
                // 선택한 이미지가 없으므로 이미지없이 제품만 DB에 등록
                uploadProduct(sellerId, title, price , "")
            }



        }


    }

    // 권한 요청에 대한 결과를 받아와서 실행
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            1000 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this,"권한을 거부하셨습니다.",Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent , 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 올바른 결과로 가져오지 못했을 경우
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val uri = data?.data
                if (uri != null) {
                    binding.photoImageView.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("권한을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") {_,_ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
            }
            .create()
            .show()
    }

    // DB에 올리는 이미지에 대해 설정 , 파일 이름 설정 및 업로드
    private fun uploadPhoto(uri: Uri , successHandler: (String) -> Unit , errorHandler: () -> Unit) {

        val fileName = "${System.currentTimeMillis()}.png"    // 업로드할 이미지의 이름
        // 저장소안에 밑에 하위 파일안에 위에서 만든 파일 이름으로 생성
        storage.reference.child("product/photo").child(fileName)
            .putFile(uri)    // 이미지 uri 저장
            .addOnCompleteListener {
                // 업로드가 완료되는 경우
                if (it.isSuccessful) {
                    storage.reference.child("product/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            // downloadUrl이 성공할 경우 위에서 구현한 successHandler를 실행
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            Log.d("태그","이까지는 진행")
                            errorHandler()
                        }

                } else {
                    // 업로드가 실패한 경우
                    errorHandler()
                }
            }

    }

    // DB에 제품을 등록
    private fun uploadProduct(sellerId:String, title:String, price:String, imageUrl:String) {
        // DB에 제품을 등록
        val model = ProductModel(sellerId , title , System.currentTimeMillis() , "$price 원" , imageUrl)
        productDB.push().setValue(model)    // 임의의 키값 안에 만들어둔 model이 들어간다.

        hideProgress()    // 업로드가 끝났을 경우 progressBar 숨김

        finish()
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }


}