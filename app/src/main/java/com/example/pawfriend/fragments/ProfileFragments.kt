package com.example.pawfriend.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pawfriend.Endpoint
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragments : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        getUserInstance()
        return binding.root
    }


    private fun getUserInstance() {
        val retrofitClient = Service.getRetrofitInstance("http://192.168.0.107:8080", context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getUserProfile().enqueue(object: Callback<User>{

            override fun onResponse(call: Call<User>, response: Response<User>) {
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
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.i("APITESTE", "error: $t")

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