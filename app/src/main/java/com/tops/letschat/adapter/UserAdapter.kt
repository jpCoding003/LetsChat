package com.tops.letschat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tops.letschat.databinding.ItemUserBinding
import com.tops.letschat.model.User

class UserAdapter(private var users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.name
            binding.userStatus.text = user.status
            Glide.with(binding.root.context)
                .load(user.profileImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery) // A default placeholder
                .into(binding.userImage)
        }
    }
}
