package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.databinding.ActivityRegisterTwoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterTwo : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterTwoBinding
    private var isUserValid: Boolean = false
    private var toastRegisterMessage: String = ""
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

        binding.registerTwoButton.setOnClickListener { submitForm(
            email!!,
            password!!,
            name!!,
            birth!!
        ) }
    }

    private fun submitForm(email: String, password: String, name: String, birth: String) {
        binding.phoneNumber.error = validPhone()
        binding.whatsappInput.error = validWhatsapp()
        binding.locationInput.error = validLocation()

        val validPhone = binding.phoneNumber.error == null
        val validWhatsapp = binding.whatsappInput.error == null
        val validLocation = binding.locationInput.error == null

        val toastMessage: String = getString(R.string.toast_error_inputs)

        if (validLocation && validPhone && validWhatsapp) {
            val location = binding.locationInput.text.toString()
            val phone = binding.phoneNumber.unMasked
            val instagram = binding.instagramInput.text.toString()
            val facebook = binding.facebookInput.text.toString()
            val whatsapp = binding.whatsappInput.unMasked
            val profilePic = "fasj"
            val banner = "afsjid"

            val user = User(
                name = name,
                email = email,
                password = password,
                birth = birth,
                location = location,
                phone = phone,
                instagram = instagram,
                facebook = facebook,
                whatsapp = whatsapp,
                profilePic = profilePic,
                banner = banner
            )
            Log.i("APITESTE", "antes de enviar $user")

            createUser(user){isUserValid ->
                if (isUserValid) {
                    Log.i("APITESTE", "criado")
                    Toast.makeText(this, toastRegisterMessage, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else  Toast.makeText(this, toastRegisterMessage, Toast.LENGTH_SHORT).show()
            }

        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
    }


    private fun createUser(user: User, callback: (Boolean) -> Unit) {
        val retrofitClient = Service.getRetrofitInstance("http://192.168.0.107:8080")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.createUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    toastRegisterMessage = getString(R.string.user_created)
                    isUserValid = true
                    Log.i("APITESTE", "response de criação ${response.body()}")
                    callback(isUserValid)

                } else {
                    isUserValid = false
                    if (response.code() == 400) toastRegisterMessage = getString(R.string.email_already_exists)
                    Log.i("APITESTE", "response de erro api ${response}")
                    callback(false)
                }

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                isUserValid = false
                Log.i("APITESTE", "response de erro conexão ${t} e $call")
                toastRegisterMessage = getString(R.string.connection_error)

                callback(false)
            }
        })
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