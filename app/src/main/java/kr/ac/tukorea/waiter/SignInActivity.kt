package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kr.ac.tukorea.waiter.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var db: FirebaseFirestore = Firebase.firestore
        db = FirebaseFirestore.getInstance()

        binding.signinbutton.setOnClickListener {
                // 입력칸들 중 하나라도 비어있다면 -> 모든 정보를 입력해주세요
                if(binding.signName.text.toString().equals("")
                    ||binding.signmail.text.toString().equals("")
                    ||binding.signPW.text.toString().equals("")
                    ||binding.signPWcheck.text.toString().equals("")
                    ||binding.signName.text.toString().equals("")
                    ||binding.signnumber.text.toString().equals("")
                    ||(binding.customer.isChecked === false
                    &&binding.owner.isChecked === false)
                ){
                    Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                // 비밀번호와 비밀번호 확인이 일치하지 않으면 -> 두 비밀번호가 일치하지 않습니다
                else if(!binding.signPW.text.toString().equals(binding.signPWcheck.text.toString())){
                    Toast.makeText(this, "두 비밀번호가 일치하지 않습니다!\n", Toast.LENGTH_SHORT).show()
                }
                // 위 두 경우가 아니면 Firebase auth에 Email과 Password를 담아 회원을 생성해 줌
                else{
                    Firebase.auth.createUserWithEmailAndPassword(
                        binding.signmail.text.toString(),
                        binding.signPW.text.toString()
                    ).addOnCompleteListener(this){
                        if (it.isSuccessful) {
                            // userMap에 입력받은 정보들을 담음

                            val userMap = hashMapOf(
                                "signName" to binding.signName.text.toString(),
                                "signID" to binding.signmail.text.toString(),
                                "passwd" to binding.signPW.text.toString(),
                                "passwdCk" to binding.signPWcheck.text.toString(),
                                "phoneNum" to binding.signnumber.text.toString(),
                                "x_y" to arrayListOf<String>()
                            )

                             //db안에 있는 users 컬렉션에 위 userMap에서 담은 정보를 넣어줌
                            if (binding.customer.isChecked) {
//                                userMap.remove("x_y")
                                userMap["userType"] = "customer"

                            }
                            else if(binding.owner.isChecked) {
                                userMap["userType"] = "owner"
                            }

                            db.collection("user")
                                   .document(Firebase.auth.currentUser?.uid ?: "No User")
                                   .set(userMap)
                            Toast.makeText(this, "회원가입 완료! 로그인 해주세요", Toast.LENGTH_LONG).show()
                            finish()
                        } // 위에서 말한 모든 경우에 해당하지 않고 회원가입 실패 -> 회원가입 실패
                        else{
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }



