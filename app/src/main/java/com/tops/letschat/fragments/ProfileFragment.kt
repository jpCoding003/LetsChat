package com.tops.letschat.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.tops.letschat.LoginActivity
import com.tops.letschat.ViewModel.MainViewModel
import com.tops.letschat.ViewModel.ProfileViewModel
import com.tops.letschat.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels for data shared across fragments (like user info, logout)
    private val mainViewModel: MainViewModel by activityViewModels()
    // Use viewModels for logic specific to this fragment (updating profile)
    private val profileViewModel: ProfileViewModel by viewModels()

    // Activity result launcher for picking an image from the gallery
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { imageUri ->
                // Once we have the image URI, tell the ViewModel to upload it
                profileViewModel.updateProfileImage(imageUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModels()
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
        }

        binding.btnChangeStatus.setOnClickListener {
            showStatusUpdateDialog()
        }

        binding.btnChangeImage.setOnClickListener {
            // Launch the image picker
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }
    }

    private fun observeViewModels() {
        // Observe user data from the shared MainViewModel
        mainViewModel.currentUserData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvProfileName.text = user.name
                binding.tvProfileStatus.text = user.status
                Glide.with(this)
                    .load(user.profileImageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.profileImage)
            }
        }

        // Observe logout state from the shared MainViewModel
        mainViewModel.logoutState.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun showStatusUpdateDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Status")

        val input = EditText(requireContext())
        input.setText(binding.tvProfileStatus.text)
        builder.setView(input)

        builder.setPositiveButton("Update") { dialog, _ ->
            val newStatus = input.text.toString().trim()
            if (newStatus.isNotEmpty()) {
                profileViewModel.updateStatus(newStatus)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}