package com.tops.letschat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tops.letschat.ViewModel.MainViewModel
import com.tops.letschat.adapter.RequestAdapter
import com.tops.letschat.databinding.FragmentRequestsBinding

class RequestsFragment : Fragment() {

    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var requestAdapter: RequestAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        requestAdapter = RequestAdapter(
            emptyList(),
            onAccept = { user -> viewModel.acceptFriendRequest(user.uid) },
            onDecline = { user -> viewModel.declineFriendRequest(user.uid) }
        )
        binding.requestsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.friendRequests.observe(viewLifecycleOwner) { requests ->
            if (requests.isEmpty()) {
                binding.tvNoRequests.visibility = View.VISIBLE
                binding.requestsRecyclerView.visibility = View.GONE
            } else {
                binding.tvNoRequests.visibility = View.GONE
                binding.requestsRecyclerView.visibility = View.VISIBLE
                requestAdapter.updateRequests(requests)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}