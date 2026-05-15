package com.example.shishu_sneh_healthcare.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.HealthRecordEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(

    babyId: Long,

    onBackClick: () -> Unit,

    viewModel: HealthRecordsViewModel = hiltViewModel()

) {

    val healthRecords by
    viewModel.healthRecords.collectAsState()

    val isLoading by
    viewModel.isLoading.collectAsState()

    val error by
    viewModel.error.collectAsState()

    var showAddDialog by remember {

        mutableStateOf(false)
    }

    LaunchedEffect(babyId) {

        viewModel.loadHealthRecords(
            babyId
        )
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        "Health Records",
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
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {

                    showAddDialog = true
                }

            ) {

                Icon(

                    Icons.Default.Add,

                    contentDescription = "Add Record"
                )
            }
        }

    ) { padding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )

        ) {

            when {

                isLoading -> {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }

                error != null -> {

                    Text(

                        text =
                            error ?: "Unknown Error",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                else -> {

                    LazyColumn(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)

                    ) {

                        item {

                            Text(

                                text = "Medical History",

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }

                        if (healthRecords.isEmpty()) {

                            item {

                                EmptyHealthState()
                            }
                        }

                        items(healthRecords) { record ->

                            HealthRecordItem(
                                record
                            )
                        }
                    }
                }
            }

            // ---------------------------------------------------
            // ADD RECORD DIALOG
            // ---------------------------------------------------

            if (showAddDialog) {

                var type by remember {

                    mutableStateOf("")
                }

                var doctorName by remember {

                    mutableStateOf("")
                }

                var clinic by remember {

                    mutableStateOf("")
                }

                var notes by remember {

                    mutableStateOf("")
                }

                AlertDialog(

                    onDismissRequest = {

                        showAddDialog = false
                    },

                    title = {

                        Text(
                            "Add Health Record"
                        )
                    },

                    text = {

                        Column(

                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)

                        ) {

                            OutlinedTextField(

                                value = type,

                                onValueChange = {

                                    type = it
                                },

                                label = {

                                    Text("Record Type")
                                },

                                placeholder = {

                                    Text(
                                        "Doctor Visit / Fever / Prescription"
                                    )
                                }
                            )

                            OutlinedTextField(

                                value = doctorName,

                                onValueChange = {

                                    doctorName = it
                                },

                                label = {

                                    Text("Doctor Name")
                                }
                            )

                            OutlinedTextField(

                                value = clinic,

                                onValueChange = {

                                    clinic = it
                                },

                                label = {

                                    Text("Clinic / Hospital")
                                }
                            )

                            OutlinedTextField(

                                value = notes,

                                onValueChange = {

                                    notes = it
                                },

                                label = {

                                    Text("Notes")
                                },

                                minLines = 3
                            )
                        }
                    },

                    confirmButton = {

                        Button(

                            onClick = {

                                viewModel.addHealthRecord(

                                    HealthRecordEntity(

                                        babyId = babyId,

                                        type = type,

                                        date =
                                            System.currentTimeMillis(),

                                        doctorName =
                                            doctorName,

                                        clinic = clinic,

                                        notes = notes,

                                        attachmentUri = null
                                    )
                                )

                                showAddDialog = false
                            },

                            enabled =
                                type.isNotBlank()

                        ) {

                            Text("Save")
                        }
                    },

                    dismissButton = {

                        TextButton(

                            onClick = {

                                showAddDialog = false
                            }

                        ) {

                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HealthRecordItem(

    record: HealthRecordEntity

) {

    val dateFormat = remember {

        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )
    }

    val dateString = dateFormat.format(
        Date(record.date)
    )

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
            )

    ) {

        Row(

            modifier =
                Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Surface(

                color =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
                        .copy(alpha = 0.3f),

                shape =
                    RoundedCornerShape(12.dp),

                modifier =
                    Modifier.size(50.dp)

            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.LocalHospital,

                        contentDescription = null,

                        tint =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = record.type,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 18.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(

                    text =
                        "${record.doctorName ?: "General"} • $dateString",

                    fontSize = 14.sp,

                    color = Color.Gray
                )

                if (!record.clinic.isNullOrBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(

                        text =
                            record.clinic ?: "",

                        fontSize = 13.sp,

                        color = Color.Gray
                    )
                }

                if (!record.notes.isNullOrBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(

                        text =
                            record.notes ?: "",

                        fontSize = 13.sp,

                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHealthState() {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Text(
            text = "🏥",
            fontSize = 64.sp
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(

            text =
                "No health records yet",

            fontWeight =
                FontWeight.Bold,

            fontSize = 20.sp
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(

            text =
                "Track doctor visits, illnesses, prescriptions and pediatric notes.",

            fontSize = 14.sp,

            color = Color.Gray
        )
    }
}