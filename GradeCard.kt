import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bcbt.Grade
import com.example.bcbt.GradeMateColors
import com.example.bcbt.Module
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// 🔹 Top-level composable that includes the summary and grade list
@Composable
fun GradeListScreen(
    grades: List<Grade>,
    allModules: List<Module>
) {
    // Compute summary using your helper function
    val summary = remember(grades, allModules) {
        computeGradeSummary(allModules, grades)
    }

    Column {
        GradeSummary(
            passed = summary.passed,
            failed = summary.failed,
            pending = summary.pending,
            total = summary.total
        )

        grades.forEach { grade ->
            GradeCard(grade = grade)
        }
    }
}

fun computeGradeSummary(
    allModules: List<Module>,
    grades: List<Grade>
): SummaryCounts {
    val gradesMap = grades.associateBy { it.moduleCode }

    val total = allModules.size
    var passed = 0
    var failed = 0
    var pending = 0

    for (module in allModules) {
        val grade = gradesMap[module.code]
        if (grade == null) {
            pending++
        } else {
            val caMark = grade.caMark
            when {
                caMark >= 30 -> passed++
                caMark in 1.0..29.9 -> failed++
                else -> pending++
            }
        }
    }

    return SummaryCounts(passed, failed, pending, total)
}

data class SummaryCounts(
    val passed: Int,
    val failed: Int,
    val pending: Int,
    val total: Int
)


// 🔹 Individual grade card
@SuppressLint("UnrememberedMutableState")
@Composable
fun GradeCard(grade: Grade) {
    val remark = if (grade.caMark >= 30) "Passed" else "Repeat"
    val remarkColor = if (grade.caMark >= 30) Color(0xFF4CAF50) else Color(0xFFF44336)
    val moduleTitle = remember { mutableStateOf("") }

    LaunchedEffect(grade.moduleCode) {
        getModuleName(grade.moduleCode) { name ->
            moduleTitle.value = name
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Module code and title + CA mark
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${grade.moduleCode} — ${moduleTitle.value.ifEmpty { "Loading..." }}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = GradeMateColors.Primary,
                        maxLines = 3,  // Allow up to 3 lines for wrapping
                        overflow = TextOverflow.Visible  // Wrap instead of ellipsis
                    )

                    Text(
                        text = "CA Mark: ${"%.1f".format(grade.caMark)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Right: Remark chip
                Chip(
                    text = remark,
                    backgroundColor = remarkColor.copy(alpha = 0.15f),
                    textColor = remarkColor
                )
            }
        }
    }
}


// 🔹 Small reusable chip
@Composable
fun Chip(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .defaultMinSize(minWidth = 64.dp) // 👈 Prevent vertical layout
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1
        )
    }
}

// 🔹 Firestore module name lookup
fun getModuleName(code: String, onResult: (String) -> Unit) {
    Firebase.firestore.collection("modules")
        .whereEqualTo("code", code)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val module = result.documents[0].toObject(Module::class.java)
                onResult(module?.name ?: "")
            } else {
                println("No module found with code: $code")
                onResult("")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting module: ${exception.message}")
            onResult("")
        }
}
suspend fun getModuleCreditSuspend(code: String): String {
    return try {
        val result = Firebase.firestore.collection("modules")
            .whereEqualTo("code", code)
            .get()
            .await()  // suspend here until task completes

        if (!result.isEmpty) {
            val module = result.documents[0].toObject(Module::class.java)
            module?.credit ?: ""
        }
        else {
            println("No module found with code: $code")
            ""
        }
    } catch (e: Exception) {
        println("Error getting module: ${e.message}")
        ""
    }
}






// 🔹 Grade summary section at the top
@Composable
fun GradeSummary(passed: Int, failed: Int, pending: Int, total: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF7F6)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),    // ensure Row fills the card width
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem("Modules", total, Color.DarkGray, Modifier.weight(1f))
            SummaryItem("Passed", passed, Color(0xFF4CAF50), Modifier.weight(1f))
            SummaryItem("Failed", failed, Color(0xFFF44336), Modifier.weight(1f))
            SummaryItem("Pending", pending, Color(0xFFFF9800), Modifier.weight(1f))
        }
    }
}

@Composable
fun SummaryItem(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp) // optional horizontal padding between items
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall,color = Color.LightGray, fontWeight = FontWeight.SemiBold)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

