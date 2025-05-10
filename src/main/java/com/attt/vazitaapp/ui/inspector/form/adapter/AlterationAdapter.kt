package com.attt.vazitaapp.ui.inspector.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.Alteration
import com.attt.vazitaapp.databinding.ItemAlterationBinding

class AlterationAdapter(
    private val onAlterationSelected: (Alteration) -> Unit,
    private val isSelected: (Alteration) -> Boolean
) : ListAdapter<Alteration, AlterationAdapter.AlterationViewHolder>(AlterationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlterationViewHolder {
        val binding = ItemAlterationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlterationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlterationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AlterationViewHolder(
        private val binding: ItemAlterationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAlterationSelected(getItem(position))
                }
            }
        }

        fun bind(alteration: Alteration) {
            binding.textViewAlterationCode.text = alteration.codeAlteration
            binding.textViewAlterationName.text = alteration.libelleAlteration

            val selected = isSelected(alteration)
            val backgroundColorRes = if (selected) R.color.selected_item_background else android.R.color.transparent
            val textColorRes = if (selected) R.color.selected_item_text else R.color.default_text

            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, backgroundColorRes))
            binding.textViewAlterationCode.setTextColor(ContextCompat.getColor(binding.root.context, textColorRes))
            binding.textViewAlterationName.setTextColor(ContextCompat.getColor(binding.root.context, textColorRes))

            binding.checkboxSelected.isChecked = selected
        }
    }

    private class AlterationDiffCallback : DiffUtil.ItemCallback<Alteration>() {
        override fun areItemsTheSame(oldItem: Alteration, newItem: Alteration): Boolean {
            return oldItem.codeAlteration == newItem.codeAlteration &&
                    oldItem.codePoint == newItem.codePoint &&
                    oldItem.codeChapter == newItem.codeChapter
        }

        override fun areContentsTheSame(oldItem: Alteration, newItem: Alteration): Boolean {
            return oldItem == newItem
        }
    }
}