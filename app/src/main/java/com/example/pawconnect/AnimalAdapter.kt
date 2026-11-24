package com.example.pawconnect

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AnimalAdapter(
    private val animals: ArrayList<Animal>,
    private val onAnimalClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    class AnimalViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.ivAnimalItem)

        val name: TextView =
            itemView.findViewById(R.id.tvAnimalItemName)

        val type: TextView =
            itemView.findViewById(R.id.tvAnimalItemType)

        val status: TextView =
            itemView.findViewById(R.id.tvAnimalItemStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnimalViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)

        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AnimalViewHolder,
        position: Int
    ) {

        val animal = animals[position]
        val context = holder.itemView.context

        holder.name.text = animal.name
        holder.type.text = animal.type

        holder.status.text = when (animal.status) {
            "AVAILABLE" -> "Disponible"
            "RESERVED" -> "Réservé"
            "ADOPTED" -> "Adopté"
            else -> animal.status
        }

        // Image par défaut
        holder.image.setImageResource(
            android.R.drawable.ic_menu_gallery
        )

        // Charger l'image
        if (!animal.image.isNullOrEmpty()) {

            val imageResource = context.resources.getIdentifier(
                animal.image,
                "drawable",
                context.packageName
            )

            if (imageResource != 0) {

                holder.image.setImageResource(imageResource)

            } else {

                val imageFile = File(animal.image)

                if (imageFile.exists()) {
                    holder.image.setImageURI(
                        Uri.fromFile(imageFile)
                    )
                }
            }
        }

        // Clic sur la carte
        holder.itemView.setOnClickListener {
            onAnimalClick(animal)
        }
    }

    override fun getItemCount(): Int {
        return animals.size
    }

    fun updateList(newAnimals: ArrayList<Animal>) {
        animals.clear()
        animals.addAll(newAnimals)
        notifyDataSetChanged()
    }
}