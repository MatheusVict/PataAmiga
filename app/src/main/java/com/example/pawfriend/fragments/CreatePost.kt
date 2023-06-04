package com.example.pawfriend.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.pawfriend.EditProfileFragments
import com.example.pawfriend.Endpoint
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.R
import com.example.pawfriend.apiJsons.CreatePostPets
import com.example.pawfriend.databinding.FragmentCreatePostBinding
import com.example.pawfriend.global.AppGlobals
import com.google.android.material.chip.Chip
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
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        Log.i("APITESTE", "dados de outro frag ${arguments?.getString("message", null)}")

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

        binding.createPostButton.setOnClickListener { submitForm() }

        return binding.root

    }

    private fun submitForm() {
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
                val post: CreatePostPets = CreatePostPets(
                    name = binding.namePostInput.text.toString(),
                    postPic = profileImageBase64!!,
                    race = selectedRace!!,
                    specie = selectedSpecie!!,
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
                if (isNetworkAvailable(requireContext())) {
                    createPost(post)
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

    private fun createPost(post: CreatePostPets) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.createPostPets(post).enqueue(object: Callback<CreatePostPets>{
            override fun onResponse(
                call: Call<CreatePostPets>,
                response: Response<CreatePostPets>
            ) {
                if (response.isSuccessful) {
                    Log.i("APITESTE", "criado ${response.body()}")
                    Toast.makeText(
                        requireContext(),
                        "Post criado!!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else  if (response.code() == 409) {
                    Toast.makeText(
                        requireContext(),
                        "A imagem é muito grande selecione outra",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("APITESTE", "erro na api $response")
                }

            }

            override fun onFailure(call: Call<CreatePostPets>, t: Throwable) {
                Log.i("APITESTE", "erro $t")
            }
        })
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