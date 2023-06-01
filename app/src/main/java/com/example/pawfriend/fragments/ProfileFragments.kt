package com.example.pawfriend.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.Endpoint
import com.example.pawfriend.Login
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.R
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragments : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        getUserInstance()
        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_editProfileFragments)
        }

        return binding.root


    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/


    private fun getUserInstance() {
        val retrofitClient = Service.getRetrofitInstance("http://192.168.0.107:8080", context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getUserProfile().enqueue(object: Callback<User>{

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
               } else {
                   Toast.makeText(requireContext(), "Erro inesperado tente logar novamente", Toast.LENGTH_SHORT).show()
                   val intent = Intent(requireContext(), Login::class.java)
                   startActivity(intent)
               }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.i("APITESTE", "error: $t")
                Toast.makeText(requireContext(), "Erro inesperado tente logar novamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), Login::class.java)
                startActivity(intent)

            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragments().apply {
                arguments = Bundle().apply {

                }
            }
    }
}