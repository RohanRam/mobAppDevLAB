package com.example.testapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class CitySuggestion(val name: String, val admin: String, val country: String, val lat: Double, val lon: Double)

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private lateinit var etCitySearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var weatherResultCard: MaterialCardView
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvWeatherDesc: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    
    private lateinit var suggestionsCard: MaterialCardView
    private lateinit var rvSuggestions: RecyclerView
    private var searchJob: Job? = null
    private val suggestionsList = mutableListOf<CitySuggestion>()
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private var isSelectingSuggestion = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etCitySearch = view.findViewById(R.id.etCitySearch)
        btnSearch = view.findViewById(R.id.btnSearch)
        progressBar = view.findViewById(R.id.progressBar)
        weatherResultCard = view.findViewById(R.id.weatherResultCard)
        tvCityName = view.findViewById(R.id.tvCityName)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvWeatherDesc = view.findViewById(R.id.tvWeatherDesc)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        tvWindSpeed = view.findViewById(R.id.tvWindSpeed)
        suggestionsCard = view.findViewById(R.id.suggestionsCard)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)

        setupSuggestionsRecyclerView()

        btnSearch.setOnClickListener {
            performSearch()
        }

        etCitySearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                suggestionsCard.visibility = View.GONE
                performSearch()
                true
            } else {
                false
            }
        }
        
        etCitySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isSelectingSuggestion) return
                
                val query = s?.toString()?.trim() ?: ""
                searchJob?.cancel()
                if (query.length < 2) {
                    suggestionsCard.visibility = View.GONE
                    return
                }
                
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300) // debounce
                    fetchSuggestions(query)
                }
            }
        })
    }

    private fun setupSuggestionsRecyclerView() {
        suggestionsAdapter = SuggestionsAdapter(suggestionsList) { suggestion ->
            // On Click Suggestion
            isSelectingSuggestion = true
            etCitySearch.setText(suggestion.name)
            etCitySearch.setSelection(suggestion.name.length)
            isSelectingSuggestion = false
            
            suggestionsCard.visibility = View.GONE
            
            // clear the list to prevent re-opening on text change if the API returns quickly
            suggestionsList.clear()
            suggestionsAdapter.notifyDataSetChanged()
            
            hideKeyboard()
            fetchWeatherForCoordinates(suggestion.lat, suggestion.lon, suggestion.name)
        }
        rvSuggestions.layoutManager = LinearLayoutManager(requireContext())
        rvSuggestions.adapter = suggestionsAdapter
    }

    private fun fetchSuggestions(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${query.replace(" ", "+")}&count=5&language=en&format=json"
                val geoResponse = withContext(Dispatchers.IO) {
                    val connection = URL(geoUrl).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.inputStream.bufferedReader().use { it.readText() }
                }

                val geoJson = JSONObject(geoResponse)
                suggestionsList.clear()
                
                if (geoJson.has("results")) {
                    val resultsArray = geoJson.getJSONArray("results")
                    for (i in 0 until resultsArray.length()) {
                        val result = resultsArray.getJSONObject(i)
                        val name = result.optString("name", "")
                        val admin = result.optString("admin1", "")
                        val country = result.optString("country", "")
                        val lat = result.getDouble("latitude")
                        val lon = result.getDouble("longitude")
                        suggestionsList.add(CitySuggestion(name, admin, country, lat, lon))
                    }
                }
                
                suggestionsAdapter.notifyDataSetChanged()
                if (suggestionsList.isNotEmpty()) {
                    suggestionsCard.visibility = View.VISIBLE
                } else {
                    suggestionsCard.visibility = View.GONE
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                // Fail silently for suggestions
            }
        }
    }

    private fun performSearch() {
        val query = etCitySearch.text.toString().trim()
        if (query.isNotEmpty()) {
            hideKeyboard()
            fetchWeatherData(query)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etCitySearch.windowToken, 0)
    }

    private fun fetchWeatherData(cityName: String) {
        // Show loading, hide previous results
        progressBar.visibility = View.VISIBLE
        weatherResultCard.visibility = View.GONE
        weatherResultCard.alpha = 0f

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Geocode City Name
                val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${cityName.replace(" ", "+")}&count=1&language=en&format=json"
                val geoResponse = withContext(Dispatchers.IO) {
                    val connection = URL(geoUrl).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.inputStream.bufferedReader().use { it.readText() }
                }

                val geoJson = JSONObject(geoResponse)
                if (!geoJson.has("results")) {
                    showError("City not found")
                    return@launch
                }

                val result = geoJson.getJSONArray("results").getJSONObject(0)
                val lat = result.getDouble("latitude")
                val lon = result.getDouble("longitude")
                val resolvedCityName = result.getString("name")

                fetchWeatherForCoordinates(lat, lon, resolvedCityName, false)

            } catch (e: Exception) {
                e.printStackTrace()
                showError("Failed to load weather data")
            }
        }
    }
    
    private fun fetchWeatherForCoordinates(lat: Double, lon: Double, resolvedCityName: String, showLoader: Boolean = true) {
        if (showLoader) {
            progressBar.visibility = View.VISIBLE
            weatherResultCard.visibility = View.GONE
            weatherResultCard.alpha = 0f
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 2. Fetch Weather Data
                val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true&hourly=relative_humidity_2m"
                val weatherResponse = withContext(Dispatchers.IO) {
                    val connection = URL(weatherUrl).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.inputStream.bufferedReader().use { it.readText() }
                }

                val weatherJson = JSONObject(weatherResponse)
                val current = weatherJson.getJSONObject("current_weather")
                val temp = current.getDouble("temperature")
                val windSpeed = current.getDouble("windspeed")
                val weatherCode = current.getInt("weathercode")
                
                // Get humidity from hourly data (first element is closest to current)
                val hourly = weatherJson.getJSONObject("hourly")
                val humidityArray = hourly.getJSONArray("relative_humidity_2m")
                val humidity = if (humidityArray.length() > 0) humidityArray.getInt(0) else 0

                val weatherDesc = interpretWeatherCode(weatherCode)

                // Update UI
                tvCityName.text = resolvedCityName
                tvTemperature.text = "${temp}°C"
                tvWeatherDesc.text = weatherDesc
                tvHumidity.text = "$humidity%"
                tvWindSpeed.text = "$windSpeed km/h"

                progressBar.visibility = View.GONE
                weatherResultCard.visibility = View.VISIBLE
                weatherResultCard.animate().alpha(1f).setDuration(500).start()

            } catch (e: Exception) {
                e.printStackTrace()
                showError("Failed to load weather data")
            }
        }
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun interpretWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Mainly clear, partly cloudy, and overcast"
            45, 48 -> "Fog and depositing rime fog"
            51, 53, 55 -> "Drizzle: Light, moderate, and dense intensity"
            56, 57 -> "Freezing Drizzle: Light and dense intensity"
            61, 63, 65 -> "Rain: Slight, moderate and heavy intensity"
            66, 67 -> "Freezing Rain: Light and heavy intensity"
            71, 73, 75 -> "Snow fall: Slight, moderate, and heavy intensity"
            77 -> "Snow grains"
            80, 81, 82 -> "Rain showers: Slight, moderate, and violent"
            85, 86 -> "Snow showers slight and heavy"
            95 -> "Thunderstorm: Slight or moderate"
            96, 99 -> "Thunderstorm with slight and heavy hail"
            else -> "Unknown"
        }
    }
}

class SuggestionsAdapter(
    private val items: List<CitySuggestion>,
    private val onItemClick: (CitySuggestion) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCitySuggestionName)
        val tvDetails: TextView = view.findViewById(R.id.tvCitySuggestionDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        
        val detailsParts = listOf(item.admin, item.country).filter { it.isNotEmpty() }
        holder.tvDetails.text = detailsParts.joinToString(", ")
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size
}
