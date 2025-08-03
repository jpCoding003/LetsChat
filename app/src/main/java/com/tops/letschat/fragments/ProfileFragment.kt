package com.tops.letschat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.tops.letschat.LoginActivity
import com.tops.letschat.ViewModel.MainViewModel
import com.tops.letschat.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.currentUserData.observe(viewLifecycleOwner) { user ->
            binding.tvProfileName.text = user.name
            binding.tvProfileStatus.text = user.status
            Glide.with(this)
                .load(user.profileImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.profileImage)
        }

        viewModel.logoutState.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
