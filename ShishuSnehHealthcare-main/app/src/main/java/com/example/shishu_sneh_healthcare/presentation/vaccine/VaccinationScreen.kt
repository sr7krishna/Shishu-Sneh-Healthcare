package com.example.shishu_sneh_healthcare.presentation.vaccine

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: VaccinationViewModel = hiltViewModel()
) {

    val vaccines by viewModel.vaccines.collectAsState()

    LaunchedEffect(babyId) {
        viewModel.loadVaccines(babyId)
    }

    val sortedVaccines = vaccines.sortedBy {

        when (

            getVaccineStatus(it)

        ) {

            "Overdue" -> 0

            "Due Today" -> 1

            "Due Soon" -> 2

            "Upcoming" -> 3

            else -> 4
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Immunization Calendar",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    )
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)

        ) {

            Text(
                text = "Track your baby's protection",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(16.dp),

                contentPadding =
                    PaddingValues(bottom = 24.dp)

            ) {

                items(sortedVaccines) { vaccine ->

                    VaccineCard(

                        vaccine = vaccine,

                        onMarkDone = {

                            viewModel.updateVaccineStatus(it)
                        }
                    )
                }

                if (vaccines.isEmpty()) {

                    item {

                        EmptyVaccineState()
                    }
                }
            }
        }
    }
}

@Composable
fun VaccineCard(

    vaccine: VaccineEntity,

    onMarkDone: (
        VaccineEntity
    ) -> Unit

) {

    val dateFormat =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

    val dueDate =
        dateFormat.format(
            Date(vaccine.scheduledDate)
        )

    val status =
        getVaccineStatus(vaccine)

    val statusColor =
        getStatusColor(status)

    val icon =
        getStatusIcon(status)

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {

        Row(

            modifier = Modifier.padding(20.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Surface(

                color =
                    statusColor.copy(alpha = 0.1f),

                shape =
                    RoundedCornerShape(16.dp),

                modifier =
                    Modifier.size(56.dp)

            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector = icon,

                        contentDescription = null,

                        tint = statusColor,

                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = vaccine.name,

                    fontWeight = FontWeight.ExtraBold,

                    fontSize = 18.sp
                )

                Text(

                    text = vaccine.disease,

                    fontSize = 13.sp,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(

                    text = "Due: $dueDate",

                    fontSize = 14.sp,

                    fontWeight = FontWeight.Medium
                )

                if (status != "Completed") {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(

                        onClick = {

                            onMarkDone(

                                vaccine.copy(
                                    status = "Done"
                                )
                            )
                        },

                        shape =
                            RoundedCornerShape(12.dp)

                    ) {

                        Icon(

                            Icons.Default.CheckCircle,

                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Mark Done")
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            StatusChip(

                status = status,

                color = statusColor
            )
        }
    }
}
@Composable
fun StatusChip(
    status: String,
    color: Color
) {

    Surface(

        color = color.copy(alpha = 0.1f),

        shape = RoundedCornerShape(8.dp),

        border = BorderStroke(
            1.dp,
            color.copy(alpha = 0.5f)
        )

    ) {

        Text(

            text = status,

            color = color,

            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),

            fontSize = 11.sp,

            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyVaccineState() {

    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),

        contentAlignment = Alignment.Center

    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "💉",
                fontSize = 60.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text =
                    "No vaccines scheduled yet",

                fontWeight = FontWeight.Bold,

                color = Color.Gray
            )
        }
    }
}

// ---------------------------------------------------
// Vaccine Status Logic
// ---------------------------------------------------

fun getVaccineStatus(

    vaccine: VaccineEntity

): String {

    if (

        vaccine.status.equals(
            "Done",
            ignoreCase = true
        )

    ) {

        return "Completed"
    }

    val currentTime =
        System.currentTimeMillis()

    val oneDay =
        24L * 60 * 60 * 1000

    val sevenDays =
        7L * oneDay

    val difference =
        vaccine.scheduledDate - currentTime

    return when {

        // ---------------------------------------------------
        // DUE TODAY
        // ---------------------------------------------------

        kotlin.math.abs(difference) < oneDay -> {

            "Due Today"
        }

        // ---------------------------------------------------
        // OVERDUE
        // ---------------------------------------------------

        difference < 0 -> {

            "Overdue"
        }

        // ---------------------------------------------------
        // DUE SOON
        // ---------------------------------------------------

        difference <= sevenDays -> {

            "Due Soon"
        }

        // ---------------------------------------------------
        // UPCOMING
        // ---------------------------------------------------

        else -> {

            "Upcoming"
        }
    }
}
fun getStatusColor(

    status: String

): Color {

    return when (status) {

        "Completed" ->
            Color(0xFF4CAF50)

        "Overdue" ->
            Color(0xFFF44336)

        "Due Today" ->
            Color(0xFFE91E63)

        "Due Soon" ->
            Color(0xFFFF9800)

        else ->
            Color(0xFF2196F3)
    }
}

// ---------------------------------------------------
// Status Icon
// ---------------------------------------------------

fun getStatusIcon(

    status: String

): ImageVector {

    return when (status) {

        "Completed" ->
            Icons.Default.CheckCircle

        "Overdue" ->
            Icons.Default.Error

        "Due Today" ->
            Icons.Default.Error

        else ->
            Icons.Default.Schedule
    }
}