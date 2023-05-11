package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.pawfriend.databinding.ActivityHomeBinding
import com.example.pawfriend.databinding.ActivityRegisterOneBinding
import java.text.SimpleDateFormat
import java.util.Date


class RegisterOne : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterOneBinding
    private lateinit var dateFormatter: SimpleDateFormat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ActionBar settings
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = Date()
        binding.birthDate.setText(dateFormatter.format(currentDate))


        var errors = 0

        // Inputs validations
        binding.registerButtonContinue.setOnClickListener {
            if (binding.registerEmail.text.toString().isEmpty()) {
                binding.registerEmail.error = getString(R.string.inputEmptyError)
                errors ++
            } else {
                binding.registerEmail.error = null
                errors --
            }
            if (binding.registerName.text.toString().isEmpty()) {
                binding.registerName.error = getString(R.string.inputEmptyError)
                errors ++
            } else {
                binding.registerName.error = null
                errors --
            }
            if (binding.passwordRegister.text.toString().isEmpty()) {
                binding.passwordRegister.error = getString(R.string.inputEmptyError)
                errors ++
            } else {
                binding.passwordRegister.error = null
                if (binding.confirmPassword.text.toString() != binding.passwordRegister.text.toString()) {
                    binding.confirmPassword.error = getString(R.string.inputPasswordError)
                    errors ++

                } else errors --

            }
            if (binding.birthDate.text.toString().isEmpty()) {
                binding.birthDate.error = getString(R.string.inputEmptyError)
                errors ++
            } else {
                // TODO: age validation
                binding.birthDate.error = null
                errors --
            }
            // TODO: connection and API validation
            if (errors > 0) {
                val text = getString(R.string.toastErrorInputs)
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RegisterTwo::class.java)
                intent.putExtra("emailUser", binding.registerEmail.text.toString())
                intent.putExtra("passwordUser", binding.passwordRegister.text.toString())
                intent.putExtra("nameUser", binding.registerName.text.toString())
                intent.putExtra("birthUser", binding.birthDate.text.toString())
                startActivity(intent)
            }
        }
    }

}
