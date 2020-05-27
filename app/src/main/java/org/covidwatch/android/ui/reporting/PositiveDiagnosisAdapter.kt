package org.covidwatch.android.ui.reporting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R

class PositiveDiagnosisAdapter : RecyclerView.Adapter<PositiveDiagnosisViewHolder>() {

    private val positiveDiagnoses: MutableList<PositiveDiagnosisItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositiveDiagnosisViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_positive_diagnosis, parent, false)
        return PositiveDiagnosisViewHolder(root)
    }

    override fun getItemCount(): Int = positiveDiagnoses.size

    override fun onBindViewHolder(holder: PositiveDiagnosisViewHolder, position: Int) {
        holder.bind(positiveDiagnoses[position])
    }

    fun setItems(items: List<PositiveDiagnosisItem>) {
        positiveDiagnoses.clear()
        positiveDiagnoses.addAll(items)
        notifyDataSetChanged()
    }
}