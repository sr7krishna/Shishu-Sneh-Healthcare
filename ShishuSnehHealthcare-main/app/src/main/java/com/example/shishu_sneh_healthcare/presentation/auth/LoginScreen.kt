package com.example.shishu_sneh_healthcare.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.shishu_sneh_healthcare.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(

    navController: NavHostController

) {

    Surface(

        modifier = Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center

        ) {

            // ---------------------------------------------------
            // APP ICON
            // ---------------------------------------------------

            Surface(

                shape = RoundedCornerShape(32.dp),

                color =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer

            ) {

                Icon(

                    Icons.Default.ChildCare,

                    contentDescription = null,

                    modifier = Modifier
                        .padding(24.dp)
                        .size(56.dp),

                    tint =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------------------------------------------------
            // TITLE
            // ---------------------------------------------------

            Text(

                text = "Welcome to Shishu-Sneh",

                fontSize = 30.sp,

                fontWeight = FontWeight.ExtraBold,

                textAlign = androidx.compose.ui.text.style.TextAlign.Center,

                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                text =
                    "Track your baby's growth, vaccines and development journey.",

                fontSize = 16.sp,

                textAlign = androidx.compose.ui.text.style.TextAlign.Center,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(60.dp))

            // ---------------------------------------------------
            // GET STARTED BUTTON
            // ---------------------------------------------------

            Button(

                onClick = {

                    navController.navigate(
                        Screen.ProfileSetup.route
                    ) {

                        popUpTo(
                            Screen.Login.route
                        ) {

                            inclusive = true
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Text(

                    text = "Get Started",

                    fontWeight = FontWeight.Bold,

                    fontSize = 18.sp
                )
            }
        }
    }
}