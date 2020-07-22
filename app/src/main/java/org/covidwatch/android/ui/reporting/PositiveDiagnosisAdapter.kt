package org.covidwatch.android.ui.reporting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayout.ExpandableLayout
import org.covidwatch.android.R
import org.covidwatch.android.data.PositiveDiagnosisReport

class PositiveDiagnosisAdapter : RecyclerView.Adapter<PositiveDiagnosisViewHolder>() {

    private lateinit var viewModel: PositiveDiagnosesViewModel

    private val positiveDiagnoses: MutableList<PositiveDiagnosisReport> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositiveDiagnosisViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_positive_diagnosis, parent, false)
        return PositiveDiagnosisViewHolder(root as ExpandableLayout)
    }

    override fun getItemCount(): Int = positiveDiagnoses.size

    override fun onBindViewHolder(holder: PositiveDiagnosisViewHolder, position: Int) {
        holder.bind(positiveDiagnoses[position], viewModel)
    }

    fun setViewModel(viewModel: PositiveDiagnosesViewModel) {
        this.viewModel = viewModel
    }

    fun setItems(items: List<PositiveDiagnosisReport>) {
        positiveDiagnoses.clear()
        positiveDiagnoses.addAll(items)
        notifyDataSetChanged()
    }
}