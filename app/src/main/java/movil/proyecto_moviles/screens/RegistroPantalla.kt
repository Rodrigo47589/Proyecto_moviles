package movil.proyecto_moviles.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import movil.proyecto_moviles.ui.theme.Proyecto_movilesTheme
import androidx.compose.foundation.layout.size

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onRegistroCompletado: () -> Unit,
    onVolverInicio: () -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(1) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var vehiculoSeleccionado by rememberSaveable { mutableStateOf("") }
    var terminosAceptados by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF0F4F8), RoundedCornerShape(14.dp))
                        .clickable {
                            if (step > 1) {
                                step--
                            } else {
                                onVolverInicio()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                StepIndicator(step = step)
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> {
                    Text(
                        text = "Crea tu cuenta",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingresa tus datos personales para comenzar",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField(
                        label = "Nombre completo",
                        placeholder = "Tu nombre",
                        icon = Icons.Default.Person,
                        value = nombre,
                        onValueChange = { nombre = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        label = "Correo electrónico",
                        placeholder = "Tu@email.com",
                        icon = Icons.Default.Email,
                        value = correo,
                        onValueChange = { correo = it },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        label = "Teléfono",
                        placeholder = "+51 999 999 999",
                        icon = Icons.Default.Phone,
                        value = telefono,
                        onValueChange = { telefono = it },
                        keyboardType = KeyboardType.Phone
                    )
                }

                2 -> {
                    Text(
                        text = "Crea tu contraseña",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Usa al menos 8 caracteres con letras y números",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField(
                        label = "Contraseña",
                        placeholder = "Tu contraseña",
                        icon = Icons.Default.Lock,
                        value = password,
                        onValueChange = { password = it },
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        label = "Confirmar contraseña",
                        placeholder = "Tu contraseña",
                        icon = Icons.Default.Lock,
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tu contraseña debe tener:",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "- Al menos 8 caracteres", color = Color.Gray, fontSize = 12.sp)
                            Text(text = "- Una letra mayúscula", color = Color.Gray, fontSize = 12.sp)
                            Text(text = "- Un número", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                3 -> {
                    Text(
                        text = "Tu vehículo",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selecciona qué tipo de vehículo usas para movilizarte",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    VehicleSelectionCard(
                        titulo = "Bicicleta",
                        subtitulo = "Uso principalmente bicicleta",
                        icon = Icons.Default.DirectionsBike,
                        isSelected = vehiculoSeleccionado == "Bicicleta",
                        onClick = { vehiculoSeleccionado = "Bicicleta" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VehicleSelectionCard(
                        titulo = "Scooter eléctrico",
                        subtitulo = "Uso scooter o patineta eléctrica",
                        icon = Icons.Default.ElectricScooter,
                        isSelected = vehiculoSeleccionado == "Scooter eléctrico",
                        onClick = { vehiculoSeleccionado = "Scooter eléctrico" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VehicleSelectionCard(
                        titulo = "Ambos",
                        subtitulo = "Uso bicicleta y scooter",
                        icon = Icons.Default.DirectionsBike,
                        isSelected = vehiculoSeleccionado == "Ambos",
                        onClick = { vehiculoSeleccionado = "Ambos" }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TermsCheckbox(
                        isChecked = terminosAceptados,
                        onCheckedChange = { terminosAceptados = it }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (step < 3) {
                    step++
                } else {
                    onRegistroCompletado()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A6BEA),
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (step < 3) "Continuar" else "Finalizar",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StepIndicator(step: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(4.dp)
                    .background(
                        color = if (index < step) Color(0xFF0A6BEA) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    placeholder: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            fontSize = 14.sp
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F4F8),
                unfocusedContainerColor = Color(0xFFF0F4F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF0A6BEA)
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Composable
fun VehicleSelectionCard(
    titulo: String,
    subtitulo: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE8F0FE) else Color.White
    val borderColor = if (isSelected) Color(0xFF0A6BEA) else Color(0xFFE0E0E0)

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Black)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = subtitulo, color = Color.Gray, fontSize = 12.sp)
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = Color(0xFF0A6BEA)
                )
            }
        }
    }
}

@Composable
fun TermsCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isChecked) Color(0xFF0A6BEA) else Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Acepto los Términos y Condiciones y la Política de Privacidad",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroScreenPreview() {
    Proyecto_movilesTheme {
        RegistroScreen(
            onRegistroCompletado = {},
            onVolverInicio = {}
        )
    }
}