package com.example.lab12

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Importar Color de Compose
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon // Importar Polygon
import com.google.maps.android.compose.Polyline // Importar Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

// Data class para organizar la información de los marcadores
data class MapLocation(
    val coordinates: LatLng,
    val title: String,
    val snippet: String,
    @DrawableRes val iconRes: Int
)

@Composable
fun MapScreen() {
    // 1. Ubicación inicial y estado de la cámara
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    // 2. Lista de marcadores personalizados
    val locations = listOf(
        MapLocation(
            coordinates = LatLng(-16.433415, -71.5442652),
            title = "JLByR",
            snippet = "Punto de interés en JLByR",
            iconRes = R.drawable.strategy_92
        ),
        MapLocation(
            coordinates = LatLng(-16.4205151, -71.4945209),
            title = "Paucarpata",
            snippet = "Punto de interés en Paucarpata",
            iconRes = R.drawable.golf_course_92
        ),
        MapLocation(
            coordinates = LatLng(-16.3524187, -71.5675994),
            title = "Zamacola",
            snippet = "Punto de interés en Zamacola",
            iconRes = R.drawable.mountain_flag_92
        )
    )

    // 3. Puntos para los Polígonos (¡UBICACIONES CORREGIDAS!)

    // --- (CORREGIDO) ---
    // Polígono aproximado para Mall Aventura (Av. Porongoche)
    val mallAventuraPolygon = listOf(
        LatLng(-16.4170, -71.5150),
        LatLng(-16.4170, -71.5140),
        LatLng(-16.4180, -71.5140),
        LatLng(-16.4180, -71.5150)
    )

    // --- (CORREGIDO) ---
    // Polígono aproximado para Parque Lambramani (Av. Lambramani)
    val parqueLambramaniPolygon = listOf(
        LatLng(-16.4100, -71.5210),
        LatLng(-16.4100, -71.5200),
        LatLng(-16.4110, -71.5200),
        LatLng(-16.4110, -71.5210)
    )

    // Polígono de la Plaza de Armas (Este estaba bien)
    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

    // 4. Puntos para la Polilínea (ruta)
    val rutaSimple = listOf(
        LatLng(-16.398866, -71.536961), // Plaza de Armas
        LatLng(-16.4105, -71.5205),    // Centro de Lambramani
        LatLng(-16.4173, -71.5142)     // Centro de Mall Aventura
    )


    // 5. Animación de la cámara al iniciar
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f), // Mover a Yura
            durationMs = 3000
        )
    }

    // 6. Contenedor del mapa
    Box(modifier = Modifier.fillMaxSize()) {

        // 7. El Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {

            // --- DIBUJAR MARCADORES ---

            // Marcador original de Arequipa
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.volcano_72),
                title = "Arequipa, Perú"
            )
            // Marcadores de la lista
            locations.forEach { locationData ->
                Marker(
                    state = rememberMarkerState(position = locationData.coordinates),
                    title = locationData.title,
                    snippet = locationData.snippet,
                    icon = BitmapDescriptorFactory.fromResource(locationData.iconRes)
                )
            }

            // --- DIBUJAR POLÍGONOS ---
            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f), // Color con 30% opacidad
                strokeWidth = 5f
            )
            Polygon(
                points = parqueLambramaniPolygon, // <-- Corregido
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )
            Polygon(
                points = mallAventuraPolygon, // <-- Corregido
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )

            // --- DIBUJAR POLILÍNEA ---
            Polyline(
                points = rutaSimple, // <-- Ruta actualizada
                color = Color.Green, // Color de la línea
                width = 10f          // Ancho de la línea
            )
        }
    }
}