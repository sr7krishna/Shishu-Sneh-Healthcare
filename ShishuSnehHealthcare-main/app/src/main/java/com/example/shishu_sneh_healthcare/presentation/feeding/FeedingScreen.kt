package com.example.shishu_sneh_healthcare.presentation.feeding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.FeedingLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: FeedingViewModel = hiltViewModel()
) {

    val feedingLogs by viewModel.feedingLogs.collectAsState()
    val babyDob by viewModel.babyDob.collectAsState()

    var showDialog by remember {
        mutableStateOf(false)
    }

    var selectedType by remember {
        mutableStateOf("Breastfeed")
    }

    var duration by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var foodItem by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = babyId) {
        viewModel.loadFeedingLogs(babyId)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Feeding Log",
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
                    showDialog = true
                }
            ) {

                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Feeding Log"
                )
            }
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)

        ) {

            Text(
                text = "Recent Activities",

                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            SmartFeedingGuidanceCard(
                feedingLogs = feedingLogs,
                babyDob = babyDob
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------------------------------------------
            // Empty State
            // ---------------------------------------------------

            if (feedingLogs.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🍼",
                            fontSize = 56.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No Feeding Logs Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text =
                                "Track feeding schedules, nutrition and meal patterns for your baby.",
                            lineHeight = 20.sp
                        )
                    }
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(feedingLogs.reversed()) { log ->

                        FeedingLogItem(log = log)
                    }
                }
            }
        }

        // ---------------------------------------------------
        // Add Feeding Dialog
        // ---------------------------------------------------

        if (showDialog) {

            AlertDialog(

                onDismissRequest = {
                    showDialog = false
                },

                title = {
                    Text("Add Feeding Log")
                },

                text = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // ---------------------------------------------------
                        // Feeding Type
                        // ---------------------------------------------------

                        Text(
                            text = "Feeding Type",
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            FilterChip(
                                selected = selectedType == "Breastfeed",
                                onClick = {
                                    selectedType = "Breastfeed"
                                },
                                label = {
                                    Text("Breastfeed")
                                }
                            )

                            FilterChip(
                                selected = selectedType == "Formula",
                                onClick = {
                                    selectedType = "Formula"
                                },
                                label = {
                                    Text("Formula")
                                }
                            )

                            FilterChip(
                                selected = selectedType == "Solid",
                                onClick = {
                                    selectedType = "Solid"
                                },
                                label = {
                                    Text("Solid")
                                }
                            )
                        }

                        // ---------------------------------------------------
                        // Duration
                        // ---------------------------------------------------

                        OutlinedTextField(
                            value = duration,
                            onValueChange = {
                                duration = it
                            },
                            label = {
                                Text("Duration (minutes)")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )

                        // ---------------------------------------------------
                        // Amount
                        // ---------------------------------------------------

                        OutlinedTextField(
                            value = amount,
                            onValueChange = {
                                amount = it
                            },
                            label = {
                                Text("Amount (ml)")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        // ---------------------------------------------------
                        // Food Item
                        // ---------------------------------------------------

                        OutlinedTextField(
                            value = foodItem,
                            onValueChange = {
                                foodItem = it
                            },
                            label = {
                                Text("Food Item (optional)")
                            }
                        )

                        // ---------------------------------------------------
                        // Notes
                        // ---------------------------------------------------

                        OutlinedTextField(
                            value = notes,
                            onValueChange = {
                                notes = it
                            },
                            label = {
                                Text("Notes")
                            }
                        )
                    }
                },

                confirmButton = {

                    Button(

                        onClick = {

                            val log = FeedingLogEntity(

                                babyId = babyId,

                                type = selectedType,

                                startTime = System.currentTimeMillis(),

                                duration =
                                    duration.toIntOrNull() ?: 0,

                                amount =
                                    amount.toDoubleOrNull() ?: 0.0,

                                foodItem =
                                    if (foodItem.isBlank())
                                        null
                                    else
                                        foodItem,

                                notes =
                                    if (notes.isBlank())
                                        null
                                    else
                                        notes
                            )

                            viewModel.addFeedingLog(log)

                            duration = ""
                            amount = ""
                            foodItem = ""
                            notes = ""
                            selectedType = "Breastfeed"

                            showDialog = false
                        }

                    ) {

                        Text("Save")
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {

                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FeedingLogItem(
    log: FeedingLogEntity
) {

    val timeFormat =
        SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        )

    val startTime =
        timeFormat.format(Date(log.startTime))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(

            modifier = Modifier.padding(16.dp),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween

        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = log.type,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Started at: $startTime",
                    fontSize = 14.sp
                )

                if (log.amount > 0) {

                    Text(
                        text = "Amount: ${log.amount} ml",
                        fontSize = 14.sp
                    )
                }

                if (!log.foodItem.isNullOrBlank()) {

                    Text(
                        text = "Food: ${log.foodItem}",
                        fontSize = 14.sp
                    )
                }

                if (!log.notes.isNullOrBlank()) {

                    Text(
                        text = "Notes: ${log.notes}",
                        fontSize = 14.sp
                    )
                }
            }

            if (log.duration > 0) {

                Text(
                    text = "${log.duration} min",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SmartFeedingGuidanceCard(

    feedingLogs: List<FeedingLogEntity>,
    babyDob: Long

) {

    val ageMonths = remember(babyDob) {
        if (babyDob == 0L) 0
        else ((System.currentTimeMillis() - babyDob) / (1000L * 60 * 60 * 24 * 30)).toInt()
    }

    val latestType =

        feedingLogs.lastOrNull()?.type
            ?: "Breastfeed"

    val guidance = when {
        ageMonths < 6 -> {
            "Exclusive breastfeeding or formula is recommended for infants under 6 months."
        }
        ageMonths < 12 -> {
            "Introduce mashed solids while continuing breast milk or formula. Focus on iron-rich foods."
        }
        else -> {
            "Focus on a balanced diet with 3 meals and 2 healthy snacks. Encourage independent eating."
        }
    }

    val foodSuggestions = when {
        ageMonths < 6 -> {
            listOf("Breast milk", "Formula milk", "Frequent hydration")
        }
        ageMonths < 9 -> {
            listOf("Rice porridge", "Mashed banana", "Steamed apple puree", "Mashed vegetables")
        }
        ageMonths < 12 -> {
            listOf("Soft cooked pulses", "Scrambled eggs", "Finger foods", "Yogurt")
        }
        else -> {
            listOf("Family meals (low salt)", "Cow's milk (if advised)", "Variety of fruits", "Chopped meat/proteins")
        }
    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
                        .copy(alpha = 0.25f)
            )

    ) {

        Column(

            modifier =
                Modifier.padding(20.dp)

        ) {

            Text(

                text = "Smart Feeding Guidance",

                fontWeight =
                    FontWeight.Bold,

                fontSize = 20.sp
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(

                text = guidance,

                lineHeight = 22.sp
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(

                text = "Suggested Foods",

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            foodSuggestions.forEach { food ->

                Text(
                    text = "• $food"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(

                text =
                    "Recommended feeding interval: every 2–4 hours",

                fontSize = 13.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}