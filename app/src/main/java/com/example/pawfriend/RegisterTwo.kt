package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.pawfriend.databinding.ActivityRegisterOneBinding
import com.example.pawfriend.databinding.ActivityRegisterTwoBinding

class RegisterTwo : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterTwoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        val email: String? = intent.getStringExtra("emailUser")
        val password: String? = intent.getStringExtra("passwordUser")
        val name: String? = intent.getStringExtra("nameUser")
        val birth: String? = intent.getStringExtra("birthUser")

        var erros: Int = 0

        // TODO: add number regexx


        binding.registerTwoButton.setOnClickListener {
            if (binding.phoneNumber.text.toString().isNotEmpty() && binding.phoneNumber.text.toString().length == 11) {
                binding.phoneNumber.error = null
                erros --

            } else {
                binding.phoneNumber.error = getString(R.string.inputEmptyError)
                erros ++
            }
            if (binding.whatsappInput.text.toString().isNotEmpty() && binding.whatsappInput.text.toString().length == 11) {
                binding.whatsappInput.error = null
                erros --

            } else {
                binding.whatsappInput.error = getString(R.string.inputEmptyError)
                erros ++
            }
            if(erros > 0) {
                Toast.makeText(applicationContext, "preencja", Toast.LENGTH_LONG).show()
            } // TODO: API connection 
        }


    }

}