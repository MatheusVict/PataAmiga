package com.example.pawfriend

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.apiJsons.UserUpdate
import com.example.pawfriend.databinding.CustomDialogBinding
import com.example.pawfriend.databinding.FragmentEditProfileFragmentsBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Base64

class EditProfileFragments : Fragment() {

    private var _binding: FragmentEditProfileFragmentsBinding? = null
    private val binding: FragmentEditProfileFragmentsBinding get() = _binding!!

    private lateinit var dialog: AlertDialog

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_SECOND_IMAGE_PICK = 2
    }

    private var profileImageBase64: String? = null
    private var bannerImageBase64: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileFragmentsBinding.inflate(inflater, container, false)

        if (isNetworkAvailable(requireContext())) {
            getUserInstance()
        } else {
            Toast.makeText(requireContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Home::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.returnButton.setOnClickListener{
            findNavController().navigate(R.id.action_editProfileFragments_to_menu_profile)
            findNavController().popBackStack(R.id.editProfileFragments, true)
        }

        binding.changePicButton.setOnClickListener {
            openGallery(REQUEST_IMAGE_PICK)
        }

        binding.changeBannerButton.setOnClickListener {
            openGallery(REQUEST_SECOND_IMAGE_PICK)
        }

        binding.removePicButton.setOnClickListener {
            profileImageBase64 = null
            binding.userProfilePic.setImageResource(R.drawable.no_user_pic_placeholder)
            Toast.makeText(requireContext(), "Profile picture removed", Toast.LENGTH_SHORT).show()
        }

        binding.removeBannerButton.setOnClickListener {
            bannerImageBase64 = null
            binding.userBannerPic.setImageResource(R.drawable.banner_placeholder)
            Toast.makeText(requireContext(), "Banner removed", Toast.LENGTH_SHORT).show()
        }

        binding.saveChangesButton.setOnClickListener {
            val user = UserUpdate(
                name = binding.userNameInput.text.toString(),
                location = binding.userLocation.text.toString(),
                phone = binding.userPhoneInput.text.toString(),
                whatsapp = binding.userWhatsappInput.text.toString(),
                instagram = binding.userInstagramInput.text.toString(),
                facebook = binding.userFacebookInput.text.toString(),
                profilePic = profileImageBase64,
                banner = bannerImageBase64
            )

            updateUserProfile(user)

            Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }


    private fun openGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(it)
                    val imageBytes = inputStream?.readBytes()
                    val base64Image = imageBytes?.let { bytesToBase64(it) }
                    when (requestCode) {
                        REQUEST_IMAGE_PICK -> {
                            binding.userProfilePic.setImageURI(it)
                            profileImageBase64 = base64Image
                        }
                        REQUEST_SECOND_IMAGE_PICK -> {
                            binding.userBannerPic.setImageURI(it)
                            bannerImageBase64 = base64Image
                        }
                    }
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun bytesToBase64(imageBytes: ByteArray): String {
        val base64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
        return base64.replace("\n", "")
    }

    private fun showNoConnectionDialog(
        title: String,
        message: String,
        messageButton: String,
        imageId: Drawable,
        isServerError: Boolean
    ) {
        val build = AlertDialog.Builder(requireContext())

        val view: CustomDialogBinding =
            CustomDialogBinding.inflate(LayoutInflater.from(requireContext()))

        view.closeDialog.setOnClickListener {
            dialog.dismiss()
        }
        view.imageDialog.setImageDrawable(imageId)
        view.titleDialog.text = title
        view.messageDialog.text = message
        view.buttonDialog.text = messageButton


        if (isServerError) {
            view.buttonDialog.setOnClickListener {
                val intent = Intent(requireContext(), Login::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } else {
            view.buttonDialog.setOnClickListener {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
        }
        build.setView(view.root)

        dialog = build.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun decodeBase64ToBitMap(base64Code: String): Bitmap? {
        base64Code.let {
            val imageBytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }



    private fun getUserInstance() {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getUserProfile().enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "user: ${response.body()}")
                    val user: UserUpdate? = response.body()?.let {
                        UserUpdate(
                            name = it.name,
                            location = it.location,
                            profilePic = it.profilePic,
                            banner = it.banner,
                            phone = it.phone,
                            whatsapp = it.whatsapp,
                            instagram = it.instagram,
                            facebook = it.facebook
                        )
                    }
                    binding.userNameInput.setText(user?.name)
                    binding.userLocation.setText(user?.location)
                    binding.userPhoneInput.setText(user?.phone)
                    binding.userWhatsappInput.setText(user?.whatsapp)
                    binding.userInstagramInput.setText(user?.instagram)
                    binding.userFacebookInput.setText(user?.facebook)
                    user?.banner.let {base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code.toString())
                        if (bitmap != null) {
                            binding.userBannerPic.setImageBitmap(bitmap)
                            bannerImageBase64 = base64code
                        } else {
                            binding.userBannerPic.setImageResource(R.drawable.banner_placeholder)
                        }
                    }
                    user?.profilePic.let {base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code.toString())
                        if (bitmap != null) {
                            binding.userProfilePic.setImageBitmap(bitmap)
                            profileImageBase64 = base64code
                        } else {
                            binding.userProfilePic.setImageResource(R.drawable.no_user_pic_placeholder)
                        }
                    }

                } else if (response.code() == 401 || response.code() == 403){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.try_login),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.api_error),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.i("APITESTE", "error: $t")
                Toast.makeText(
                    requireContext(),
                    "Erro inesperado tente logar novamente",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(requireContext(), Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
        })
    }

    private fun updateUserProfile(user: UserUpdate) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.updateUserProfile(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "user atualizado ${response.body()}")
                } else Log.i("APITESTE", "erro da API ${response}")

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.i("APITESTE", "erro $t")

            }
        })
    }

}