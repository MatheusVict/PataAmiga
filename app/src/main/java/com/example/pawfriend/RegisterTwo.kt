package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
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
        phoneFocusListener()
        whatsappFocusListener()
        locationFocusListener()

        binding.registerTwoButton.setOnClickListener { submitForm() }
    }

    private fun submitForm() {
        binding.phoneNumber.error = validPhone()
        binding.whatsappInput.error = validWhatsapp()
        binding.locationInput.error = validLocation()

        val validPhone = binding.phoneNumber.error == null
        val validWhatsapp = binding.whatsappInput.error == null
        val validLocation = binding.locationInput.error == null

        val toastMessage: String = getString(R.string.toast_error_inputs)

        if (validLocation && validPhone && validWhatsapp) {
            // TODO: create user in database and validate
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun phoneFocusListener() {
        binding.phoneNumber.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.phoneNumber.error = validPhone()
            }
        }
    }

    private fun validPhone(): String? {
        val isDone = binding.phoneNumber.isDone
        if (!isDone) {
            return getString(R.string.inputEmptyError)
        }
        return null
    }

    private fun whatsappFocusListener() {
        binding.whatsappInput.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.whatsappInput.error = validWhatsapp()
            }
        }
    }

    private fun validWhatsapp(): String? {
        val isDone = binding.whatsappInput.isDone
        if (!isDone) {
            return getString(R.string.inputEmptyError)
        }
        return null
    }

    private fun locationFocusListener() {
        binding.locationInput.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.locationInput.error = validLocation()
            }
        }
    }

    private fun validLocation(): String? {
        val locationText = binding.locationInput.text.toString()
        if (locationText.isEmpty()) {
            return getString(R.string.inputEmptyError)
        }
        if (locationText.length < 5) {
            return getString(R.string.register_two_location_length)
        }
        return null
    }

}