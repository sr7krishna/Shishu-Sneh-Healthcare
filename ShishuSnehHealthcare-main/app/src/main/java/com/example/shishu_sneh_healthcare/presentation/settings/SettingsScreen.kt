package com.example.shishu_sneh_healthcare.presentation.settings

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.presentation.report.ReportViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.shishu_sneh_healthcare.presentation.navigation.Screen
import com.example.shishu_sneh_healthcare.presentation.report.PdfReportGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(

    navController: NavHostController,

    babyId: Long,

    onBackClick: () -> Unit,

    reportViewModel: ReportViewModel = hiltViewModel()

) {

    var notificationsEnabled by remember {

        mutableStateOf(true)
    }

    var showAboutDialog by remember {

        mutableStateOf(false)
    }

    val baby by
    reportViewModel.baby.collectAsState()

    LaunchedEffect(babyId) {

        reportViewModel.loadReportData(
            babyId
        )
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Settings",
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
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .padding(16.dp)

        ) {

            // ---------------------------------------------------
            // ACCOUNT
            // ---------------------------------------------------

            Text(

                text = "Account",

                fontSize = 18.sp,

                fontWeight = FontWeight.Bold,

                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(

                title = "Edit Baby Profile",

                icon = Icons.Default.Person

            ) {

                navController.navigate(
                    Screen.ProfileSetup.route
                )
            }

            SettingsItem(

                title = "Growth Tracker",

                icon = Icons.Default.Timeline

            ) {

                navController.navigate(

                    Screen.GrowthChart.route +
                            "/$babyId"
                )
            }

            SettingsItem(

                title = "Vaccination Tracker",

                icon = Icons.Default.Vaccines

            ) {

                navController.navigate(

                    Screen.Vaccination.route +
                            "/$babyId"
                )
            }

            // ---------------------------------------------------
            // DYNAMIC PDF EXPORT
            // ---------------------------------------------------

            SettingsItem(

                title = "Export Health Report",

                icon = Icons.Default.PictureAsPdf

            ) {

                PdfReportGenerator.generateReport(

                    context = navController.context,

                    babyName =
                        baby?.name ?: "Baby",

                    ageText =
                        reportViewModel.getAgeText(),

                    latestWeight =
                        reportViewModel.getLatestWeight(),

                    latestHeight =
                        reportViewModel.getLatestHeight(),

                    vaccineSummary =
                        reportViewModel
                            .getVaccineSummary(),

                    milestoneProgress =
                        reportViewModel
                            .getMilestoneSummary(),

                    aiInsight =
                        reportViewModel
                            .getAiInsight()
                )
            }

            // ---------------------------------------------------
            // PREFERENCES
            // ---------------------------------------------------

            Spacer(modifier = Modifier.height(24.dp))

            Text(

                text = "Preferences",

                fontSize = 18.sp,

                fontWeight = FontWeight.Bold,

                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            NotificationToggleItem(

                checked = notificationsEnabled,

                onCheckedChange = {

                    notificationsEnabled = it
                }
            )



            SettingsItem(

                title = "About App",

                icon = Icons.Default.Info

            ) {

                showAboutDialog = true
            }

            Spacer(modifier = Modifier.weight(1f))

            // ---------------------------------------------------
            // LOGOUT
            // ---------------------------------------------------

            Button(

                onClick = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(0)
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .error
                    ),

                shape =
                    RoundedCornerShape(16.dp)

            ) {

                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    "Logout",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }

        // ---------------------------------------------------
        // ABOUT DIALOG
        // ---------------------------------------------------

        if (showAboutDialog) {

            AlertDialog(

                onDismissRequest = {

                    showAboutDialog = false
                },

                confirmButton = {

                    TextButton(

                        onClick = {

                            showAboutDialog = false
                        }

                    ) {

                        Text("OK")
                    }
                },

                title = {

                    Text("About Shishu-Sneh")
                },

                text = {

                    Text(

                        "Shishu-Sneh is a maternal and infant healthcare companion designed to help parents track baby growth, vaccinations, nutrition, and developmental milestones."
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsItem(

    title: String,

    icon: ImageVector,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {

                onClick()
            },

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            ),

        shape = RoundedCornerShape(12.dp)

    ) {

        Row(

            modifier = Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(

                text = title,

                fontSize = 16.sp,

                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = ">",
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun NotificationToggleItem(

    checked: Boolean,

    onCheckedChange: (Boolean) -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            ),

        shape = RoundedCornerShape(12.dp)

    ) {

        Row(

            modifier = Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Icon(

                imageVector =
                    Icons.Default.Notifications,

                contentDescription = null,

                tint =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(

                text = "Notifications",

                fontSize = 16.sp,

                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(

                checked = checked,

                onCheckedChange =
                    onCheckedChange
            )
        }
    }
}