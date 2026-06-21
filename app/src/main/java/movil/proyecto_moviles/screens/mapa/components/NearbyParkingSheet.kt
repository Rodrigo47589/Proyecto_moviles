package movil.proyecto_moviles.screens.mapa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import movil.proyecto_moviles.screens.mapa.ParkingUi

@Composable
fun NearbyParkingSheet(
    parkings: List<ParkingUi>,
    onParkingClick: (ParkingUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFD1D5DB), RoundedCornerShape(50))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Estacionamientos cercanos",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Icon(
                    imageVector = Icons.Default.ExpandLess,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn {
                items(parkings, key = { it.id }) { parking ->
                    ParkingCard(
                        parking = parking,
                        onClick = { onParkingClick(parking) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}