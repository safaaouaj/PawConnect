
package com.example.pawconnect

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ReportAdapter(
    private val reports: ArrayList<Report>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.ivReportItem)

        val location: TextView =
            itemView.findViewById(R.id.tvReportLocation)

        val description: TextView =
            itemView.findViewById(R.id.tvReportDescription)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_report,
            parent,
            false
        )

        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReportViewHolder,
        position: Int
    ) {

        val report = reports[position]

        holder.location.text = "Lieu : ${report.location}"
        holder.description.text = report.description

        if (!report.image.isNullOrEmpty()) {

            val imageFile = File(report.image)

            if (imageFile.exists()) {
                holder.image.setImageURI(Uri.fromFile(imageFile))
            } else {
                holder.image.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }

        } else {
            holder.image.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }
    }

    override fun getItemCount(): Int {
        return reports.size
    }
}
