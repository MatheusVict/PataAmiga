package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.pawfriend.databinding.ActivityRegisterOneBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class RegisterOne : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterOneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ActionBar settings
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        emailFocusListener()
        passwordFocusListener()
        nameFocusListener()
        passwordConfirmFocusListener()
        birthFocusListener()

        binding.registerButtonContinue.setOnClickListener { submitForm() }
    }

    private fun submitForm() {
        binding.registerEmail.error = validEmail()
        binding.registerName.error = validName()
        binding.passwordRegister.error = validPassword()
        binding.confirmPassword.error = validPasswordConfirm()
        binding.birthDate.error = validBirth()

        val validEmail = binding.registerEmail.error == null
        val validName = binding.registerName.error == null
        val validPassword = binding.passwordRegister.error == null
        val validConfirmPassword = binding.confirmPassword.error == null
        val birth = binding.birthDate.error == null

        val toastMessage: String = getString(R.string.toast_error_inputs)

        if(validEmail && validName && validPassword && validConfirmPassword && birth) {
            val intent = Intent(this, RegisterTwo::class.java)
            intent.putExtra("emailUser", binding.registerEmail.text.toString())
            intent.putExtra("passwordUser", binding.passwordRegister.text.toString())
            intent.putExtra("nameUser", binding.registerName.text.toString())
            intent.putExtra("birthUser", binding.birthDate.unMasked)
            startActivity(intent)
        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()



    }

    // Inputs validations
    private fun emailFocusListener() {
        binding.registerEmail.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.registerEmail.error = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.registerEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return getString(R.string.register_one_email_wrong)
        }
        return null
    }

    private fun passwordFocusListener() {
        binding.passwordRegister.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.passwordRegister.error = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordRegister.text.toString()
        if (passwordText.length < 8) {
            return getString(R.string.register_one_password_length_wrong)
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return getString(R.string.register_one_password_uppercase_wrong)
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return getString(R.string.register_one_password_lowercase_wrong)
        }
        if (!passwordText.matches(".*[@#\$%^&+=!].*".toRegex())) {
            return getString(R.string.register_one_password_especial_wrong)
        }
        return null
    }
    private fun passwordConfirmFocusListener() {
        binding.confirmPassword.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.confirmPassword.error = validPasswordConfirm()
            }
        }
    }

    private fun validPasswordConfirm(): String? {
        val passwordText = binding.passwordRegister.text.toString()
        val passwordTextConfirm = binding.confirmPassword.text.toString()
        if (passwordText != passwordTextConfirm) {
            return getString(R.string.input_passwod_confirm_wrong)
        }
        return null
    }

    private fun nameFocusListener() {
        binding.registerName.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.registerName.error = validName()
            }
        }
    }

    private fun validName(): String? {
        val nameText = binding.registerName.text.toString()
        if (nameText.length < 3) {
            return getString(R.string.register_one_name_length_wrong)
        }
        return null
    }
    private fun birthFocusListener() {
        binding.birthDate.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.birthDate.error = validBirth()
            }
        }
    }

    private fun validBirth(): String? {
        val birthIsDone = binding.birthDate.isDone

        if (!birthIsDone) {
            return getString(R.string.register_one_birth_wrong)
        }

        // data atual do sistema
        val calender = Calendar.getInstance()

        val birthDate = binding.birthDate.text.toString()
        // formata a localidade padrão do dispositivo
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false // impede datas inválidas como 31/02/2022

        try {
            // Nova instancia do calander que será usado para armazenar a data de nascimento do usuário.
            val userCal = Calendar.getInstance()
            // converte em formato Date
            userCal.time = dateFormat.parse(birthDate)
            if (userCal.after(calender)) {
                // Data futura - inválida
                return getString(R.string.register_one_birth_wrong)
            }

            var diff = calender.get(Calendar.YEAR) - userCal.get(Calendar.YEAR)
            if (calender.get(Calendar.MONTH) < userCal.get(Calendar.MONTH) ||
                (calender.get(Calendar.MONTH) == userCal.get(Calendar.MONTH) && calender.get(Calendar.DAY_OF_MONTH) < userCal.get(Calendar.DAY_OF_MONTH))) {
                diff--
            }
            if (diff < 15) return getString(R.string.age_alert)
        } catch (e: ParseException) {
            return getString(R.string.register_one_birth_wrong)
        }
        return null
    }
}
