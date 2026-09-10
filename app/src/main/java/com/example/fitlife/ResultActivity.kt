package com.example.fitlife

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitlife.ui.theme.FitLifeTheme
import kotlinx.coroutines.delay

class ResultActivity : ComponentActivity() {
    private val TAG = "ResultActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()

        val studentName = intent.getStringExtra("STUDENT_NAME") ?: "Unknown"
        val steps = intent.getStringExtra("STEPS") ?: "0"
        val activityType = intent.getStringExtra("ACTIVITY_TYPE") ?: "None"
        val dailyGoal = intent.getBooleanExtra("DAILY_GOAL", false)

        setContent {
            FitLifeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResultScreen(
                        name = studentName,
                        steps = steps,
                        activityType = activityType,
                        dailyGoalStatus = dailyGoal,
                        modifier = Modifier.padding(innerPadding),
                        onBackClick = { 
                            finish()
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }
                    )
                }
            }
        }
    }

    // Removed onBackPressed override for Android 13+ compatibility

    override fun onStart() { super.onStart() }
    override fun onResume() { super.onResume() }
    override fun onPause() { super.onPause() }
    override fun onStop() { super.onStop() }
    override fun onDestroy() { super.onDestroy() }
}

@Composable
fun ResultScreen(
    name: String,
    steps: String,
    activityType: String,
    dailyGoalStatus: Boolean,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_fitness_logo),
                contentDescription = "Success",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            Text(
                text = "Activity Summary",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    SummaryRow("Student Name", name)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    SummaryRow("Number of Steps", steps)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    SummaryRow("Selected Activity", activityType)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    SummaryRow("Daily Goal Met", if (dailyGoalStatus) "Yes" else "No")
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { 150 }, animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Go Back", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
    }
}