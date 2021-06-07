package com.example.finalproject.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.R
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.CheckUser
import com.example.finalproject.model.User
import com.google.android.material.textfield.TextInputLayout
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: ShowHidePasswordEditText
    lateinit var btLogin: ImageButton
    lateinit var register: TextView
    lateinit var layoutEmail: TextInputLayout
    lateinit var layoutPassword:TextInputLayout
    lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()

        btLogin.setOnClickListener {
            if (validate()) {
                login()
            }
        }
    }
    private fun initView() {
        layoutEmail = findViewById(R.id.txtLayoutEmailSignIn)
        layoutPassword = findViewById(R.id.txtLayoutPasswordSignIn)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btLogin = findViewById(R.id.btLogin)
        register = findViewById(R.id.register)

        register.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra("url", "http://xmusicg.herokuapp.com/register")
            startActivity(intent)
        }

        dialog = ProgressDialog(this)
        dialog.setCancelable(false)

        email.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (email.text.isNotEmpty()) {
                    layoutEmail.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        password.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (password.text.isNotEmpty()) {
                    layoutPassword.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun login() {
        val service = ApiAdapter.makeRetrofitService
        try {
            val call = service.login(
                email = email.text.toString().trim(),
                password = password.text.toString().trim())
            dialog.setMessage("Đang đăng nhập");
            dialog.show();
            call.enqueue(object : Callback<CheckUser> {
                override fun onResponse(call: Call<CheckUser>, response: Response<CheckUser>) {
                    dialog.dismiss()
                    if (response.body()?.success == true) {
                        val user: User? = response.body()?.user
                        val sharePref = this@LoginActivity.getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sharePref.edit()
                        if (user != null) {
                            editor.putBoolean("check", true)
                            editor.putInt("id", user.id)
                            editor.putString("name", user.name)
                            editor.putString("email", user.email)
                            editor.apply();
                        }
                        val returnIntent = Intent()
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    } else {
                        password.text = null
                        layoutEmail.isErrorEnabled = true;
                        layoutEmail.error = "Bạn nhập sai email hoặc mật khẩu";
                    }
                }

                override fun onFailure(call: Call<CheckUser>, t: Throwable) {
                    t.message?.let { Log.e("error", it) }
                }

            })
        } catch (e: Exception){
            e.message?.let { Log.e("Can't Call API", it) }
        }
    }

    private fun validate(): Boolean {
        if (email.text.isEmpty()) {
            layoutEmail.isErrorEnabled = true;
            layoutEmail.error = "Bạn chưa nhập email";
            return false
        }
        if (password.text.isEmpty()) {
            layoutPassword.isErrorEnabled = true;
            layoutPassword.error = "Bạn chưa nhập mật khẩu"
            return false
        }
        return true
    }


}