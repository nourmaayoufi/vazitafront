package com.attt.vazitaapp.ui.inspector.queue


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.CarQueue
import com.attt.vazitaapp.util.DateUtils

class CarQueueAdapter(private val onItemClick: (CarQueue) -> Unit) :
    ListAdapter<CarQueue, CarQueueAdapter.CarQueueViewHolder>(CarQueueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarQueueViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car_queue, parent, false)
        return CarQueueViewHolder(itemView, onItemClick)
    }

    override fun onBindViewHolder(holder: CarQueueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CarQueueViewHolder(
        itemView: View,
        private val onItemClick: (CarQueue) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDossierNumber: TextView = itemView.findViewById(R.id.tvDossierNumber)
        private val tvChassisNumber: TextView = itemView.findViewById(R.id.tvChassisNumber)
        private val tvRegistration: TextView = itemView.findViewById(R.id.tvRegistration)
        private val tvLane: TextView = itemView.findViewById(R.id.tvLane)
        private val tvRegistrationTime: TextView = itemView.findViewById(R.id.tvRegistrationTime)

        fun bind(carQueue: CarQueue) {
            tvDossierNumber.text = carQueue.dossierNumber
            tvChassisNumber.text = carQueue.chassisNumber
            tvRegistration.text = carQueue.registration
            tvLane.text = carQueue.lane
            tvRegistrationTime.text = DateUtils.formatDateTime(carQueue.registrationTime)

            itemView.setOnClickListener {
                onItemClick(carQueue)
            }
        }
    }

    class CarQueueDiffCallback : DiffUtil.ItemCallback<CarQueue>() {
        override fun areItemsTheSame(oldItem: CarQueue, newItem: CarQueue): Boolean {
            return oldItem.dossierNumber == newItem.dossierNumber
        }

        override fun areContentsTheSame(oldItem: CarQueue, newItem: CarQueue): Boolean {
            return oldItem == newItem
        }
    }
}