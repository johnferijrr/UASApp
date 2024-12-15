package com.example.retrofit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit.databinding.ItemBinding
import com.example.retrofit.network.User

class UserAdapter(
    private val users: List<User>,
    private val onItemClick: (User, ActionType) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    enum class ActionType { DELETE, UPDATE, DETAIL }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(
        private val binding: ItemBinding,
        private val onItemClick: (User, ActionType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUsername.text = user.username
            binding.tvPassword.text = user.password

            // Assign actions to buttons
            binding.btnDelete.setOnClickListener { onItemClick(user, ActionType.DELETE) }
            binding.btnUpdate.setOnClickListener { onItemClick(user, ActionType.UPDATE) }
            binding.root.setOnClickListener { onItemClick(user, ActionType.DETAIL) }
        }
    }
}
