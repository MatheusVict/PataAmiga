package com.example.pawfriend.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawfriend.*
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.apiJsons.ListPostsPets
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.databinding.CustomDialogBinding
import com.example.pawfriend.databinding.FragmentProfileBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragments : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        if (isNetworkAvailable(requireContext())) {

            getUserInstance()
            getAllUserPosts()
            binding.editButton.setOnClickListener {
                findNavController().navigate(R.id.action_menu_profile_to_editProfileFragments)
            }
            binding.createPostInsteadOfButton.setOnClickListener {
                findNavController().navigate(R.id.action_menu_profile_to_menu_create_post)
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.verify_your_connection),
                Toast.LENGTH_SHORT
            ).show()
            binding.textViewInsteadOf.visibility = View.VISIBLE
            binding.createPostInsteadOfButton.visibility = View.VISIBLE
            binding.textViewInsteadOf.text = getString(R.string.connection_error)
            binding.createPostInsteadOfButton.text = getString(R.string.dialog_connection_error_button)
            binding.createPostInsteadOfButton.setOnClickListener {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            showNoConnectionDialog(
                getString(R.string.dialog_connection_error_title),
                getString(R.string.dialog_connection_error_message),
                getString(R.string.dialog_connection_error_button),
                resources.getDrawable(R.drawable.lost_connectio),
                false
            )

            /*val intent = Intent(requireContext(), Login::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)*/
        }

        return binding.root
    }

    private fun intiRecyclerView(postPetsList: List<ListPostsPets>) {
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postRecyclerView.setHasFixedSize(true)
        binding.postRecyclerView.adapter = PostPetsAdapter(postPetsList) { id ->
            val bundle = Bundle().apply {
                putString("idPost", id.toString())
                putString("postType", "ownerPost")
            }
            findNavController().navigate(R.id.action_menu_profile_to_viewPostFragment, bundle)
        }
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
                getUserInstance()
                getAllUserPosts()
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

    private fun getUserInstance() {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getUserProfile().enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "user: ${response.body()}")
                    val user: User? = response.body()?.let {
                        User(
                            name = it.name,
                            location = it.location,
                            profilePic = it.profilePic,
                            banner = it.banner
                        )
                    }

                    binding.userName.text = user?.name
                    binding.userLocation.text = user?.location
                    user?.banner.let {base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code.toString())
                        if (bitmap != null) {
                            binding.userBanner.setImageBitmap(bitmap)
                        } else {
                            binding.userBanner.setImageResource(R.drawable.banner_placeholder)
                        }
                    }
                    user?.profilePic.let {base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code.toString())
                        if (bitmap != null) {
                            binding.userProfilePic.setImageBitmap(bitmap)
                        } else {
                            binding.userProfilePic.setImageResource(R.drawable.no_user_pic_placeholder)
                        }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Erro inesperado tente logar novamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
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

    private fun getAllUserPosts() {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getAllPostsFromUser().enqueue(object : Callback<List<ListPostsPets>> {
            override fun onResponse(
                call: Call<List<ListPostsPets>>,
                response: Response<List<ListPostsPets>>
            ) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "post: ${response.body()}")
                    if (response.body()?.count() == 0) {
                        binding.postRecyclerView.visibility = GONE
                        binding.createPostInsteadOfButton.visibility = View.VISIBLE
                        binding.textViewInsteadOf.visibility = View.VISIBLE
                    } else {
                        binding.postRecyclerView.visibility = View.VISIBLE
                        binding.createPostInsteadOfButton.visibility = GONE
                        binding.textViewInsteadOf.visibility = View.GONE

                    }
                    response.body()?.let {
                        if (it.isNotEmpty()) {
                            Log.i("APITESTE", "post: ${response.body()}")
                            intiRecyclerView(it)
                        }
                    }
                } else Log.i("APITESTE", "erro na api: ${response}")

            }

            override fun onFailure(call: Call<List<ListPostsPets>>, t: Throwable) {
                Log.i("APITESTE", "erro: ${t}")

            }
        })
    }

    private fun decodeBase64ToBitMap(base64Code: String): Bitmap? {
        base64Code.let {
            val imageBytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }

}