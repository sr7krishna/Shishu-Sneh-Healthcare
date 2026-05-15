package com.example.shishu_sneh_healthcare.presentation.milestone

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.MilestoneEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: MilestoneViewModel = hiltViewModel()
) {
    val milestones by viewModel.milestones.collectAsState()

    LaunchedEffect(key1 = babyId) {
        viewModel.loadMilestones(babyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Milestones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            val progress = if (milestones.isNotEmpty()) {
                milestones.count {
                    it.status == "Completed"
                }.toFloat() / milestones.size
            } else 0f// Default 40% for visual professional feel if empty
            
            MilestoneProgressBar(progress = progress)
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (milestones.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🧸",
                                    fontSize = 42.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No milestones available yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Milestones will appear automatically based on your baby's age and development stage.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Show some sample milestones if empty for professional look
                    val samples = listOf(

                        MilestoneEntity(
                            0,
                            babyId,
                            1,
                            "Responds to familiar voices",
                            "Completed",
                            null,
                            "Babies begin recognizing sounds and voices."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            2,
                            "Smiles socially",
                            "Completed",
                            null,
                            "Begins smiling during interactions."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            3,
                            "Holds head up steadily",
                            "In Progress",
                            null,
                            "Neck strength improves with tummy time."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            4,
                            "Rolls from tummy to back",
                            "Not Yet",
                            null,
                            "Motor coordination begins developing."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            6,
                            "Sits with little support",
                            "Not Yet",
                            null,
                            "Core balance becomes stronger."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            8,
                            "Begins crawling",
                            "Not Yet",
                            null,
                            "Exploration and mobility increase."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            10,
                            "Pulls to stand",
                            "Not Yet",
                            null,
                            "Leg strength and balance improve."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            12,
                            "Walks with support",
                            "Not Yet",
                            null,
                            "First supported walking attempts."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            15,
                            "Says simple words",
                            "Not Yet",
                            null,
                            "Language skills begin forming."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            18,
                            "Uses spoon with assistance",
                            "Not Yet",
                            null,
                            "Self-feeding coordination develops."
                        ),

                        MilestoneEntity(
                            0,
                            babyId,
                            24,
                            "Forms simple sentences",
                            "Not Yet",
                            null,
                            "Communication and vocabulary expand."
                        )
                    )
                    items(samples) { milestone ->
                        MilestoneItem(milestone = milestone, onToggle = {})
                    }
                } else {
                    items(milestones) { milestone ->
                        MilestoneItem(
                            milestone = milestone,
                            onToggle = { updatedMilestone ->
                                viewModel.updateMilestone(updatedMilestone)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Overall Development", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun MilestoneItem(milestone: MilestoneEntity, onToggle: (MilestoneEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.status == "Yes") Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (milestone.status == "Yes") Color(0xFF4CAF50).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "${milestone.month}", fontWeight = FontWeight.Bold, color = if (milestone.status == "Yes") Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(

                shape = CircleShape,

                color =
                    MaterialTheme
                        .colorScheme
                        .secondaryContainer,

                modifier =
                    Modifier.size(72.dp)

            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text = "👶",

                        fontSize = 34.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Month ${milestone.month} Milestone", fontSize = 12.sp, color = Color.Gray)
                Text(text = milestone.description, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(
                        modifier = Modifier.height(4.dp)
                        )

                if (!milestone.notes.isNullOrBlank()) {

                    Text(

                        text = milestone.notes ?: "",

                        fontSize = 12.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
            Column(

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                FilterChip(

                    selected =
                        milestone.status == "Completed",

                    onClick = {

                        val newStatus = when (

                            milestone.status

                        ) {

                            "Not Yet" -> "In Progress"

                            "In Progress" -> "Completed"

                            else -> "Not Yet"
                        }

                        onToggle(

                            milestone.copy(
                                status = newStatus
                            )
                        )
                    },

                    label = {

                        Text(milestone.status)
                    }
                )
            }
        }
    }
}
