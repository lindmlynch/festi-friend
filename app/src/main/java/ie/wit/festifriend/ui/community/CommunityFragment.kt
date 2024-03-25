package ie.wit.festifriend.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ie.wit.festifriend.adapters.PostAdapter
import ie.wit.festifriend.databinding.FragmentCommunityBinding
import android.widget.Toast
import ie.wit.festifriend.R

@AndroidEntryPoint
class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private val communityViewModel: CommunityViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFAB()
        observeViewModel()

        communityViewModel.fetchPosts()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(listOf())
        binding.postRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun setupFAB() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_communityFragment_to_postFragment)
        }
    }

    private fun observeViewModel() {
        communityViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.updatePosts(posts)
        }

        communityViewModel.uploadSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Review posted successfully", Toast.LENGTH_SHORT).show()
                communityViewModel.fetchPosts()
            } else {
                Toast.makeText(context, "Failed to post review", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

