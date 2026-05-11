package movil.proyecto_moviles.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import movil.proyecto_moviles.ui.theme.Proyecto_movilesTheme

@Composable
fun PantallaBienvenida (
    modifier: Modifier = Modifier,
    onCrearCuentaClick: () -> Unit
){

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A6BEA))
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Card (
                modifier = Modifier.size(82.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🚲",
                        fontSize = 28.sp
                    )

                }

            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Movired Lima",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tu guía de ciclovías para bicicletas y scooters en Lima",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureCard(
                    title = "Ciclovías compartidas",
                    subtitle = "Para bicis y scooters eléctricos"
                )
                FeatureCard(
                    title = "Estacionamientos",
                    subtitle = "Disponibilidad en tiempo real"
                )
                FeatureCard(
                    title = "Planifica tu ruta",
                    subtitle = "Llega de forma segura a tu destino"
                )
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onCrearCuentaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0A6BEA)

                    )
                ) {
                    Text(
                        text = "Crear Cuenta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                TextButton(
                    onClick = {}
                ) {
                    Text(
                        text = "Ya tengo cuenta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.18f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaBienvenidaPreview() {
    Proyecto_movilesTheme {
        PantallaBienvenida(onCrearCuentaClick = {})
    }
}