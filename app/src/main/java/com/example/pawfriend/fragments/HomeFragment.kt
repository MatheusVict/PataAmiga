package com.example.pawfriend.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawfriend.Endpoint
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.PostPetsAdapter
import com.example.pawfriend.R
import com.example.pawfriend.apiJsons.ListPostsPets
import com.example.pawfriend.databinding.FragmentHomeBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        if (isNetworkAvailable(requireContext())) {
            getAllPosts()
            binding.createPostButton.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_menu_create_post)
            }
        } else {
            binding.postRecyclerView.visibility = View.GONE
            binding.createPostInsteadOfContainer.visibility = View.VISIBLE
            binding.createPostButton.visibility = View.GONE
            binding.titleTextView.text = "Parece que você não possui conexão com a internt"
            // TODO: pic of without connection
            Toast.makeText(requireContext(), "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
        }

        return binding.root

    }

    private fun intiRecyclerView(postPetsList: List<ListPostsPets>) {
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postRecyclerView.setHasFixedSize(true)
        binding.postRecyclerView.adapter = PostPetsAdapter(postPetsList) { id ->
            val bundle = Bundle().apply {
                putString("idPost", id.toString())
            }
            findNavController().navigate(R.id.action_menu_home_to_viewPostFragment, bundle)
        }
    }

    private fun getAllPosts() {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getAllPosts().enqueue(object: Callback<List<ListPostsPets>> {
            override fun onResponse(
                call: Call<List<ListPostsPets>>,
                response: Response<List<ListPostsPets>>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.count() == 0) {
                        binding.postRecyclerView.visibility = View.GONE
                        binding.createPostInsteadOfContainer.visibility = View.VISIBLE

                    } else {
                        binding.postRecyclerView.visibility = View.VISIBLE
                        binding.createPostInsteadOfContainer.visibility = View.GONE

                    }

                    response.body()?.let {
                        if (it.isNotEmpty()) {
                            Log.i("APITESTE", "post: ${response.body()}")
                            intiRecyclerView(it)
                        }
                    }
                } else Toast.makeText(requireContext(), "Houve um erro tente novamente mais tarde", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<ListPostsPets>>, t: Throwable) {
                Toast.makeText(requireContext(), "Houve um erro interno tente novamente mais tarde", Toast.LENGTH_SHORT).show()
            }
        })
    }

}