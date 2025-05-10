package com.attt.vazitaapp.ui.inspector.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.attt.vazitaapp.data.model.DefectPoint
import com.attt.vazitaapp.databinding.ItemPointBinding

class PointAdapter(
    private val onPointSelected: (DefectPoint) -> Unit
) : ListAdapter<DefectPoint, PointAdapter.PointViewHolder>(PointDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val binding = ItemPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PointViewHolder(
        private val binding: ItemPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPointSelected(getItem(position))
                }
            }
        }

        fun bind(point: DefectPoint) {
            binding.textViewPointCode.text = point.codePoint
            binding.textViewPointName.text = point.libellePoint
        }
    }

    private class PointDiffCallback : DiffUtil.ItemCallback<DefectPoint>() {
        override fun areItemsTheSame(oldItem: DefectPoint, newItem: DefectPoint): Boolean {
            return oldItem.codePoint == newItem.codePoint && oldItem.codeChapter == newItem.codeChapter
        }

        override fun areContentsTheSame(oldItem: DefectPoint, newItem: DefectPoint): Boolean {
            return oldItem == newItem
        }
    }
}
