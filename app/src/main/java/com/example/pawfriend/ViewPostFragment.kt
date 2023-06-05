package com.example.pawfriend

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.apiJsons.GetOnePost
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.databinding.FragmentViewPostBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        val postType = arguments?.getString("postType")

        if (postType == "ownerPost") {
            binding.postOwnerTextView.visibility = View.GONE
            binding.userPostPic.visibility = View.GONE
            binding.userNamePostView.visibility = View.GONE
            binding.buttonAdoppetButton.visibility = View.GONE
            binding.editPostViewButton.visibility = View.VISIBLE
        }

        if (isNetworkAvailable(requireContext())) {
            getPostInstance(idPost)

            binding.editPostViewButton.setOnClickListener {
                idPost?.let {
                    val bundle = Bundle().apply {
                        putString("idPost", idPost.toString())
                    }
                    findNavController().navigate(R.id.action_viewPostFragment_to_menu_create_post, bundle)
                }
            }

            binding.buttonAdoppetButton.setOnClickListener {
               val alert = AlertDialog.Builder(requireContext())
                alert.setTitle("Entre em contato")
                alert.setMessage("aqui")
            }
        } else Toast.makeText(requireContext(), "sem Conexão", Toast.LENGTH_SHORT).show()

        return binding.root
    }

    private fun getPostInstance(idPost: Long) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getOnePostForId(idPost).enqueue(object: Callback<GetOnePost> {
            override fun onResponse(call: Call<GetOnePost>, response: Response<GetOnePost>) {
                if (response.isSuccessful) {
                    val post: GetOnePost? = response.body()?.let {
                        GetOnePost(
                            id = it.id,
                            postPic = it.postPic,
                            name = it.name,
                            race = it.race,
                            sex = it.sex,
                            age = it.age,
                            size = it.size,
                            weight = it.weight,
                            about = it.about,
                            petLocation = it.petLocation,
                            isAdopted = it.isAdopted,
                            isCastrated = it.isCastrated,
                            isVaccinated = it.isVaccinated,
                            isPedigree = it.isPedigree,
                            isDewormed = it.isDewormed,
                            isEspecialNeeds = it.isEspecialNeeds,
                            userId = it.userId,
                            userPic = it.userPic,
                            userName = it.userName,
                            specie = it.specie,
                        )
                    }
                    Log.i("APITESTE", "location ${post?.petLocation} e specie ${post?.specie}")

                    binding.postPetName.text = post?.name
                    binding.postLocationTextView.text = post?.petLocation
                    binding.speciePostView.text = "Espécie: ${post?.specie}"
                    binding.racePostView.text = "Raça: ${post?.race}"
                    binding.sexPostView.text = "Sexo: ${post?.sex}"
                    binding.agePostView.text = "Idade: ${post?.age}"
                    binding.weigthPostView.text = "Peso: ${post?.weight}"
                    binding.isCastratedPostView.text = "Castrado: ${if (post?.isCastrated == true) "Sim" else "Não"}"
                    binding.isVaccinatedPostView.text = "Vacinado: ${if (post?.isVaccinated == true) "Sim" else "Não"}"
                    binding.isDewormedPostView.text = "Verfimungado: ${if (post?.isDewormed == true) "Sim" else "Não"}"
                    binding.isPedigreePostView.text = "Pedigree: ${if (post?.isPedigree == true) "Sim" else "Não"}"
                    binding.isSpecialNeed.text = "Cuidados especiais: ${if (post?.isEspecialNeeds == true) "Sim" else "Não"}"
                    binding.portPostView.text = "Porte: ${post?.weight}"
                    binding.aboutPostView.text = post?.about
                    binding.userNamePostView.text = post?.userName

                    post?.postPic.let {
                        if (it.toString().isNotEmpty()) {
                            binding.postPetPic.setImageBitmap(decodeBase64ToBitMap(it))
                        }
                    }
                    post?.userPic.let {
                        if (it.toString().isNotEmpty()) {
                            binding.userPostPic.setImageBitmap(decodeBase64ToBitMap(it))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetOnePost>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun decodeBase64ToBitMap(base64Code: String?): Bitmap {
        val imageBytes = Base64.decode(base64Code, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


}