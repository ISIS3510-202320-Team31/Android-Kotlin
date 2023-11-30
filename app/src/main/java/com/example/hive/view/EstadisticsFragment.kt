package com.example.hive.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hive.R
import com.example.hive.model.adapters.PartnerNamesAdapter
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.CategoryResponse
import com.example.hive.model.room.entities.CategoryChart
import com.example.hive.util.ConnectionLiveData
import com.example.hive.util.Resource
import com.example.hive.viewmodel.UserProfileOfflineViewModel
import com.example.hive.viewmodel.UserProfileOfflineViewModelProviderFactory
import com.example.hive.viewmodel.UserProfileViewModel
import com.example.hive.viewmodel.UserProfileViewModelProviderFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class EstadisticsFragment : Fragment() {

    private lateinit var pieChart: PieChart  // Agrega esta línea

    // Top Partners (RecyclerView)
    private lateinit var namesRecyclerView: RecyclerView
    private lateinit var namesAdapter: PartnerNamesAdapter
    private lateinit var user: UserSession
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var viewModelUserProfileOffline: UserProfileOfflineViewModel
    private lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadistics, container, false)

        val userSession = SessionManager(requireContext())
        user = userSession.getUserSession()

        val viewModelFactoryOffline = context?.let{
            UserProfileOfflineViewModelProviderFactory(
                it
            )
        }

        viewModelUserProfileOffline =
            viewModelFactoryOffline?.let{
                ViewModelProvider(this,
                    it
                ).get(UserProfileOfflineViewModel::class.java)
            }!!
        pieChart = view.findViewById(R.id.pieChart)
        viewModelUserProfileOffline.allCategories?.observe(viewLifecycleOwner, Observer { resource ->
            val list = mutableListOf<CategoryResponse>()
            resource.let {
                println("resource: $it")
                for (category in it){
                    val categoryToAdd = CategoryResponse(
                        category.category?:"",
                        category.value?:0f,
                        category.color?:""
                    )
                    list.add(categoryToAdd)
                }
                val categories = list.map { it.category }
                val values = list.map { it.value }
                val colors = list.map { it.color }
                setupPieChart(categories, values, colors)

                //inicializa el recycler view
                namesRecyclerView = view.findViewById(R.id.namesRecyclerView)
                namesAdapter = PartnerNamesAdapter()

                // Configurar el RecyclerView con un LinearLayoutManager y el Adapter
                namesRecyclerView.layoutManager = LinearLayoutManager(context)
                namesRecyclerView.adapter = namesAdapter

                // Simulación de datos
                val samplesNames = listOf("Laura Valentina Martinez Presa", "Darwin Esteban Aguilar Figueroa", "Cristopher Arturo Sandino Ordoñez")
                namesAdapter.submitList(samplesNames)

            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData = ConnectionLiveData(requireContext())

        connectionLiveData.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected){

                sessionManager = SessionManager(requireContext())
                val viewModelFactory = sessionManager?.let{ UserProfileViewModelProviderFactory(it,requireContext()) }
                viewModel = ViewModelProvider(this, viewModelFactory!!).get(UserProfileViewModel::class.java)

                //update user detail
                viewModel.categoryChart.observe(viewLifecycleOwner, Observer { resourceCategoryChart ->

                    when (resourceCategoryChart){
                        is Resource.Loading<*> -> {
                        }
                        is Resource.Success<*> -> {
                            viewModelUserProfileOffline.removeCategoryDatabase()

                            resourceCategoryChart.data?.forEach { category ->
                                val categoryToAdd = CategoryChart(
                                    category.category?:"",
                                    category.value?:0f,
                                    category.color?:""
                                )
                                viewModelUserProfileOffline.insertCategory(categoryToAdd)
                            }
                            val categoriesTotal = resourceCategoryChart.data as List<CategoryResponse>
                            val categories = categoriesTotal.map { it.category }
                            val values = categoriesTotal.map { it.value }
                            val colors = categoriesTotal.map { it.color }
                            setupPieChart(categories, values, colors)

                        }
                        is Resource.Error<*> -> {
                        }
                    }
                })
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setupPieChart(categories: List<String>, values: List<Float>, colors: List<String>) {
        val entries: ArrayList<PieEntry> = ArrayList()

        // Agrega los datos al conjunto de entradas
        for (i in categories.indices) {
            entries.add(PieEntry(values[i], categories[i]))
        }

        // Configura el conjunto de datos
        val dataSet = PieDataSet(entries, "Eventos por Categoría")

        // Convierte el Array de colores a una lista antes de configurarlos
        val pieColors = colors.map { Color.parseColor(it) }

        // Configura los colores del conjunto de datos
        dataSet.colors = pieColors

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
