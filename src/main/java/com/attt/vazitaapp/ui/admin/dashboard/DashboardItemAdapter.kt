package com.attt.vazitaapp.ui.admin.dashboard


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.attt.vazitaapp.databinding.ItemDashboardBinding

// Data class to represent dashboard items
data class DashboardItem(
    val title: String,
    val description: String,
    val iconResId: Int,
    val type: DashboardItemType
)

// Enum to represent dashboard item types
enum class DashboardItemType {
    USER_MANAGEMENT,
    DEFECT_REVIEW,
    ANALYTICS
}

class DashboardItemAdapter(private val onItemClicked: (DashboardItemType) -> Unit) :
    ListAdapter<DashboardItem, DashboardItemAdapter.DashboardItemViewHolder>(DashboardItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
        val binding = ItemDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DashboardItemViewHolder(private val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position).type)
                }
            }
        }

        fun bind(item: DashboardItem) {
            binding.apply {
                textTitle.text = item.title
                textDescription.text = item.description
                imageIcon.setImageResource(item.iconResId)
            }
        }
    }

    private class DashboardItemDiffCallback : DiffUtil.ItemCallback<DashboardItem>() {
        override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
            return oldItem == newItem
        }
    }
}