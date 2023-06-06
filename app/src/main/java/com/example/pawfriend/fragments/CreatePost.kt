package com.example.pawfriend.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.Endpoint
import com.example.pawfriend.Login
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.R
import com.example.pawfriend.apiJsons.PostPets
import com.example.pawfriend.apiJsons.GetOnePost
import com.example.pawfriend.databinding.FragmentCreatePostBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CreatePost : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding: FragmentCreatePostBinding get() = _binding!!

    private lateinit var raceSpinner: Spinner

    private var selectedRace: String? = null
    private var selectedSpecie: String? = null

    private var profileImageBase64: String? = null
    private val REQUEST_IMAGE_PICK = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        Log.i("APITESTE", "dados de outro frag ${arguments?.getString("message", null)}")

        val idPost = arguments?.getString("idPost")

        if (isNetworkAvailable(requireContext())) {
            idPost?.let {postId ->
                binding.createPostButton.text = "atualizar"
                getPostInstance(postId.toLong())
                binding.createPostButton.setOnClickListener {
                    submitForm(postId.toLong())
                }
            }?: run {
                binding.createPostButton.setOnClickListener{
                    submitForm()
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.verify_your_connection),
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }

        petNameFocusListener()
        locationFocusListener()
        yearsAgeFocusListener()
        mouthsAgeFocusListener()
        aboutFocusListener()

        raceSpinner = binding.selectRaceInput
        val emptyRaceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            emptyArray<String>()
        )
        raceSpinner.adapter = emptyRaceAdapter
        binding.speciePostInput.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                val specieArray = resources.getStringArray(R.array.species_array)

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = specieArray[position]
                    selectedSpecie = selectedItem

                    val filteredRaces = getFilteredRaces(selectedItem)

                    if (position != 0) {
                        Log.i("APITESTE", "specie $selectedItem")
                    } else selectedSpecie = null

                    val raceAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        filteredRaces
                    )
                    binding.selectRaceInput.adapter = raceAdapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // TODO:
                }
            }

        binding.selectRaceInput.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedRace = binding.selectRaceInput.selectedItem.toString()
                    val race = selectedRace
                    if (race != null) {
                        Log.i("APITESTE", "raca $race")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // TODO:
                }
            }

        binding.sexChoice.setOnCheckedChangeListener { radioGroup, chekedId ->
            val selectdRadioButton: RadioButton = radioGroup.findViewById(chekedId)
            val selectedSex = selectdRadioButton.text.toString()
        }

        binding.selectImageButton.setOnClickListener {
            openGallery(REQUEST_IMAGE_PICK)
        }



        return binding.root

    }

    private fun submitForm(idPost: Long? = null) {
        binding.namePostInput.error = validPetName()
        binding.locationPostInput.error = validLocation()
        binding.yearAgePostInput.error = validYearOld()
        binding.mounthsAgePostInput.error = validMouthsYearOld()
        binding.aboutPostInput.error = validAbout()

        val validName = binding.namePostInput.error == null
        val validLocation = binding.locationPostInput.error == null
        val validYears = binding.yearAgePostInput.error == null
        val validMouths = binding.mounthsAgePostInput.error == null
        val validAbout = binding.aboutPostInput.error == null

        if (validName && validLocation && validYears && validMouths && validAbout && selectedSpecie != null && profileImageBase64 != null) {
            if (binding.sexChoice.checkedRadioButtonId != -1 && binding.portPostChoise.checkedRadioButtonId != -1) {
                val selectedSex =
                    binding.sexChoice.findViewById<RadioButton>(binding.sexChoice.checkedRadioButtonId).text.toString()
                val selectedPort =
                    binding.portPostChoise.findViewById<RadioButton>(binding.portPostChoise.checkedRadioButtonId).text.toString()
                val post = PostPets(
                    name = binding.namePostInput.text.toString(),
                    postPic = profileImageBase64.toString(),
                    race = selectedRace!!,
                    specie = selectedSpecie.toString(),
                    sex = selectedSex,
                    age = "${binding.yearAgePostInput.text.toString()} anos ${binding.mounthsAgePostInput.text.toString()} meses",
                    size = selectedPort,
                    weight = selectedPort,
                    about = binding.aboutPostInput.text.toString(),
                    petLocation = binding.locationPostInput.text.toString(),
                    isAdopted = false,
                    isCastrated = binding.isCastratedCheckBox.isChecked,
                    isVaccinated = binding.isVaccinatedCheckBox.isChecked,
                    isPedigree = binding.isPedigreeCheckBox.isChecked,
                    isDewormed = binding.isDewormedChekBox.isChecked,
                    isEspecialNeeds = binding.isEspecialNeedsChekBox.isChecked,
                )
                Log.i("APITESTE", "selecionado ${selectedSpecie.toString()}")
                Log.i("APITESTE", "location ${binding.locationPostInput.text.toString()}")
                if (isNetworkAvailable(requireContext())) {
                    idPost?.let {
                        updatePostInstance(it, post)
                    }?: run {
                        createPost(post)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Você não está conectado a rede",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else Toast.makeText(
                requireContext(),
                getString(R.string.create_post_radios_empty),
                Toast.LENGTH_SHORT
            ).show()
        } else Toast.makeText(
            requireContext(),
            getString(R.string.toast_error_inputs),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun createPost(post: PostPets) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.createPostPets(post).enqueue(object: Callback<PostPets>{
            override fun onResponse(
                call: Call<PostPets>,
                response: Response<PostPets>
            ) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "criado ${response.body()}")
                    Toast.makeText(
                        requireContext(),
                        "Post criado!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val bundle = Bundle().apply {
                        putString("idPost", response.body()?.id.toString())
                    }

                    findNavController().navigate(R.id.action_menu_create_post_to_viewPostFragment, bundle)
                } else  if (response.code() == 409) {
                    Toast.makeText(
                        requireContext(),
                        "A imagem é muito grande selecione outra",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("APITESTE", "erro na api $response")
                }

            }

            override fun onFailure(call: Call<PostPets>, t: Throwable) {
                Log.i("APITESTE", "erro $t")
            }
        })
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
                    binding.namePostInput.setText(post?.name)

                    val getSpeciesArrays = resources.getStringArray(R.array.species_array)
                    val getSpecieIndex = getSpeciesArrays.indexOf(post?.specie)
                    binding.speciePostInput.setSelection(getSpecieIndex)

                    /*val racesFiltereds = getFilteredRaces(post?.specie.toString())
                    val getIndexofRace = racesFiltereds.indexOf(post?.race)
                    binding.selectRaceInput.setSelection(getIndexofRace)*/

                    binding.locationPostInput.setText(post?.petLocation)

                    for (i in 0 until binding.sexChoice.childCount) {
                        val sexChoiseSelected = binding.sexChoice.getChildAt(i) as RadioButton
                        val sexValue = sexChoiseSelected.text.toString()

                        if(sexValue == post?.sex) {
                            sexChoiseSelected.isChecked = true
                            break
                        }
                    }

                    val age = post?.age?.split(" ")
                    val years = age?.get(0)
                    val mouths = age?.get(2)
                    binding.yearAgePostInput.setText(years)
                    binding.mounthsAgePostInput.setText(mouths)

                    for (i in 0 until binding.portPostChoise.childCount) {
                        val portChoiseSelected = binding.portPostChoise.getChildAt(i) as RadioButton
                        val portValue = portChoiseSelected.text.toString()

                        if (portValue == post?.weight) {
                            portChoiseSelected.isChecked = true
                            break
                        }
                    }

                    binding.isCastratedCheckBox.isChecked = post!!.isCastrated
                    binding.isVaccinatedCheckBox.isChecked = post.isVaccinated
                    binding.isDewormedChekBox.isChecked = post.isDewormed
                    binding.isPedigreeCheckBox.isChecked = post.isPedigree
                    binding.isEspecialNeedsChekBox.isChecked = post.isEspecialNeeds
                    binding.aboutPostInput.setText(post.about)
                    Log.i("APITESTE", "location ${post?.petLocation} e specie ${post?.specie}")

                    post.postPic.let {base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code)
                        if (bitmap != null) {
                            binding.selectImageButton.setImageBitmap(bitmap)
                            profileImageBase64 = base64code
                        } else {
                            binding.selectImageButton.setImageResource(R.drawable.no_pet_pic_placeholder)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetOnePost>, t: Throwable) {

            }
        })
    }

    private fun updatePostInstance(idPost: Long, post: PostPets) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.updatePostPetsForId(idPost, post).enqueue(object: Callback<PostPets> {
            override fun onResponse(call: Call<PostPets>, response: Response<PostPets>) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "aqui o post ${response.body()}")
                    Toast.makeText(requireContext(), "atualizado com sucesso", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 409) {
                    Toast.makeText(requireContext(), "imagem muito grande selecione outra", Toast.LENGTH_SHORT).show()
                } else  Toast.makeText(requireContext(), "Erro ao atualizar", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<PostPets>, t: Throwable) {
                Toast.makeText(requireContext(), "Erro tente novamente mais tarde", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun decodeBase64ToBitMap(base64Code: String): Bitmap? {
        base64Code.let {
            val imageBytes = Base64.decode(it, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }

    private fun bytesToBase64(imageBytes: ByteArray): String {
        val base64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
        return base64.replace("\n", "")
    }

    private fun openGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(it)
                    val imageBytes = inputStream?.readBytes()
                    val base64Image = imageBytes?.let { bytesToBase64(it) }
                    binding.selectImageButton.setImageURI(it)
                    profileImageBase64 = base64Image
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getFilteredRaces(specie: String): Array<String> {
        val speciesList = listOf(
            Species("Gato", resources.getStringArray(R.array.cats_races_array)),
            Species("Cachorro", resources.getStringArray(R.array.dog_races_array)),
            Species("Coelho", resources.getStringArray(R.array.bunny_races_array))
        )

        val selectedSpecies = speciesList.find { it.name == specie }
        return selectedSpecies?.races ?: emptyArray()
    }

    data class Species(val name: String, val races: Array<String>)

    private fun petNameFocusListener() {
        binding.namePostInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.namePostInput.error = validPetName()
            }
        }
    }

    private fun validPetName(): String? {
        if (binding.namePostInput.text.toString().isEmpty()) {
            return getString(R.string.inputEmptyError)
        }
        if (binding.namePostInput.text.toString().length < 3) {
            return getString(R.string.create_post_name_small)
        }
        return null
    }

    private fun locationFocusListener() {
        binding.locationPostInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.locationPostInput.error = validLocation()
            }
        }
    }

    private fun validLocation(): String? {
        if (binding.locationPostInput.text.toString().isEmpty()) {
            return getString(R.string.inputEmptyError)
        }
        return null
    }

    private fun yearsAgeFocusListener() {
        binding.yearAgePostInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.yearAgePostInput.error = validYearOld()
            }
        }
    }

    private fun validYearOld(): String? {
        if (binding.yearAgePostInput.text.toString().isEmpty()) {
            return getString(R.string.create_post_years_empty)
        }
        return null
    }

    private fun mouthsAgeFocusListener() {
        binding.mounthsAgePostInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.mounthsAgePostInput.error = validMouthsYearOld()
            }
        }
    }

    private fun validMouthsYearOld(): String? {
        if (binding.mounthsAgePostInput.text.toString().isEmpty()) {
            return getString(R.string.create_post_mouths_empty)
        }
        return null
    }

    private fun aboutFocusListener() {
        binding.aboutPostInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.mounthsAgePostInput.error = validAbout()
            }
        }
    }

    private fun validAbout(): String? {
        if (binding.aboutPostInput.text.toString().isEmpty()) {
            return getString(R.string.inputEmptyError)
        }
        return null
    }


}