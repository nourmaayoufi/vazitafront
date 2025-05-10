package com.attt.vazitaapp.ui.admin.defects


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.attt.vazitaapp.data.model.DossierDefect
import com.attt.vazitaapp.databinding.ItemDefectBinding
import com.attt.vazitaapp.util.DateUtils

class DefectReviewAdapter(
    private val onItemClick: (DossierDefect) -> Unit
) : ListAdapter<DossierDefect, DefectReviewAdapter.DefectViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefectViewHolder {
        val binding = ItemDefectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DefectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DefectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DefectViewHolder(
        private val binding: ItemDefectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(defect: DossierDefect) {
            binding.apply {
                tvDossierNumber.text = defect.nDossier
                tvChassis.text = defect.numChassis
                tvDefectCode.text = defect.codeDefaut.toString()

                // Format the defect code to show chapter-point-alteration format
                val formattedCode = "${defect.codeDefaut.toString().padStart(3, '0')}"
                tvDefectCode.text = formattedCode

                tvDate.text = DateUtils.formatDateForUI(defect.dateHeureEnregistrement)
                tvInspector.text = defect.matAgent
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DossierDefect>() {
        override fun areItemsTheSame(oldItem: DossierDefect, newItem: DossierDefect): Boolean {
            return oldItem.nDossier == newItem.nDossier &&
                    oldItem.codeDefaut == newItem.codeDefaut
        }

        override fun areContentsTheSame(oldItem: DossierDefect, newItem: DossierDefect): Boolean {
            return oldItem == newItem
        }
    }
}