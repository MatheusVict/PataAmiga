package com.example.pawfriend

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.databinding.FragmentEditProfileFragmentsBinding
import java.io.IOException
import java.util.Base64

class EditProfileFragments : Fragment() {

    private  var _binding: FragmentEditProfileFragmentsBinding? = null
    private val binding: FragmentEditProfileFragmentsBinding get() = _binding!!

     companion object UserImage {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_SECOND_IMAGE_PICK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileFragmentsBinding.inflate(inflater, container, false)
        binding.returnButton.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragments_to_menu_profile)
        }
        binding.changePicButton.setOnClickListener {
            openGallery(REQUEST_IMAGE_PICK)
        }
        binding.changeBannerButton.setOnClickListener {
            openGallery(REQUEST_SECOND_IMAGE_PICK)
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
                    if (requestCode == REQUEST_IMAGE_PICK) {
                        Log.i("APITESTE", " imagem do perfil ${base64Image}")
                    } else if (requestCode == REQUEST_SECOND_IMAGE_PICK) {
                        Log.i("APITESTE", " imagem do banner ${base64Image}")
                    } else {
                        Log.i("APITESTE", " imagem do do erro ${base64Image}")
                    }
                } catch (e: IOException) {

                }
            }
        }
    }

    private fun bytesToBase64(imageBytes: ByteArray): String {
        val base64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
        return base64.replace("\n", "")
    }

    /*companion object  {

        fun newInstance(param1: String, param2: String) =
            EditProfileFragments().apply {
                arguments = Bundle().apply {

                }
            }
    }*/
}