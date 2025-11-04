package com.example.lab12

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


// 1. Define una data class para agrupar la información
data class MapLocation(
    val coordinates: LatLng,
    val title: String,
    val snippet: String,
    @DrawableRes val iconRes: Int // El ID del recurso drawable
)

@Composable
fun MapScreen() {
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    // --- (NUEVO) ---
    // 2. Crea tu lista usando la data class
    val locations = listOf(
        MapLocation(
            coordinates = LatLng(-16.433415, -71.5442652),
            title = "JLByR",
            snippet = "Punto de interés en JLByR",
            iconRes = R.drawable.strategy_92 // <-- Icono para JLByR
        ),
        MapLocation(
            coordinates = LatLng(-16.4205151, -71.4945209),
            title = "Paucarpata",
            snippet = "Punto de interés en Paucarpata",
            iconRes = R.drawable.golf_course_92 // <-- Icono para Paucarpata
        ),
        MapLocation(
            coordinates = LatLng(-16.3524187, -71.5675994),
            title = "Zamacola",
            snippet = "Punto de interés en Zamacola",
            iconRes = R.drawable.mountain_flag_92 // <-- Icono para Zamacola
        )
    )


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // 1. Tu marcador original de Arequipa
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.volcano_72),
                title = "Arequipa, Perú"
            )

            // --- (NUEVO) ---
            // 3. Itera sobre tu nueva lista y crea los marcadores
            locations.forEach { locationData ->
                Marker(
                    state = rememberMarkerState(position = locationData.coordinates),
                    title = locationData.title,
                    snippet = locationData.snippet,
                    icon = BitmapDescriptorFactory.fromResource(locationData.iconRes) // <-- Asigna el icono específico
                )
            }
        }
    }
}