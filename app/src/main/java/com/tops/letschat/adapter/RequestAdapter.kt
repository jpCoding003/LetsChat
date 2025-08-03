package com.tops.letschat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tops.letschat.databinding.ItemFriendRequestBinding
import com.tops.letschat.model.User

class RequestAdapter(
    private var requests: List<User>,
    private val onAccept: (User) -> Unit,
    private val onDecline: (User) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding, onAccept, onDecline)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    fun updateRequests(newRequests: List<User>) {
        this.requests = newRequests
        notifyDataSetChanged()
    }

    class RequestViewHolder(
        private val binding: ItemFriendRequestBinding,
        private val onAccept: (User) -> Unit,
        private val onDecline: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.name
            Glide.with(binding.root.context).load(user.profileImageUrl).into(binding.userImage)

            binding.btnAccept.setOnClickListener { onAccept(user) }
            binding.btnDecline.setOnClickListener { onDecline(user) }
        }
    }
}
