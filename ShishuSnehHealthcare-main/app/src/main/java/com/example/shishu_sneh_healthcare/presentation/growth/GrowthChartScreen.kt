package com.example.shishu_sneh_healthcare.presentation.growth

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.GrowthEntryEntity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthChartScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: GrowthViewModel = hiltViewModel()
) {

    val growthEntries by viewModel.growthEntries.collectAsState()
    val baby by viewModel.baby.collectAsState()

    var selectedChartType by remember { mutableStateOf(0) } // 0: Weight, 1: Height

    var showDialog by remember {
        mutableStateOf(false)
    }

    var weight by remember {
        mutableStateOf("")
    }

    var height by remember {
        mutableStateOf("")
    }

    var headCirc by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var selectedDate by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    LaunchedEffect(babyId) {
        viewModel.loadGrowthEntries(babyId)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Growth Analytics",
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
                    contentDescription = "Add Entry"
                )
            }
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)

        ) {

            // ---------------------------------------------------
            // Empty State
            // ---------------------------------------------------

            if (growthEntries.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No Growth Records Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap + to add growth data"
                        )
                    }
                }

            } else {

                // ---------------------------------------------------
                // Growth Chart
                // ---------------------------------------------------

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),

                    shape = RoundedCornerShape(24.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        TabRow(
                            selectedTabIndex = selectedChartType,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedChartType]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedChartType == 0,
                                onClick = { selectedChartType = 0 },
                                text = { Text("Weight (kg)") }
                            )
                            Tab(
                                selected = selectedChartType == 1,
                                onClick = { selectedChartType = 1 },
                                text = { Text("Height (cm)") }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val sortedEntries = growthEntries.sortedBy { it.date }

                        GrowthLineChart(
                            entries = sortedEntries.mapIndexed { index, entry ->
                                Entry(
                                    index.toFloat(),
                                    if (selectedChartType == 0) entry.weight.toFloat() else entry.height.toFloat()
                                )
                            },
                            label = if (selectedChartType == 0) "Weight" else "Height",
                            color = if (selectedChartType == 0) "#F48FB1" else "#64B5F6"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------------------------------------------
                // Stats
                // ---------------------------------------------------

                val sortedEntries = growthEntries.sortedBy { it.date }
                val latest = sortedEntries.last()
                val first = sortedEntries.first()

                val weightGain = latest.weight - first.weight

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    StatCard(
                        label = "Latest Weight",
                        value = "${latest.weight} kg",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        label = "Weight Gain",
                        value = "+${String.format(Locale.getDefault(), "%.1f", weightGain)} kg",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------------------------------------------
                // Records
                // ---------------------------------------------------

                Text(
                    text = "Growth Records",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(growthEntries.sortedByDescending { it.date }) { entry ->

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val formattedDate =
                                        SimpleDateFormat(
                                            "dd MMM yyyy",
                                            Locale.getDefault()
                                        ).format(Date(entry.date))

                                    Text(
                                        text = formattedDate,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )

                                    // Growth Status Badge
                                    baby?.let { b ->
                                        val ageMonths = calculateAgeInMonths(b.dob, entry.date)
                                        val (status, color) = getGrowthStatus(ageMonths, entry.weight, b.gender)
                                        
                                        Surface(
                                            color = color.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, color)
                                        ) {
                                            Text(
                                                text = status,
                                                color = color,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    GrowthStatItem("Weight", "${entry.weight} kg", Modifier.weight(1f))
                                    GrowthStatItem("Height", "${entry.height} cm", Modifier.weight(1f))
                                    GrowthStatItem("Head", "${entry.headCirc} cm", Modifier.weight(1f))
                                }

                                if (!entry.notes.isNullOrBlank()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                    Text(
                                        text = "Notes: ${entry.notes}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------
        // Add Entry Dialog
        // ---------------------------------------------------

        if (showDialog) {
            val context = LocalContext.current

            AlertDialog(

                onDismissRequest = {
                    showDialog = false
                },

                title = {
                    Text("Add Growth Entry")
                },

                text = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Date Selection Button
                        OutlinedCard(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = selectedDate
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val cal = Calendar.getInstance()
                                        cal.set(year, month, day)
                                        selectedDate = cal.timeInMillis
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))
                                )
                            }
                        }

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height (cm)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        OutlinedTextField(
                            value = headCirc,
                            onValueChange = { headCirc = it },
                            label = { Text("Head Circumference (cm)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },

                confirmButton = {

                    Button(

                        onClick = {

                            val entry = GrowthEntryEntity(
                                babyId = babyId,
                                date = selectedDate,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                height = height.toDoubleOrNull() ?: 0.0,
                                headCirc = headCirc.toDoubleOrNull() ?: 0.0,
                                notes = notes
                            )

                            viewModel.addGrowthEntry(entry)

                            weight = ""
                            height = ""
                            headCirc = ""
                            notes = ""
                            selectedDate = System.currentTimeMillis()

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
fun GrowthStatItem(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = label,
                fontSize = 12.sp
            )

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GrowthLineChart(
    entries: List<Entry>,
    label: String,
    color: String
) {

    AndroidView(

        modifier = Modifier.fillMaxSize(),

        factory = { context ->

            LineChart(context).apply {

                description.isEnabled = false

                setTouchEnabled(true)

                setPinchZoom(true)

                setScaleEnabled(true)

                setDrawGridBackground(false)

                xAxis.apply {

                    position = XAxis.XAxisPosition.BOTTOM

                    setDrawGridLines(false)

                    granularity = 1f
                }

                axisLeft.setDrawGridLines(true)

                axisRight.isEnabled = false

                legend.isEnabled = true
            }
        },

        update = { chart ->

            val dataSet = LineDataSet(
                entries,
                label
            ).apply {

                this.color =
                    android.graphics.Color.parseColor(color)

                setCircleColor(
                    android.graphics.Color.parseColor(color)
                )

                lineWidth = 3f

                circleRadius = 5f

                valueTextSize = 10f

                setDrawFilled(true)

                fillColor =
                    android.graphics.Color.parseColor(color)

                fillAlpha = 40

                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            chart.data = LineData(dataSet)

            chart.animateX(1000)

            chart.invalidate()
        }
    )
}

// ---------------------------------------------------
// Helper Functions
// ---------------------------------------------------

fun getGrowthStatus(

    ageMonths: Int,

    weight: Double,

    gender: String

): Pair<String, Color> {

    val medianWeight =

        if (gender == "Boy") {

            when (ageMonths) {

                0 -> 3.3
                1 -> 4.5
                2 -> 5.6
                3 -> 6.4
                4 -> 7.0
                5 -> 7.5
                6 -> 7.9
                7 -> 8.3
                8 -> 8.6
                9 -> 8.9
                10 -> 9.2
                11 -> 9.4
                12 -> 9.6

                else -> 10.0
            }

        } else {

            when (ageMonths) {

                0 -> 3.2
                1 -> 4.2
                2 -> 5.1
                3 -> 5.8
                4 -> 6.4
                5 -> 6.9
                6 -> 7.3
                7 -> 7.6
                8 -> 7.9
                9 -> 8.2
                10 -> 8.5
                11 -> 8.7
                12 -> 8.9

                else -> 9.5
            }
        }

    val difference =
        weight - medianWeight

    return when {

        difference < -2.0 -> {

            "Growth needs attention" to Color.Red
        }

        difference < -1.0 -> {

            "Slightly below expected range" to Color(0xFFFF9800)
        }

        difference > 2.5 -> {

            "Above expected range" to Color(0xFFE53935)
        }

        difference > 1.5 -> {

            "Higher than average growth" to Color(0xFFFB8C00)
        }

        else -> {

            "Within healthy WHO-inspired range" to Color(0xFF4CAF50)
        }
    }
}
fun calculateAgeInMonths(dob: Long, recordDate: Long): Int {
    val diff = recordDate - dob
    val months = (diff / (1000L * 60 * 60 * 24 * 30.44)).toInt() // More accurate days in month
    return months.coerceAtLeast(0)
}
