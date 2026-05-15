package com.example.shishu_sneh_healthcare.presentation.dashboard

import java.util.Calendar
import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shishu_sneh_healthcare.presentation.navigation.Screen
import com.example.shishu_sneh_healthcare.ui.theme.LavenderHeader
import com.example.shishu_sneh_healthcare.ui.theme.PinkHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val selectedBaby by viewModel.selectedBaby.collectAsState()
    val babies by viewModel.babies.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBabies("mock_user_123")
    }

    Scaffold(
        bottomBar = {
            DashboardBottomNavigation(
                navController = navController,
                selectedTab = currentTab,
                onTabSelected = viewModel::setCurrentTab,
                babyId = selectedBaby?.id ?: -1L
            )
        }
    ) { padding ->
        val babyId = selectedBaby?.id ?: -1L

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                DashboardHeader(

                    babyName =
                        selectedBaby?.name ?: "Baby",

                    babyDob =
                        selectedBaby?.dob ?: 0L,

                    babyGender =
                        selectedBaby?.gender ?: "Girl",

                    guardianName =
                        selectedBaby?.motherName
                            ?: "Guardian",

                    guardianRole =
                        selectedBaby?.guardianRole
                            ?: "Guardian",

                    onProfileClick = {

                        navController.navigate(

                            Screen.ProfileSetup.route
                        )
                    }
                )
            }

            if (babies.isNotEmpty()) {
                item {
                    SummaryCardsSection(

                        navController = navController,

                        babyId = babyId,

                        babyDob =
                            selectedBaby?.dob ?: 0L,

                        vaccineText =
                            viewModel.getUpcomingVaccineText()
                    )
                }

                item {
                    QuickActionsGrid(navController, babyId)
                }

                item {

                    CareReminderSection(

                        babyDob =
                            selectedBaby?.dob ?: 0L
                    )
                }

                item {
                    UpcomingAlertsSection(

                        babyName =
                            selectedBaby?.name ?: "Baby",

                        babyDob =
                            selectedBaby?.dob ?: 0L,

                        vaccineText =
                            viewModel.getUpcomingVaccineText()
                    )
                }
                item {

                    DevelopmentProgressCard(

                        babyDob =
                            selectedBaby?.dob ?: 0L
                    )
                }

                item {

                    TodayTimelineCard(
                        babyDob =
                            selectedBaby?.dob ?: 0L
                    )
                }

                item {
                    AIInsightsCard(
                        babyDob =
                            selectedBaby?.dob ?: 0L
                    )
                }
            } else {
                item {
                    EmptyDashboardState {
                        navController.navigate(Screen.ProfileSetup.route)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EmptyDashboardState(onAddBabyClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "👶", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No babies added yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Add your baby's details to get started with health tracking and insights.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddBabyClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Baby Profile")
        }
    }
}


@Composable
fun DashboardHeader(

    babyName: String,

    babyDob: Long,

    babyGender: String,

    guardianName: String,

    guardianRole: String,

    onProfileClick: () -> Unit

) {

    val currentHour =
        Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY)

    val greeting = when {

        currentHour < 12 -> "Good Morning"

        currentHour < 18 -> "Good Afternoon"

        else -> "Good Evening"
    }

    val ageMonths = remember(babyDob) {

        if (babyDob == 0L) {

            0

        } else {

            val diff =
                System.currentTimeMillis() - babyDob

            (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
        }
    }

    val babyEmoji = when {

        ageMonths <= 2 -> "🍼"

        ageMonths <= 12 -> "👶"

        else -> {

            if (babyGender == "Boy") {

                "🧒"

            } else {

                "👧"
            }
        }
    }

    val guardianEmoji = when (guardianRole) {

        "Mother" -> "👩"

        "Father" -> "👨"

        else -> "🧑"
    }

    val ageText = when {

        ageMonths <= 0 -> "Newborn"

        ageMonths == 1 -> "1 month old"

        ageMonths < 12 -> "$ageMonths months old"

        else -> "${ageMonths / 12} years old"
    }

    Surface(

        modifier = Modifier.fillMaxWidth(),

        color =
            MaterialTheme
                .colorScheme
                .primaryContainer

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween

        ) {

            // ---------------------------------------------------
            // LEFT SIDE
            // ---------------------------------------------------

            Row(

                modifier = Modifier.weight(1f),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Surface(

                    shape = CircleShape,

                    color =
                        MaterialTheme
                            .colorScheme
                            .secondaryContainer,

                    modifier =
                        Modifier.size(74.dp)

                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            text = babyEmoji,

                            fontSize = 34.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column(

                    verticalArrangement =
                        Arrangement.Center

                ) {

                    Text(

                        text =
                            "$guardianEmoji $greeting",

                        fontSize = 15.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimaryContainer
                                .copy(alpha = 0.8f)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(

                        text = guardianName,

                        fontSize = 28.sp,

                        fontWeight =
                            FontWeight.ExtraBold,

                        maxLines = 1
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(

                        text =
                            "$babyName • $ageText",

                        fontSize = 14.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimaryContainer
                                .copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // ---------------------------------------------------
            // SETTINGS BUTTON
            // ---------------------------------------------------

            FilledTonalIconButton(

                onClick = onProfileClick

            ) {

                Icon(

                    Icons.Default.Settings,

                    contentDescription = "Settings"
                )
            }
        }
    }
}
@Composable
fun CareReminderSection(

    babyDob: Long

) {

    val ageMonths = remember(babyDob) {

        if (babyDob == 0L) {

            0

        } else {

            val diff =
                System.currentTimeMillis() - babyDob

            (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
        }
    }

    val currentHour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]

    val reminderData = when {
        ageMonths < 6 -> {
            when {
                currentHour in 6..11 -> {
                    Triple(
                        "Morning Feeding Time",
                        "Breastfeed every 2–3 hours for healthy growth",
                        Icons.Default.ChildCare
                    )
                }
                currentHour in 12..18 -> {
                    Triple(
                        "Vitamin D Reminder",
                        "Daily Vitamin D drops are recommended",
                        Icons.Default.Medication
                    )
                }
                else -> {
                    Triple(
                        "Night Feeding Care",
                        "Keep baby hydrated and monitor sleep",
                        Icons.Default.Nightlight
                    )
                }
            }
        }
        ageMonths < 12 -> {
            Triple(
                "Nutrition Suggestion",
                "Introduce mashed fruits and soft solids gradually",
                Icons.Default.Restaurant
            )
        }
        else -> {
            Triple(
                "Toddler Nutrition",
                "Encourage balanced meals and hydration",
                Icons.Default.Favorite
            )
        }
    }

    val reminderTitle = reminderData.first

    val reminderSubtitle = reminderData.second

    val icon = reminderData.third

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(

            text = "Smart Care Reminder",

            style =
                MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .primaryContainer
                            .copy(alpha = 0.2f)
                )

        ) {

            Row(

                modifier = Modifier.padding(16.dp),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Surface(

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,

                    shape = RoundedCornerShape(12.dp),

                    modifier =
                        Modifier.size(48.dp)

                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(

                            imageVector = icon,

                            contentDescription = null,

                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = reminderTitle,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(

                        text = reminderSubtitle,

                        fontSize = 13.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}
@Composable
fun SummaryCardsSection(

    navController: NavHostController,

    babyId: Long,

    babyDob: Long,

    vaccineText: String

) {

    val ageMonths = remember(babyDob) {

        if (babyDob == 0L) {

            0

        } else {

            val diff =
                System.currentTimeMillis() - babyDob

            (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
        }
    }

    val feedingText = when {

        ageMonths < 6 -> {
            "Every 2-3 hrs"
        }

        ageMonths < 12 -> {
            "Semi-solid stage"
        }

        else -> {
            "Balanced meals"
        }
    }

    val growthText = when {

        ageMonths < 3 -> {
            "Rapid Growth"
        }

        ageMonths < 6 -> {
            "Healthy"
        }

        ageMonths < 12 -> {
            "Developing Well"
        }

        else -> {
            "Stable"
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(

            text = "Today's Summary",

            style =
                MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(

            horizontalArrangement =
                Arrangement.spacedBy(16.dp),

            contentPadding =
                PaddingValues(horizontal = 4.dp)

        ) {

            item {

                SummaryCard(

                    title = "Vaccines",

                    value = vaccineText,

                    icon = Icons.Default.Vaccines,

                    color =
                        MaterialTheme.colorScheme.primary

                ) {

                    navController.navigate(
                        Screen.Vaccination.route + "/$babyId"
                    )
                }
            }

            item {

                SummaryCard(

                    title = "Growth",

                    value = growthText,

                    icon = Icons.Default.Timeline,

                    color =
                        MaterialTheme.colorScheme.secondary

                ) {

                    navController.navigate(
                        Screen.GrowthChart.route + "/$babyId"
                    )
                }
            }

            item {

                SummaryCard(

                    title = "Nutrition",

                    value = feedingText,

                    icon = Icons.Default.ChildCare,

                    color =
                        MaterialTheme.colorScheme.tertiary

                ) {

                    navController.navigate(
                        Screen.Feeding.route + "/$babyId"
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(width = 150.dp, height = 110.dp),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun QuickActionsGrid(navController: NavHostController, babyId: Long) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Log Growth", Icons.Default.Scale, Modifier.weight(1f), MaterialTheme.colorScheme.primaryContainer) {
                navController.navigate(Screen.GrowthChart.route + "/$babyId")
            }
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier, containerColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = containerColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
@Composable
fun DevelopmentProgressCard(

    babyDob: Long

) {

    val ageMonths = remember(babyDob) {

        if (babyDob == 0L) {

            0

        } else {

            val diff =
                System.currentTimeMillis() - babyDob

            (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
        }
    }

    val milestones = when {

        ageMonths <= 2 -> {

            listOf(

                "Responds to voices",

                "Social smiling",

                "Eye tracking"
            )
        }

        ageMonths <= 6 -> {

            listOf(

                "Head control",

                "Rolling over",

                "Sitting support"
            )
        }

        ageMonths <= 12 -> {

            listOf(

                "Crawling",

                "Standing support",

                "First words"
            )
        }

        else -> {

            listOf(

                "Walking",

                "Simple communication",

                "Interactive play"
            )
        }
    }

    Card(

        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    MaterialTheme
                        .colorScheme
                        .secondaryContainer
                        .copy(alpha = 0.25f)
            )

    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    Icons.Default.Insights,

                    contentDescription = null,

                    tint =
                        MaterialTheme
                            .colorScheme
                            .secondary
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(

                    text = "Development Progress",

                    fontWeight = FontWeight.Bold,

                    fontSize = 20.sp
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(

                text =
                    "Observed developmental milestones",

                fontSize = 13.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            val checkedStates = remember(milestones) {
                mutableStateListOf<Boolean>().apply {
                    repeat(milestones.size) { add(false) }
                }
            }

            milestones.forEachIndexed { index, milestone ->

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Checkbox(
                        checked = checkedStates[index],
                        onCheckedChange = { checkedStates[index] = it }
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = milestone
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}
@Composable
fun UpcomingAlertsSection(

    babyName: String,

    babyDob: Long,

    vaccineText: String

) {

    // ---------------------------------------------------
    // VACCINE STATUS
    // ---------------------------------------------------

    val isOverdue =
        vaccineText.contains(
            "overdue",
            ignoreCase = true
        )

    val containerColor =
        if (isOverdue) {

            MaterialTheme
                .colorScheme
                .errorContainer
                .copy(alpha = 0.25f)

        } else {

            MaterialTheme
                .colorScheme
                .primaryContainer
                .copy(alpha = 0.2f)
        }

    Card(

        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = containerColor
            )

    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(

                text = "Upcoming Events & Alerts",

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ---------------------------------------------------
            // VACCINE ALERT
            // ---------------------------------------------------

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    imageVector =
                        if (isOverdue)
                            Icons.Default.Warning
                        else
                            Icons.Default.Vaccines,

                    contentDescription = null,

                    tint =
                        if (isOverdue)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(

                        text =
                            if (isOverdue)
                                "Vaccination Attention Needed"
                            else
                                "Vaccination Update",

                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = vaccineText,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AIInsightsCard(

    babyDob: Long

) {

    val ageMonths = remember(babyDob) {

        if (babyDob == 0L) {

            0

        } else {

            val diff =
                System.currentTimeMillis() - babyDob

            (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
        }
    }

    // ---------------------------------------------------
    // AGE-BASED INSIGHTS
    // ---------------------------------------------------

    val insights = remember(ageMonths) {

        when {

            ageMonths <= 2 -> {

                listOf(

                    "Skin-to-skin bonding supports emotional development.",

                    "Frequent feeding helps healthy newborn growth.",

                    "Newborns sleep 14–17 hours daily on average.",

                    "Gentle interaction helps babies recognize familiar voices.",

                    "Responsive caregiving builds trust and comfort."
                )
            }

            ageMonths <= 6 -> {

                listOf(

                    "Tummy time helps strengthen neck and shoulder muscles.",

                    "Babies often begin rolling over around this age.",

                    "Visual tracking activities support cognitive growth.",

                    "Consistent sleep routines improve development.",

                    "Smiling and interaction help social learning."
                )
            }

            ageMonths <= 12 -> {

                listOf(

                    "Introduce iron-rich semi-solid foods gradually.",

                    "Crawling supports coordination and motor skills.",

                    "Reading aloud helps language development.",

                    "Babies learn quickly through play and repetition.",

                    "Hydration and balanced nutrition remain important."
                )
            }

            else -> {

                listOf(

                    "Toddler play supports creativity and communication.",

                    "Balanced meals encourage healthy long-term growth.",

                    "Daily routines improve emotional stability.",

                    "Outdoor activity supports physical development.",

                    "Interactive storytelling boosts language skills."
                )
            }
        }
    }

    // ---------------------------------------------------
    // ROTATING INSIGHT INDEX
    // ---------------------------------------------------

    var currentInsightIndex by remember {

        mutableIntStateOf(0)
    }

    LaunchedEffect(insights) {

        while (true) {

            delay(6000)

            currentInsightIndex =

                (currentInsightIndex + 1) %
                        insights.size
        }
    }

    val currentInsight =
        insights[currentInsightIndex]

    // ---------------------------------------------------
    // UI
    // ---------------------------------------------------

    Card(

        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    MaterialTheme
                        .colorScheme
                        .tertiaryContainer
                        .copy(alpha = 0.35f)
            )

    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    Icons.Default.AutoAwesome,

                    contentDescription = null,

                    tint =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Text(

                    text = "AI Health Insight",

                    fontWeight = FontWeight.Bold,

                    fontSize = 18.sp
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            AnimatedContent(

                targetState = currentInsight,

                label = "InsightAnimation"

            ) { insight ->

                Text(

                    text = insight,

                    fontSize = 16.sp,

                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun TodayTimelineCard(
    babyDob: Long
) {
    val ageMonths = remember(babyDob) {
        if (babyDob == 0L) {
            0
        } else {
            val diff = System.currentTimeMillis() - babyDob
            (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
        }
    }

    val timelineEvents = remember(ageMonths) {
        buildList {
            add(
                Triple(
                    "Morning",
                    "Feeding Schedule",
                    Icons.Default.Restaurant
                )
            )

            if (ageMonths < 6) {
                add(
                    Triple(
                        "Afternoon",
                        "Vitamin D Reminder",
                        Icons.Default.Medication
                    )
                )
            }

            add(
                Triple(
                    "Evening",
                    "Sleep Routine Reminder",
                    Icons.Default.Nightlight
                )
            )

            if (ageMonths <= 12) {
                add(
                    Triple(
                        "Tomorrow",
                        "Vaccination Check",
                        Icons.Default.Vaccines
                    )
                )
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Today's Timeline",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            timelineEvents.forEachIndexed { index, event ->
                val time = event.first
                val title = event.second
                val icon = event.third

                Row(verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (index != timelineEvents.lastIndex) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = time,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = title,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun DashboardBottomNavigation(
    navController: NavHostController,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    babyId: Long
) {
    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { 
                onTabSelected(0)
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Timeline, contentDescription = null) },
            label = { Text("Growth") },
            selected = selectedTab == 1,
            onClick = { 
                onTabSelected(1)
                if (babyId != -1L) {
                    navController.navigate(Screen.GrowthChart.route + "/$babyId")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("Records") },
            selected = selectedTab == 2,
            onClick = { 
                onTabSelected(2)
                if (babyId != -1L) {
                    navController.navigate(Screen.HealthRecords.route + "/$babyId")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            selected = selectedTab == 3,
            onClick = { 
                onTabSelected(3)
                if (babyId != -1L) {
                    navController.navigate(Screen.Settings.route + "/$babyId")
                }
            }
        )
    }
}
