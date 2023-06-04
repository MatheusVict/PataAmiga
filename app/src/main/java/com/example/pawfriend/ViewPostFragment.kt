package com.example.pawfriend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pawfriend.databinding.FragmentViewPostBinding


class ViewPostFragment : Fragment() {
    private var _binding: FragmentViewPostBinding? = null
    private val binding: FragmentViewPostBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPostBinding.inflate(inflater, container, false)

        val idPost = arguments?.getString("idPost").toString().toLong()


        return binding.root
    }


}