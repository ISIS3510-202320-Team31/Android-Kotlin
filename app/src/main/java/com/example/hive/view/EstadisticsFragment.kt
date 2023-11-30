package com.example.hive.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hive.R
import com.example.hive.model.adapters.PartnerNamesAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.UserCacheResponse
import com.example.hive.model.room.entities.User
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.Resource
import com.example.hive.viewmodel.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class EstadisticsFragment : Fragment() {

    private lateinit var pieChart: PieChart  // Agrega esta línea

    // Top Partners (RecyclerView)
    private lateinit var namesRecyclerView: RecyclerView
    private lateinit var namesAdapter: PartnerNamesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadistics, container, false)

        // Inicializa el PieChart
        pieChart = view.findViewById(R.id.pieChart)

        // Configura los datos de ejemplo para el gráfico
        val categories = listOf("Categoria A", "Categoria B", "Categoria C")
        val values = listOf(30f, 40f, 30f)  // Porcentajes que suman 100

        // Configura el gráfico
        setupPieChart(categories, values)

        //inicializa el recycler view
        namesRecyclerView = view.findViewById(R.id.namesRecyclerView)
        namesAdapter = PartnerNamesAdapter()

        // Configurar el RecyclerView con un LinearLayoutManager y el Adapter
        namesRecyclerView.layoutManager = LinearLayoutManager(context)
        namesRecyclerView.adapter = namesAdapter

        // Simulación de datos
        val samplesNames = listOf("Laura Valentina Martinez Presa", "Darwin Esteban Aguilar Figueroa", "Cristopher Arturo Sandino Ordoñez")
        namesAdapter.submitList(samplesNames)

        return view
    }

    private fun setupPieChart(categories: List<String>, values: List<Float>) {
        val entries: ArrayList<PieEntry> = ArrayList()

        // Agrega los datos al conjunto de entradas
        for (i in categories.indices) {
            entries.add(PieEntry(values[i], categories[i]))
        }

        // Configura el conjunto de datos
        val dataSet = PieDataSet(entries, "Eventos por Categoría")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        // Configura el conjunto de datos en el gráfico
        val data = PieData(dataSet)
        pieChart.data = data

        // Configura otras propiedades del gráfico (puedes personalizar según tus necesidades)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.setDrawEntryLabels(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // Actualiza el gráfico
        pieChart.invalidate()
    }

}
