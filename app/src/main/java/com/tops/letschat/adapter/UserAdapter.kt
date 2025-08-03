package com.tops.letschat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tops.letschat.ChatActivity
import com.tops.letschat.databinding.ItemUserBinding
import com.tops.letschat.model.User

class UserAdapter(private var users: List<User>,
                  private val onAddFriendClicked: (User) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onAddFriendClicked)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    class UserViewHolder(private val binding: ItemUserBinding, private val onAddFriendClicked: (User) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.name
            binding.userStatus.text = user.status
            Glide.with(binding.root.context)
                .load(user.profileImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery) // A default placeholder
                .into(binding.userImage)

            // Set click listener to open chat
            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, ChatActivity::class.java).apply {
                    putExtra("USER_ID", user.uid)
                    putExtra("USER_NAME", user.name)
                }
                context.startActivity(intent)
            }

            binding.btnAddnewfriend.setOnClickListener { onAddFriendClicked(user) }

        }
    }
}
