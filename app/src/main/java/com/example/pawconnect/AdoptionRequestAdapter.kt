
package com.example.pawconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AdoptionRequestItem(
    val id: Int,
    val animalName: String,
    val userName: String,
    val status: String
)

class AdoptionRequestAdapter(
    private val requests: ArrayList<AdoptionRequestItem>,
    private val onAccept: (AdoptionRequestItem) -> Unit,
    private val onReject: (AdoptionRequestItem) -> Unit
) : RecyclerView.Adapter<AdoptionRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val animalName: TextView =
            itemView.findViewById(R.id.tvRequestAnimalName)

        val userName: TextView =
            itemView.findViewById(R.id.tvRequestUserName)

        val status: TextView =
            itemView.findViewById(R.id.tvRequestStatus)

        val btnAccept: Button =
            itemView.findViewById(R.id.btnAcceptRequest)

        val btnReject: Button =
            itemView.findViewById(R.id.btnRejectRequest)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_adoption_request,
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

        holder.userName.text =
            "Demandeur : ${request.userName}"

        holder.status.text = when (request.status) {
            "PENDING" -> "Statut : En attente"
            "ACCEPTED" -> "Statut : Acceptée"
            "REJECTED" -> "Statut : Refusée"
            else -> "Statut : ${request.status}"
        }

        val isPending = request.status == "PENDING"

        holder.btnAccept.visibility =
            if (isPending) View.VISIBLE else View.GONE

        holder.btnReject.visibility =
            if (isPending) View.VISIBLE else View.GONE

        holder.btnAccept.setOnClickListener {
            onAccept(request)
        }

        holder.btnReject.setOnClickListener {
            onReject(request)
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
