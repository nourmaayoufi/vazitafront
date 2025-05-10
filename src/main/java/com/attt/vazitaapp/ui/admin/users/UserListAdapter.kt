package com.attt.vazitaapp.ui.admin.users


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.databinding.ItemUserBinding

class UserListAdapter(
    private val onUserClicked: (User) -> Unit
) : ListAdapter<User, UserListAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = getItem(position)
                    onUserClicked(user)
                }
            }
        }

        fun bind(user: User) {
            binding.apply {
                textViewUserId.text = user.idUser
                textViewUserName.text = "${user.prenom} ${user.nom}"

                // Display role based on codGrp
                val roleName = when (user.codGrp) {
                    1 -> "Admin"
                    2 -> "Inspector"
                    3 -> "Adjoint"
                    else -> "Unknown"
                }
                textViewUserRole.text = roleName

                // Display user status
                val statusText = if (user.etat == 1) "Active" else "Inactive"
                textViewUserStatus.text = statusText

                // Display center ID
                textViewUserCenter.text = "Center: ${user.idCentre}"
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.idUser == newItem.idUser
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}