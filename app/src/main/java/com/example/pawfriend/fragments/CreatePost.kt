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
import com.example.pawfriend.R
import com.example.pawfriend.databinding.FragmentCreatePostBinding
import com.google.android.material.chip.Chip
import java.io.IOException

class CreatePost : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding: FragmentCreatePostBinding get() = _binding!!

    private lateinit var raceSpinner: Spinner

    private var selectedRace: String? = null

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
                    positon: Int,
                    id: Long
                ) {
                    val selectedItem = specieArray[positon]


                    val filteredRaces = getFilteredRaces(selectedItem)

                    if (positon != 0) {
                        Log.i("APITESTE", "specie $selectedItem")
                    }



                    val raceAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        filteredRaces
                    )
                    raceSpinner.adapter = raceAdapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        /*binding.selectRaceInput.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRace = getFilteredRaces()[position]
                val race = selectedRace
                if (race != null) {
                    Log.i("APITESTE", "raca $race")

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }*/

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

        if (validName && validLocation && validYears && validMouths && validAbout) {
            if (binding.sexChoice.checkedRadioButtonId != -1 && binding.portPostChoise.checkedRadioButtonId != -1) {
                val selectedSex =
                    binding.sexChoice.findViewById<RadioButton>(binding.sexChoice.checkedRadioButtonId).text.toString()
                val selectedPort =
                    binding.portPostChoise.findViewById<RadioButton>(binding.portPostChoise.checkedRadioButtonId).text.toString()

                Toast.makeText(
                    requireContext(),
                    "tudo passou $selectedPort e $selectedSex",
                    Toast.LENGTH_SHORT
                ).show()
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
        return when (specie) {
            "Gato" -> arrayOf(
                "vira-lata",
                "Persa",
                "Siamese",
                "Maine Coon",
                "Sphynx",
                "Bengal",
                "Ragdoll"
            )
            "Cachorro" -> arrayOf(
                "vira-lata",
                "Labrador",
                "Bulldog",
                "Golden Retriever",
                "Pastor alemão",
                "Beagle",
                "Rotivalei"
            )
            "Coelho" -> arrayOf(
                "vira-lata",
                "Mini Rex",
                "Lionhead",
                "Holland",
                "Flemish",
                "Netherland",
                "Angorá"
            )
            else -> emptyArray()
        }
    }

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
            return getString(R.string.create_post_years_empty)
        }
        return null
    }


}