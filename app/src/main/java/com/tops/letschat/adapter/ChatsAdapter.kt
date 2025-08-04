package com.tops.letschat.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tops.letschat.ChatActivity
import com.tops.letschat.databinding.ItemChatBinding
import com.tops.letschat.model.User

class ChatsAdapter(private var friends: List<User>,
                   private val onRemoveFriend: (User) -> Unit) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding, onRemoveFriend)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    override fun getItemCount(): Int = friends.size

    fun updateFriends(newFriends: List<User>) {
        this.friends = newFriends
        notifyDataSetChanged()
    }

    class ChatViewHolder(private val binding: ItemChatBinding,
                         private val onRemoveFriend: (User) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {
            binding.userName.text = friend.name
            binding.lastMessage.text = friend.status // Using status as a placeholder for now

            Glide.with(binding.root.context)
                .load(friend.profileImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.userImage)

            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, ChatActivity::class.java).apply {
                    putExtra("USER_ID", friend.uid)
                    putExtra("USER_NAME", friend.name)
                }
                context.startActivity(intent)
            }

            // A long click will trigger the remove friend dialog
            itemView.setOnLongClickListener {
                val context = it.context
                AlertDialog.Builder(context)
                    .setTitle("Remove Friend")
                    .setMessage("Are you sure you want to remove ${friend.name} from your friends list?")
                    .setPositiveButton("Remove") { dialog, _ ->
                        onRemoveFriend(friend) // Call the remove function
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true // We return true to indicate we have handled the long click
            }
        }
    }
}
