package movil.proyecto_moviles.screens.mapa.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import movil.proyecto_moviles.screens.mapa.ParkingUi

@Composable
fun MapAreaPlaceholder(
    selectedParking: ParkingUi?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Color(0xFFEFF4FB))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color(0xFFD9E1EC)
            val spacing = 42f

            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += spacing
            }

            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += spacing
            }

            val route1 = Path().apply {
                moveTo(size.width * 0.08f, size.height * 0.62f)
                cubicTo(
                    size.width * 0.20f, size.height * 0.35f,
                    size.width * 0.48f, size.height * 0.55f,
                    size.width * 0.78f, size.height * 0.58f
                )
            }

            val route2 = Path().apply {
                moveTo(size.width * 0.62f, size.height * 0.15f)
                cubicTo(
                    size.width * 0.75f, size.height * 0.30f,
                    size.width * 0.76f, size.height * 0.55f,
                    size.width * 0.70f, size.height * 0.95f
                )
            }

            val route3 = Path().apply {
                moveTo(size.width * 0.30f, size.height * 0.95f)
                cubicTo(
                    size.width * 0.42f, size.height * 0.82f,
                    size.width * 0.60f, size.height * 0.75f,
                    size.width * 0.86f, size.height * 0.86f
                )
            }

            listOf(route1, route2, route3).forEach {
                drawPath(
                    path = it,
                    color = Color(0xFF156FE6),
                    style = Stroke(width = 9f, cap = StrokeCap.Round)
                )
            }
        }

        SearchMapBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp, start = 18.dp, end = 18.dp)
        )

        FloatingMapButtons(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .offset(y = 8.dp)
        )

        MapLegend(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 150.dp)
        )

        Text(
            text = "San Isidro",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 92.dp, top = 120.dp),
            color = Color(0xFF6B7280),
            fontSize = 13.sp
        )

        Text(
            text = "Barranco",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 72.dp)
                .offset(y = (-20).dp),
            color = Color(0xFF6B7280),
            fontSize = 13.sp
        )

        Text(
            text = "Miraflores",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 28.dp),
            color = Color(0xFF6B7280),
            fontSize = 13.sp
        )

        MarkerBlue(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 110.dp, y = 6.dp)
        )

        MarkerBlue(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 22.dp, y = 80.dp)
        )

        MarkerYellow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 28.dp, y = 170.dp)
        )

        if (selectedParking != null) {
            SelectedParkingBubble(
                parking = selectedParking,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = 12.dp, y = 150.dp)
            )
        }
    }
}

@Composable
private fun SearchMapBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F8F8),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF156FE6)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Miraflores, Lima",
                color = Color(0xFF111827),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FloatingMapButtons(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 5.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = Color(0xFF6B7280)
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(44.dp)
                .offset(y = 54.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 5.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = Color(0xFF156FE6)
                )
            }
        }
    }
}

@Composable
private fun MapLegend(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 5.dp
    ) {
        Box(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Leyenda\n\n━━  Ciclovía (Bicis + Scooters)\n\n●  Estacionamiento\n\n● ● ●  Disponibilidad",
                color = Color(0xFF4B5563),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MarkerBlue(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(36.dp),
        shape = CircleShape,
        color = Color(0xFF156FE6),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun MarkerYellow(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(40.dp),
        shape = CircleShape,
        color = Color(0xFFF0B429),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = null,
                tint = Color(0xFF111827),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SelectedParkingBubble(
    parking: ParkingUi,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        shadowElevation = 5.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(
                text = "${parking.name}\n${parking.totalFreeSpaces} espacios libres",
                color = Color(0xFF111827),
                fontSize = 11.sp
            )
        }
    }
}