package com.example.lab12

// --- IMPORTS PARA PLACES, SCAFFOLD Y SNACKBAR ---
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place.Field as PlaceField // <-- ¡ESTE ES EL IMPORT!

import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
// --- FIN NUEVOS IMPORTS ---

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

data class MapLocation(
    val coordinates: LatLng,
    val title: String,
    val snippet: String,
    @DrawableRes val iconRes: Int
)

@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {

    // --- 1. ESTADO PARA EL TIPO DE MAPA ---
    var currentMapType by remember { mutableStateOf(MapType.NORMAL) }

    // --- 2. LÓGICA DE PERMISOS ---
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )
    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // --- 3. PROPIEDADES Y UI SETTINGS ---
    val mapProperties = remember(currentMapType, hasLocationPermission) {
        MapProperties(
            mapType = currentMapType,
            isMyLocationEnabled = hasLocationPermission
        )
    }
    val uiSettings = remember {
        MapUiSettings(
            myLocationButtonEnabled = true
        )
    }

    // --- 4. ESTADOS PARA PLACES Y SNACKBAR ---
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val placesClient = remember {
        Places.createClient(context)
    }

    // (Definiciones de ubicaciones, polígonos, etc.)
    val ArequipaLocation = LatLng(-16.4040102, -71.559611)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }
    val locations = listOf(
        MapLocation(LatLng(-16.433415, -71.5442652), "JLByR", "Punto de interés en JLByR", R.drawable.strategy_92),
        MapLocation(LatLng(-16.4205151, -71.4945209), "Paucarpata", "Punto de interés en Paucarpata", R.drawable.golf_course_92),
        MapLocation(LatLng(-16.3524187, -71.5675994), "Zamacola", "Punto de interés en Zamacola", R.drawable.mountain_flag_92)
    )
    val mallAventuraPolygon = listOf(LatLng(-16.4170, -71.5150), LatLng(-16.4170, -71.5140), LatLng(-16.4180, -71.5140), LatLng(-16.4180, -71.5150))
    val parqueLambramaniPolygon = listOf(LatLng(-16.4100, -71.5210), LatLng(-16.4100, -71.5200), LatLng(-16.4110, -71.5200), LatLng(-16.4110, -71.5210))
    val plazaDeArmasPolygon = listOf(LatLng(-16.398866, -71.536961), LatLng(-16.398744, -71.536529), LatLng(-16.399178, -71.536289), LatLng(-16.399299, -71.536721))
    val rutaSimple = listOf(LatLng(-16.398866, -71.536961), LatLng(-16.4105, -71.5205), LatLng(-16.4173, -71.5142))

    // Animación de cámara
    LaunchedEffect(key1 = cameraPositionState) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f),
            durationMs = 3000
        )
    }

    // --- 5. LAYOUT CON SCAFFOLD ---
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    findCurrentPlace(
                        placesClient = placesClient,
                        hasPermission = hasLocationPermission,
                        onSuccess = { placeName ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Estás cerca de: $placeName")
                            }
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: ${error.message}")
                            }
                        }
                    )
                }
            ) {
                Icon(Icons.Default.Place, contentDescription = "Buscar lugar actual")
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings
            ) {

                // (Marcadores, Polígonos y Polilíneas)
                Marker(state = rememberMarkerState(position = ArequipaLocation), icon = BitmapDescriptorFactory.fromResource(R.drawable.volcano_72), title = "Arequipa, Perú")
                locations.forEach { locationData ->
                    Marker(state = rememberMarkerState(position = locationData.coordinates), title = locationData.title, snippet = locationData.snippet, icon = BitmapDescriptorFactory.fromResource(locationData.iconRes))
                }
                Polygon(points = plazaDeArmasPolygon, strokeColor = Color.Red, fillColor = Color.Blue.copy(alpha = 0.3f), strokeWidth = 5f)
                Polygon(points = parqueLambramaniPolygon, strokeColor = Color.Red, fillColor = Color.Blue.copy(alpha = 0.3f), strokeWidth = 5f)
                Polygon(points = mallAventuraPolygon, strokeColor = Color.Red, fillColor = Color.Blue.copy(alpha = 0.3f), strokeWidth = 5f)
                Polyline(points = rutaSimple, color = Color.Green, width = 10f)
            }

            // --- Botones de tipo de mapa ---
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { currentMapType = MapType.NORMAL }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp), contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Normal") }
                Button(onClick = { currentMapType = MapType.HYBRID }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp), contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Híbrido") }
                Button(onClick = { currentMapType = MapType.TERRAIN }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp), contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Terreno") }
                Button(onClick = { currentMapType = MapType.SATELLITE }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp), contentPadding = PaddingValues(horizontal = 4.dp)) { Text("Satélite") }
            }
        }
    }
}

// --- 6. FUNCIÓN AUXILIAR PARA LLAMAR A LA API DE PLACES ---
@SuppressLint("MissingPermission")
private fun findCurrentPlace(
    placesClient: PlacesClient,
    hasPermission: Boolean,
    onSuccess: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    if (!hasPermission) {
        onError(Exception("Permiso de ubicación denegado"))
        return
    }

    val placeFields = listOf(PlaceField.NAME, PlaceField.ADDRESS)
    val request = FindCurrentPlaceRequest.newInstance(placeFields)

    placesClient.findCurrentPlace(request)
        .addOnSuccessListener { response ->
            val placeName = response.placeLikelihoods.firstOrNull()?.place?.name
            if (placeName != null) {
                onSuccess(placeName)
            } else {
                onError(Exception("No se encontraron lugares cercanos"))
            }
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}