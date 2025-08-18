package com.example.bcbt

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
object Auth {
    private val _id = mutableStateOf("")
    val id: androidx.compose.runtime.State<String> get() = _id
    fun setId(newId: String) {
        _id.value = newId
    }
    var noModules = mutableStateOf("")
    var totalCredits = mutableDoubleStateOf(0.0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeHome(navController: NavController) {
    BackHandler(enabled = true) {
        // Do nothing — disables back press on splash
    }
    StatusBar()
    var bottomIndex by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    // perform logout logic here
                    Firebase.auth.signOut()
                    navController.navigate(Routes.gradeSplash)
                    showLogoutDialog = false
                    studentList.clear()
                    gradeList.clear()

                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirm Logout") },
            text = { Text(text = "Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        clearAuth()
                        Firebase.auth.signOut()
                        navController.navigate(Routes.gradeSplash) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text(text = "Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }


    Scaffold(
        floatingActionButton = {
            if(bottomIndex == 0){
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.home)
                    },
                    modifier = Modifier.padding(16.dp),
                    containerColor = GradeMateColors.Primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.website),
                        contentDescription = "Open BCBT website",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GradeMateColors.Primary
                ),
                title = {
                    val titles = listOf(Constants.title, "Courseworks", "Examination Results","Settings")

                    Text(
                        text = titles.getOrElse(bottomIndex) { "" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                },
                actions = {

                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.size(48.dp) // Better touch target
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = GradeMateColors.Background,
                            modifier = Modifier.size(24.dp))
                    }

                }

            )
        },
        bottomBar = {
            NavSection(
                selectedIndex = bottomIndex,
                navAction = { index -> bottomIndex = index }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GradeMateColors.backGradient),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (studentList.isNotEmpty()) {
                val currentNtaLevel = studentList[0].ntaLevel
                val currentSemester = studentList[0].semester
                // proceed with these variables
                val screen: @Composable () -> Unit = {
                    when (bottomIndex) {
                        0 -> {
                            if (studentList.isNotEmpty()) Landing()
                            else NotPublished("No students available yet")
                        }

                        1 -> {
                            val publishItem = publish.firstOrNull()
                            if (publishItem != null &&
                                publishItem.coursework == "allowed" &&
                                (publishItem.courseworkNtaLevel == currentNtaLevel || publishItem.courseworkNtaLevel == "All") &&
                                (publishItem.courseworkSemester == currentSemester || publishItem.courseworkSemester == "Both")
                            ) {
                                if (studentList.isNotEmpty() && gradeList.isNotEmpty()) {
                                    Coursework(
                                        studentList = studentList,
                                        gradeList = gradeList,
                                        isLoading = isLoading,
                                        loadStudent = { loadStudent() },
                                        loadGrades = { nta, sem -> loadGrades(nta, sem) }
                                    )
                                } else {
                                    NotPublished("No coursework data available")
                                }
                            } else {
                                NotPublished("Coursework not yet published")
                            }
                        }

                        2 -> {
                            val publishItem = publish.firstOrNull()
                            if (publishItem != null &&
                                publishItem.results == "allowed" &&
                                (publishItem.resultsNtaLevel == currentNtaLevel || publishItem.resultsNtaLevel == "All") &&
                                (publishItem.resultsSemester == currentSemester || publishItem.resultsSemester == "Both")
                            ) {
                                if (studentList.isNotEmpty() && gradeList.isNotEmpty()) {
                                    GradeResults()
                                } else {
                                    NotPublished("No results available yet")
                                }
                            } else {
                                NotPublished("Results not yet published")
                            }
                        }

                        3 -> Settings()

                        else -> Text("Unknown Screen", color = Color.Black)
                    }
                }


// Render the screen
                screen()

            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}




@Composable
fun NavSection(
    selectedIndex: Int,
    navAction: (Int) -> Unit
) {
    NavigationBar(
        containerColor = GradeMateColors.back1,
        tonalElevation = 0.dp
    ) {
        navItems.forEachIndexed { index, nav ->
            val isSelected = selectedIndex == index

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else GradeMateColors.Primary,
                label = "AnimatedNavColor"
            )

            // Animate elevation for shadow
            val animatedElevation by animateDpAsState(
                targetValue = if (isSelected) 8.dp else 0.dp,
                label = "AnimatedElevation"
            )

            val horizontalPadding = 8.dp
            val startPadding = if (index == 0) horizontalPadding else 0.dp
            val endPadding = if (index == navItems.lastIndex) horizontalPadding else 0.dp

            val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

            Box(
                modifier = Modifier
                    .padding(start = startPadding, end = endPadding)
                    .weight(1f)
                    .height(56.dp)
                    .shadow(
                        elevation = animatedElevation,
                        shape = shape,
                        clip = false
                    )
                    .clip(shape)
                    .background(if (isSelected) GradeMateColors.Primary else Color.Transparent)
                    .clickable { navAction(index) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(nav.icon),
                        contentDescription = nav.label,
                        tint = animatedColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        nav.label,
                        color = animatedColor,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

fun clearAuth(){
    Auth.setId("")
    Auth.noModules.value = ""
    Auth.totalCredits.doubleValue = 0.0
   moduleList.clear()
    studentList.clear()
}







