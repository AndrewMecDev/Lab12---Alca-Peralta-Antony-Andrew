package com.example.lab12

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.lab12.ui.theme.Lab12Theme // Asegúrate de que esta sea la ruta a tu tema
import com.google.android.libraries.places.api.Places // <-- 1. IMPORTAR PLACES

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 2. (NUEVO) INICIALIZAR PLACES SDK ---
        // Lee la API Key desde el AndroidManifest.xml
        val appInfo: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")

        if (apiKey != null) {
            // Inicializa el SDK
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
        // --- (FIN DE LO NUEVO) ---

        setContent {
            // (Es buena práctica envolver tu pantalla en el tema)
            Lab12Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapScreen()
                }
            }
        }
    }
}
// (Ya no necesitas los @Composable 'Greeting' o 'GreetingPreview' de ejemplo)