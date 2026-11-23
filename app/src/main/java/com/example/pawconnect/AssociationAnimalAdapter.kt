
package com.example.pawconnect

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AssociationAnimalAdapter(
    private val animals: ArrayList<Animal>,
    private val onEdit: (Animal) -> Unit,
    private val onDelete: (Animal) -> Unit
) : RecyclerView.Adapter<AssociationAnimalAdapter.AnimalViewHolder>() {

    class AnimalViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.ivAssociationAnimal)

        val name: TextView =
            itemView.findViewById(R.id.tvAssociationAnimalName)

        val type: TextView =
            itemView.findViewById(R.id.tvAssociationAnimalType)

        val status: TextView =
            itemView.findViewById(R.id.tvAssociationAnimalStatus)

        val btnEdit: Button =
            itemView.findViewById(R.id.btnEditAnimal)

        val btnDelete: Button =
            itemView.findViewById(R.id.btnDeleteAnimal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnimalViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_association_animal,
            parent,
            false
        )

        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AnimalViewHolder,
        position: Int
    ) {

        val animal = animals[position]

        holder.name.text = animal.name
        holder.type.text = "Type : ${animal.type}"

        holder.status.text = when (animal.status) {
            "AVAILABLE" -> "Disponible"
            "RESERVED" -> "Réservé"
            "ADOPTED" -> "Adopté"
            else -> animal.status
        }

        if (!animal.image.isNullOrEmpty()) {

            val imageFile = File(animal.image)

            if (imageFile.exists()) {
                holder.image.setImageURI(Uri.fromFile(imageFile))
            } else {
                holder.image.setImageResource(
                    android.R.drawable.ic_menu_camera
                )
            }

        } else {
            holder.image.setImageResource(
                android.R.drawable.ic_menu_camera
            )
        }

        holder.btnEdit.setOnClickListener {
            onEdit(animal)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(animal)
        }
    }

    override fun getItemCount(): Int {
        return animals.size
    }
}
