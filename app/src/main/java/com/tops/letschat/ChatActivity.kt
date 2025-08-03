package com.tops.letschat

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tops.letschat.ViewModel.ChatViewModel
import com.tops.letschat.adapter.MessageAdapter
import com.tops.letschat.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        receiverId = intent.getStringExtra("USER_ID")
        val userName = intent.getStringExtra("USER_NAME")
        supportActionBar?.title = userName

        if (receiverId == null) {
            finish() // Can't chat without a receiver
            return
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.loadMessages(receiverId!!)

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            viewModel.sendMessage(receiverId!!, messageText)
            binding.etMessage.text.clear()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(emptyList())
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            messageAdapter.updateMessages(messages)
            binding.chatRecyclerView.scrollToPosition(messages.size - 1)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}