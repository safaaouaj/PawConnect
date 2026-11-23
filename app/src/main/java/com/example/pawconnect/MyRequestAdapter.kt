
package com.example.pawconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class MyRequestItem(
    val animalName: String,
    val status: String
)

class MyRequestAdapter(
    private val requests: ArrayList<MyRequestItem>
) : RecyclerView.Adapter<MyRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val animalName: TextView =
            itemView.findViewById(R.id.tvMyRequestAnimal)

        val status: TextView =
            itemView.findViewById(R.id.tvMyRequestStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_my_request,
            parent,
            false
        )

        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RequestViewHolder,
        position: Int
    ) {

        val request = requests[position]

        holder.animalName.text = request.animalName

        holder.status.text = when (request.status) {
            "PENDING" -> "Statut : En attente"
            "ACCEPTED" -> "Statut : Acceptée"
            "REJECTED" -> "Statut : Refusée"
            else -> request.status
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
