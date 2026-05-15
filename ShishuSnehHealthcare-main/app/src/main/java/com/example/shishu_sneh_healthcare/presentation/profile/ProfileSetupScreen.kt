package com.example.shishu_sneh_healthcare.presentation.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.presentation.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(

    navController: NavHostController,

    viewModel: ProfileViewModel = hiltViewModel()

) {

    var step by remember {

        mutableIntStateOf(1)
    }

    // ---------------------------------------------------
    // Guardian Details
    // ---------------------------------------------------

    var guardianName by remember {

        mutableStateOf("")
    }

    val guardianRoles = listOf(

        "Mother",

        "Father",

        "Guardian"
    )

    var selectedRoleIndex by remember {

        mutableIntStateOf(0)
    }

    // ---------------------------------------------------
    // Baby Details
    // ---------------------------------------------------

    var babyName by remember {

        mutableStateOf("")
    }

    var babyDob by remember {

        mutableStateOf("")
    }

    var babyGender by remember {

        mutableStateOf("Girl")
    }

    var birthWeight by remember {

        mutableStateOf("")
    }

    var birthHeight by remember {

        mutableStateOf("")
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {

                    Text(

                        text = "Setup Profile",

                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            LinearProgressIndicator(

                progress = {

                    step.toFloat() / 2f
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),

                strokeCap =
                    androidx.compose.ui.graphics
                        .StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(

                targetState = step,

                label = "SetupStep"

            ) { currentStep ->

                if (currentStep == 1) {

                    ParentInfoStep(

                        guardianName = guardianName,

                        guardianRoles = guardianRoles,

                        selectedRoleIndex =
                            selectedRoleIndex,

                        onNameChange = {

                            guardianName = it
                        },

                        onRoleSelected = {

                            selectedRoleIndex = it
                        },

                        onNext = {

                            step = 2
                        }
                    )

                } else {

                    BabyInfoStep(

                        babyName = babyName,

                        onBabyNameChange = {

                            babyName = it
                        },

                        babyDob = babyDob,

                        onDobChange = {

                            babyDob = it
                        },

                        babyGender = babyGender,

                        onGenderChange = {

                            babyGender = it
                        },

                        birthWeight = birthWeight,

                        onBirthWeightChange = {

                            birthWeight = it
                        },

                        birthHeight = birthHeight,

                        onBirthHeightChange = {

                            birthHeight = it
                        },

                        onFinish = {

                            try {

                                val sdf =
                                    SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    )

                                val parsedDate =
                                    sdf.parse(babyDob)

                                val dobMillis =
                                    parsedDate?.time
                                        ?: System.currentTimeMillis()

                                val baby = BabyEntity(

                                    name = babyName,

                                    dob = dobMillis,

                                    gender = babyGender,

                                    bloodGroup = "Unknown",

                                    birthWeight =
                                        birthWeight
                                            .toDoubleOrNull()
                                            ?: 0.0,

                                    birthHeight =
                                        birthHeight
                                            .toDoubleOrNull()
                                            ?: 0.0,

                                    photoUri = null,

                                    motherName =
                                        guardianName,

                                    guardianRole =
                                        guardianRoles[
                                            selectedRoleIndex
                                        ],

                                    pediatrician = null,

                                    hospital = null,

                                    userId = "mock_user_123"
                                )

                                viewModel.saveBabyDetails(
                                    baby
                                ) {

                                    navController.navigate(
                                        Screen.Dashboard.route
                                    ) {

                                        popUpTo(
                                            Screen.ProfileSetup.route
                                        ) {

                                            inclusive = true
                                        }
                                    }
                                }

                            } catch (e: Exception) {

                                e.printStackTrace()
                            }
                        },

                        onBack = {

                            step = 1
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ParentInfoStep(

    guardianName: String,

    guardianRoles: List<String>,

    selectedRoleIndex: Int,

    onNameChange: (String) -> Unit,

    onRoleSelected: (Int) -> Unit,

    onNext: () -> Unit

) {

    Column {

        Text(

            text = "Step 1: Guardian Info",

            fontSize = 22.sp,

            fontWeight = FontWeight.ExtraBold
        )

        Text(

            text =
                "Help us personalize your experience",

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(

            value = guardianName,

            onValueChange = onNameChange,

            label = {

                Text("Guardian Name")
            },

            leadingIcon = {

                Icon(
                    Icons.Default.Person,
                    contentDescription = null
                )
            },

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(

            text = "Relationship",

            fontWeight = FontWeight.Bold,

            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(

            verticalArrangement =
                Arrangement.spacedBy(12.dp)

        ) {

            guardianRoles.forEachIndexed { index, role ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(

                            selected =
                                selectedRoleIndex == index,

                            onClick = {

                                onRoleSelected(index)
                            }
                        ),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(

                            containerColor =

                                if (selectedRoleIndex == index) {

                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .surface
                                }
                        )

                ) {

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        RadioButton(

                            selected =
                                selectedRoleIndex == index,

                            onClick = {

                                onRoleSelected(index)
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )

                        Text(

                            text = role,

                            fontSize = 17.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(

            onClick = onNext,

            enabled =
                guardianName.isNotBlank(),

            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),

            shape =
                RoundedCornerShape(18.dp)

        ) {

            Text(

                text = "Next: Baby Details",

                fontWeight = FontWeight.Bold,

                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun BabyInfoStep(

    babyName: String,

    onBabyNameChange: (String) -> Unit,

    babyDob: String,

    onDobChange: (String) -> Unit,

    babyGender: String,

    onGenderChange: (String) -> Unit,

    birthWeight: String,

    onBirthWeightChange: (String) -> Unit,

    birthHeight: String,

    onBirthHeightChange: (String) -> Unit,

    onFinish: () -> Unit,

    onBack: () -> Unit

) {

    Column {

        Text(

            text = "Step 2: Baby Info",

            fontSize = 22.sp,

            fontWeight = FontWeight.ExtraBold
        )

        Text(

            text =
                "Tell us about your little one",

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(

            value = babyName,

            onValueChange =
                onBabyNameChange,

            label = {

                Text("Baby's Name")
            },

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(

            value = babyDob,

            onValueChange =
                onDobChange,

            label = {

                Text(
                    "Date of Birth (DD/MM/YYYY)"
                )
            },

            modifier =
                Modifier.fillMaxWidth(),

            keyboardOptions =
                KeyboardOptions(

                    keyboardType =
                        KeyboardType.Number
                ),

            shape =
                RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(

            value = birthWeight,

            onValueChange =
                onBirthWeightChange,

            label = {

                Text("Birth Weight (kg)")
            },

            modifier =
                Modifier.fillMaxWidth(),

            keyboardOptions =
                KeyboardOptions(

                    keyboardType =
                        KeyboardType.Decimal
                ),

            shape =
                RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(

            value = birthHeight,

            onValueChange =
                onBirthHeightChange,

            label = {

                Text("Birth Height (cm)")
            },

            modifier =
                Modifier.fillMaxWidth(),

            keyboardOptions =
                KeyboardOptions(

                    keyboardType =
                        KeyboardType.Decimal
                ),

            shape =
                RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(

                selected =
                    babyGender == "Boy",

                onClick = {

                    onGenderChange("Boy")
                }
            )

            Text("Boy")

            Spacer(
                modifier =
                    Modifier.width(16.dp)
            )

            RadioButton(

                selected =
                    babyGender == "Girl",

                onClick = {

                    onGenderChange("Girl")
                }
            )

            Text("Girl")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(

            onClick = onFinish,

            enabled =
                babyName.isNotBlank() &&
                        babyDob.isNotBlank() &&
                        birthWeight.isNotBlank() &&
                        birthHeight.isNotBlank(),

            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),

            shape =
                RoundedCornerShape(18.dp)

        ) {

            Text(

                text = "Complete Setup",

                fontWeight = FontWeight.Bold,

                fontSize = 16.sp
            )
        }

        TextButton(

            onClick = onBack,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)

        ) {

            Text(

                text = "Go Back",

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}