package ie.wit.festifriend.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import ie.wit.festifriend.databinding.FragmentPostBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import ie.wit.festifriend.R
import ie.wit.festifriend.models.PostModel

@AndroidEntryPoint
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val communityViewModel: CommunityViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val args: PostFragmentArgs by navArgs()
    private var editingPost: PostModel? = null

    private val imagePickerResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imagePreview.setImageURI(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editingPost = args.post

        if (editingPost != null) {
            binding.reviewEditText.setText(editingPost?.text)
            editingPost?.imageUrl?.let {
                Picasso.get().load(it).into(binding.imagePreview)
            }
            binding.submitPostButton.text = getString(R.string.update_post)
        } else {
            binding.submitPostButton.text = getString(R.string.submit_post)
        }
        setupUI()
    }

    private fun setupUI() {
        binding.selectImageButton.setOnClickListener {
            imagePickerResultLauncher.launch("image/*")
        }

        binding.submitPostButton.setOnClickListener {
            val reviewText = binding.reviewEditText.text.toString()
            if (reviewText.isNotEmpty()) {
                if (selectedImageUri != null || editingPost != null) {
                    if (editingPost == null) {
                        selectedImageUri?.let { uri ->
                            communityViewModel.uploadImage(uri)
                            observeImageUpload(reviewText)
                        } ?: Toast.makeText(context, "Please select an image.", Toast.LENGTH_SHORT).show()
                    } else {
                        updateExistingPost(reviewText)
                    }
                } else {
                    Toast.makeText(context, "Please select an image.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter a review.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeImageUpload(reviewText: String) {
        communityViewModel.imageUploadUrl.observe(viewLifecycleOwner) { imageUrl ->
            imageUrl?.let { url ->
                postReview(reviewText, url)
            } ?: Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_LONG).show()
            communityViewModel.imageUploadUrl.removeObservers(viewLifecycleOwner)
        }
    }

    private fun postReview(reviewText: String, imageUrl: String) {
        val postModel = PostModel(
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            userName = FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous",
            text = reviewText,
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis()
        )
        if (editingPost == null) {
            communityViewModel.postReview(postModel)
        } else {
            communityViewModel.updatePost(editingPost!!.id, postModel)
        }
        navigateBackToCommunityFragment()
    }

    private fun updateExistingPost(reviewText: String) {
        val updatedPost = editingPost?.copy(text = reviewText)
        updatedPost?.let {
            communityViewModel.updatePost(it.id, it)
            Toast.makeText(context, "Post updated successfully!", Toast.LENGTH_SHORT).show()
            navigateBackToCommunityFragment()
        }
    }

    private fun navigateBackToCommunityFragment() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}