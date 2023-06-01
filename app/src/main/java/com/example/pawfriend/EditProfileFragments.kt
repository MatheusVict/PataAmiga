package com.example.pawfriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.databinding.FragmentEditProfileFragmentsBinding

class EditProfileFragments : Fragment() {

    private  var _binding: FragmentEditProfileFragmentsBinding? = null
    private val binding: FragmentEditProfileFragmentsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileFragmentsBinding.inflate(inflater, container, false)
        binding.voltar.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragments_to_menu_profile)
        }
        return binding.root
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            EditProfileFragments().apply {
                arguments = Bundle().apply {

                }
            }
    }
}