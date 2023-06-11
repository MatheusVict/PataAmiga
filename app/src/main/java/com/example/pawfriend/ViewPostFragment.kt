package com.example.pawfriend


import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.apiJsons.GetOnePost
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.databinding.AreYouSureDialogBinding
import com.example.pawfriend.databinding.ContactsAlertDialogBinding
import com.example.pawfriend.databinding.CustomDialogBinding
import com.example.pawfriend.databinding.FragmentViewPostBinding
import com.example.pawfriend.global.AppGlobals
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewPostFragment : Fragment() {
    private var _binding: FragmentViewPostBinding? = null
    private val binding: FragmentViewPostBinding get() = _binding!!

    private lateinit var dialog: AlertDialog



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
            binding.deletePostButton.visibility = View.VISIBLE
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
            binding.deletePostButton.setOnClickListener {
                idPost?.let {
                    areYouSureAlertDialog(
                        getString(R.string.are_you_sure_delete_this_post),
                        getString(R.string.confirm_button),
                        getString(R.string.refuse_button),
                        it
                    )
                }

            }
        } else {

            showNoConnectionDialog(
                getString(R.string.dialog_connection_error_title),
                getString(R.string.dialog_connection_error_message),
                getString(R.string.dialog_connection_error_button),
                resources.getDrawable(R.drawable.lost_connectio)
            )
        }

        return binding.root
    }

    private fun showNoConnectionDialog(
        title: String,
        message: String,
        messageButton: String,
        imageId: Drawable,
        isServerError: Boolean = false

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
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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

    private fun areYouSureAlertDialog(
        title: String,
        messageConfirmButton: String,
        messageResfuseButton: String,
        idPost: Long
    ) {
        val build = AlertDialog.Builder(requireContext())

        val view: AreYouSureDialogBinding =
            AreYouSureDialogBinding.inflate(LayoutInflater.from(requireContext()))



        view.questionText.text = title
        view.confirmButton.text = messageConfirmButton
        view.refuseButton.text = messageResfuseButton


        view.refuseButton.setOnClickListener {
            dialog.dismiss()
        }
        view.confirmButton.setOnClickListener {
            deletePost(idPost)
            dialog.dismiss()
        }

        build.setView(view.root)

        dialog = build.create()
        dialog.show()
    }
    private fun contactCardDialog(
        userEmail: String,
        userPhone: String,
        userWhatsapp: String,
        userInstagram: String?,
        userFaceBook: String?
    ) {
        val build = AlertDialog.Builder(requireContext())

        val view: ContactsAlertDialogBinding =
            ContactsAlertDialogBinding.inflate(LayoutInflater.from(requireContext()))

        view.userEmailText.text = userEmail
        view.userPhoneText.text = userPhone
        view.userWhatsappText.text = userWhatsapp
        view.userInstagramText.text = userInstagram
        view.userFacebookText.text = userFaceBook

        view.userEmailText.setOnClickListener {
            val textToCopy = view.userEmailText.text.toString()

            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("email", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "copiado", Toast.LENGTH_SHORT).show()
        }
        view.userPhoneText.setOnClickListener {
            val textToCopy = view.userPhoneText.text.toString()

            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("phone", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "copiado", Toast.LENGTH_SHORT).show()
        }
        view.userWhatsappText.setOnClickListener {
            val textToCopy = view.userWhatsappText.text.toString()

            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("phone", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "copiado", Toast.LENGTH_SHORT).show()
        }
        view.userInstagramText.setOnClickListener {
            val textToCopy = view.userInstagramText.text.toString()

            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("phone", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "copiado", Toast.LENGTH_SHORT).show()
        }
        view.userFacebookText.setOnClickListener {
            val textToCopy = view.userFacebookText.text.toString()

            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("phone", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "copiado", Toast.LENGTH_SHORT).show()
        }

        view.closeButton.setOnClickListener {
            dialog.dismiss()
        }


        build.setView(view.root)

        dialog = build.create()
        dialog.show()
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
                            userWhatsapp = it.userWhatsapp,
                            userEmail = it.userEmail,
                            userFacebook = it.userFacebook,
                            userInstagram = it.userInstagram,
                            userPhone = it.userPhone
                        )
                    }

                    binding.buttonAdoppetButton.setOnClickListener {
                        contactCardDialog(
                            post?.userEmail ?: getString(R.string.dialog_whatsapp_empty),
                            post?.userPhone ?: getString(R.string.dialog_whatsapp_empty),
                            post?.userWhatsapp ?: getString(R.string.dialog_whatsapp_empty),
                            post?.userInstagram ?: getString(R.string.dialog_instagram_empty),
                            post?.userFacebook ?: getString(R.string.dialog_facebook_empty)
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

                    post?.postPic.let {base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code!!)
                        if (bitmap != null) {
                            binding.postPetPic.setImageBitmap(bitmap)
                        } else {
                            binding.postPetPic.setImageResource(R.drawable.no_pet_pic_placeholder)
                        }
                    }
                    post?.userPic.let{base64code ->
                        val bitmap = decodeBase64ToBitMap(base64code!!)
                        if (bitmap != null) {
                            binding.userPostPic.setImageBitmap(bitmap)
                        } else {
                            binding.userPostPic.setImageResource(R.drawable.no_user_pic_placeholder)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetOnePost>, t: Throwable) {
                showNoConnectionDialog(
                    getString(R.string.dialog_error_request_title),
                    getString(R.string.dialog_error_request_message),
                    getString(R.string.server_error),
                    resources.getDrawable(R.drawable.lost_server),
                    true
                )
            }
        })
    }


    private fun decodeBase64ToBitMap(base64Code: String): Bitmap? {
        base64Code.let {
            val imageBytes = Base64.decode(it, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }

    private fun deletePost(idPost: Long) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = activity?.applicationContext!!)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.deletePostForId(idPost).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "post deletado com sucesso", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_viewPostFragment_to_menu_profile)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_viewPostFragment_to_menu_create_post)
                    findNavController().popBackStack(R.id.viewPostFragment, true)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showNoConnectionDialog(
                    getString(R.string.dialog_error_request_title),
                    getString(R.string.dialog_error_request_message),
                    getString(R.string.dialog_error_request_button),
                    resources.getDrawable(R.drawable.lost_server),
                    true
                )
            }
        })
    }

}