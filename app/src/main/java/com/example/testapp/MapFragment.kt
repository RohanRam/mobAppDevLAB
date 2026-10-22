package com.example.testapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapFragment : Fragment(R.layout.fragment_map) {

    private lateinit var mapWebView: WebView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                fetchLocationAndLoadMap()
            } else {
                loadMap(37.7749, -122.4194) // Default fallback location
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapWebView = view.findViewById(R.id.mapWebView)
        mapWebView.settings.javaScriptEnabled = true
        mapWebView.webViewClient = WebViewClient()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkPermissionAndFetchLocation()
    }

    private fun checkPermissionAndFetchLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocationAndLoadMap()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchLocationAndLoadMap() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    loadMap(location.latitude, location.longitude)
                } else {
                    loadMap(37.7749, -122.4194)
                }
            }.addOnFailureListener {
                loadMap(37.7749, -122.4194)
            }
        } catch (e: SecurityException) {
            loadMap(37.7749, -122.4194)
        }
    }

    private fun loadMap(lat: Double, lng: Double) {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    html, body, iframe { width: 100%; height: 100%; margin: 0; padding: 0; border: none; }
                </style>
            </head>
            <body>
                <iframe src="https://maps.google.com/maps?q=$lat,$lng&z=15&output=embed" width="100%" height="100%" style="border:0;" allowfullscreen="" loading="lazy"></iframe>
            </body>
            </html>
        """.trimIndent()
        mapWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
}
