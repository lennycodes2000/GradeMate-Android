import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bcbt.Constants
import com.example.bcbt.Grade
import com.example.bcbt.GradeMateColors
import com.example.bcbt.R
import com.example.bcbt.gradeList
import com.example.bcbt.loadGrades
import com.example.bcbt.loadModules
import com.example.bcbt.loadStudent


// Data classes
@SuppressLint("MutableCollectionMutableState")
@Composable
fun CourseResult(grades: List<Grade>, modifier: Modifier = Modifier) {
    var moduleNameCache by remember { mutableStateOf(mutableMapOf<String, String>()) }
    fun getLevel(input: String?): Char? {
        if (input == null) return null
        // Filter only digits from the string
        val digits = input.filter { it.isDigit() }
        // Return the second digit if exists, else null
        return if (digits.length >= 2) digits[1] else null
    }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (grades.isNotEmpty()) {
            itemsIndexed(grades) { _, course ->
                val moduleName = moduleNameCache[course.moduleCode] ?: "Loading..."
                LaunchedEffect(course.moduleCode) {
                    if (!moduleNameCache.containsKey(course.moduleCode)) {
                        getModuleName(course.moduleCode) { name ->
                            moduleNameCache = moduleNameCache.toMutableMap().apply {
                                put(course.moduleCode, name)
                            }
                        }
                    }
                }

                val courseNameLower = moduleName.lowercase()
                val courseIcon = when {
                    "programming" in courseNameLower -> R.drawable.programmin
                    "database" in courseNameLower -> R.drawable.database
                    "web" in courseNameLower -> R.drawable.website
                    "mathematics" in courseNameLower -> R.drawable.maths
                    "statistics" in courseNameLower -> R.drawable.maths
                    "graphics" in courseNameLower -> R.drawable.graphics
                    "computer networking" in courseNameLower -> R.drawable.network
                    "computer communications" in courseNameLower -> R.drawable.network
                    "management information system" in courseNameLower -> R.drawable.information
                    "development of  information systems" in courseNameLower -> R.drawable.information
                    "system analysis" in courseNameLower -> R.drawable.sad
                    "computer maintenance" in courseNameLower -> R.drawable.maintenance
                    "computer application" in courseNameLower -> R.drawable.office
                    "computer software" in courseNameLower -> R.drawable.software
                    "data communication" in courseNameLower -> R.drawable.network
                    "operating system" in courseNameLower -> R.drawable.os
                    "communication skills" in courseNameLower -> R.drawable.communication
                    "business communication" in courseNameLower -> R.drawable.communication
                    "e-business" in courseNameLower -> R.drawable.ebusiness
                    "e-commerce" in courseNameLower -> R.drawable.ebusiness
                    "architecture" in courseNameLower -> R.drawable.architecture
                    "project" in courseNameLower -> R.drawable.project
                    "security" in courseNameLower -> R.drawable.security
                    else -> R.drawable.regular
                }

                val ntaLevelChar = getLevel(course.moduleCode)
                val ntaLevel = ntaLevelChar?.digitToIntOrNull()
                val totalMark = course.total
                val ueMark = course.examMark.toDoubleOrNull()

                val gradeChar = if (ntaLevel == 4 || ntaLevel == 5) {
                    when {
                        ueMark != null && ueMark < 20 -> "TS"
                        totalMark >= 80 -> "A"
                        totalMark >= 65 -> "B"
                        totalMark >= 50 -> "C"
                        totalMark >= 40 -> "D"
                        else -> "F"
                    }
                } else {
                    when {
                        ueMark != null && ueMark < 20 -> "TS"
                        totalMark >= 75 -> "A"
                        totalMark >= 65 -> "B+"
                        totalMark >= 55 -> "B"
                        totalMark >= 45 -> "C"
                        totalMark >= 35 -> "D"
                        else -> "F"
                    }
                }

                val gradeRemarks = course.examMark.toDoubleOrNull()?.let {
                    if (it < 20) {
                        "Technical Supplementary"
                    } else {
                        if (gradeChar == "D" || gradeChar == "F") "Supplementary" else "Passed"
                    }
                }

                val gradeColor = when {
                    gradeRemarks == "Technical Supplementary" -> GradeMateColors.Error
                    gradeChar == "D" || gradeChar == "F" -> GradeMateColors.Error
                    else -> GradeMateColors.Secondary
                }

                // Card UI (unchanged)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = GradeMateColors.back1
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(courseIcon),
                            contentDescription = course.moduleCode,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GradeMateColors.Background)
                                .padding(8.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = moduleName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GradeMateColors.Primary
                                )
                            )
                            Text(
                                text = course.moduleCode,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                )
                            )
                        }

                        Text(
                            text = course.total.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GradeMateColors.Primary
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(gradeColor)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = gradeChar,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GradeMateColors.Background
                                )
                            )
                            Text(
                                text = gradeRemarks.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GradeMateColors.Background
                                )
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

