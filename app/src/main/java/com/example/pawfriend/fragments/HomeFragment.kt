package com.example.pawfriend.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawfriend.Endpoint
import com.example.pawfriend.Login
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.PostPetsAdapter
import com.example.pawfriend.R
import com.example.pawfriend.apiJsons.ListPostsPets
import com.example.pawfriend.databinding.CustomDialogBinding
import com.example.pawfriend.databinding.FragmentHomeBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private var backButtonPressedOnce = false
    private val BackButtonPressInrterval = 2000
    private lateinit var onBackPressedCallBak: OnBackPressedCallback
    private lateinit var allPostsList: List<ListPostsPets>
    private lateinit var filteredPostsList: List<ListPostsPets>


    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        onBackPressedCallBak = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(backButtonPressedOnce) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    backButtonPressedOnce = true
                    Toast.makeText(requireContext(), getString(R.string.exit_confirm), Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        backButtonPressedOnce = false
                    }, BackButtonPressInrterval.toLong())
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallBak)

        if (isNetworkAvailable(requireContext())) {
            getAllPosts()
            binding.createPostButton.setOnClickListener {
                findNavController().navigate(R.id.action_menu_home_to_menu_create_post)
            }
            binding.searchPetsInputs.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterPosts(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        } else {
            binding.postRecyclerView.visibility = View.GONE
            binding.createPostInsteadOfContainer.visibility = View.VISIBLE
            binding.createPostButton.visibility = View.GONE
            binding.titleTextView.text = getString(R.string.without_connection)
            binding.iconProblem.setImageResource(R.drawable.lost_connectio)
            val imageWithoutWireless = resources.getDrawable(R.drawable.lost_connectio)
            showNoConnectionDialog(
                getString(R.string.dialog_connection_error_title),
                getString(R.string.dialog_connection_error_message),
                getString(R.string.dialog_connection_error_button),
                imageWithoutWireless,
                false
            )
        }

        return binding.root

    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallBak.remove()
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
                getAllPosts()
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

    private fun filterPosts(query: String) {
        filteredPostsList = if (query.isEmpty()) {
            allPostsList
        } else {
            allPostsList.filter { post ->
                post.specie.contains(query, ignoreCase = true)
            }
        }
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        binding.postRecyclerView.adapter = PostPetsAdapter(filteredPostsList) { id ->
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

        endpoint.getAllPosts().enqueue(object : Callback<List<ListPostsPets>> {
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
                            allPostsList = it
                            filteredPostsList = it
                            intiRecyclerView(it)
                        }
                    }
                } else {
                    Log.i("APITESTE", "erro na api olha $response")
                    Toast.makeText(
                        requireContext(),
                        "Houve um erro tente novamente mais tarde",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ListPostsPets>>, t: Throwable) {
                val errorInternalImage = resources.getDrawable(R.drawable.lost_server)
                showNoConnectionDialog(
                    getString(R.string.dialog_error_request_title),
                    getString(R.string.dialog_error_request_message),
                    getString(R.string.dialog_error_request_button),
                    errorInternalImage,
                    true
                )
            }
        })
    }

}