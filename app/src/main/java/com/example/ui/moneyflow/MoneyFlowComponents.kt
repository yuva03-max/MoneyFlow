package com.example.ui.moneyflow

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.moneyflow.TransactionEntity
import com.example.ui.theme.*

// --- GLASSMORPHIC & FINTECH CARDS ---

@Composable
fun MainBalanceCard(
    modifier: Modifier = Modifier,
    totalBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    savings: Double,
    currencySymbol: String = "$",
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DarkBlue, PrimaryBlue, AccentBlue),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(24.dp)
        ) {
            // Background design accents
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(alpha = 0.15f)
            ) {
                drawCircle(
                    color = Color.White,
                    radius = 180f,
                    center = Offset(size.width * 0.9f, size.height * 0.2f)
                )
                drawCircle(
                    color = LightBlue,
                    radius = 240f,
                    center = Offset(size.width * 0.1f, size.height * 0.9f)
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL BALANCE",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currencySymbol${String.format("%,.2f", totalBalance)}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    // Mini chip indicator for high status
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "PREMIUM",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Income / Expenses Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Income Item
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Income",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "INCOME",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$currencySymbol${String.format("%,.1f", totalIncome)}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Divider line
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.24f))
                    )

                    // Expense Item
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = "Expenses",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "EXPENSES",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$currencySymbol${String.format("%,.1f", totalExpenses)}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Savings meter slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SAVED TARGETS METRIC: $currencySymbol${String.format("%,.0f", savings)}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val ratio = if (totalIncome > 0) (savings / totalIncome) * 100 else 0.0
                        Text(
                            text = "${String.format("%.1f", ratio)}%",
                            color = LightBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Visual slide track
                    val ratioF = if (totalIncome > 0) (savings / totalIncome).toFloat().coerceIn(0f, 1f) else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratioF)
                                .fillMaxHeight()
                                .background(LightBlue, RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }
    }
}


// --- DYNAMIC PREMIUM CHARTS ---

// 1. Pie spending chart (custom Canvas drawing)
@Composable
fun CategoryPieChart(
    modifier: Modifier = Modifier,
    categorySpending: Map<String, Double>,
    currencySymbol: String = "$"
) {
    val totalSpend = categorySpending.values.sum()
    if (totalSpend <= 0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MutedTextLight,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No expenses recorded yet for breakdown", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 13.sp)
            }
        }
        return
    }

    // Modern Fintech Color wheel palette
    val colors = listOf(
        Color(0xFF2563EB), // Primary Blue
        Color(0xFF10B981), // Green
        Color(0xFFF59E0B), // Orange
        Color(0xFFEF4444), // Red
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4), // Cyan
        Color(0xFF14B8A6), // Teal
        Color(0xFF6B7280)  // Gray
    )

    val listCategoriesBySpent = categorySpending.entries.sortedByDescending { it.value }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Doughnut Chart Draw
        Box(
            modifier = Modifier
                .size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                listCategoriesBySpent.forEachIndexed { index, entry ->
                    val sweepAngle = ((entry.value / totalSpend) * 360f).toFloat()
                    val color = colors[index % colors.size]
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 28f, cap = StrokeCap.Round),
                        size = Size(size.width - 28f, size.height - 28f),
                        topLeft = Offset(14f, 14f)
                    )
                    startAngle += sweepAngle
                }
            }
            // Central Info Label
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TOTAL SPENT",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "$currencySymbol${String.format("%.0f", totalSpend)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Multi-column Category Legends
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listCategoriesBySpent.take(5).forEachIndexed { index, entry ->
                val percentage = (entry.value / totalSpend) * 100
                val color = colors[index % colors.size]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = entry.key,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", percentage)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            if (listCategoriesBySpent.size > 5) {
                val otherSum = listCategoriesBySpent.drop(5).sumOf { it.value }
                val percentage = (otherSum / totalSpend) * 100
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Other",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", percentage)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


// 2. Wave Trend Chart with linear path gradients (Weekly balance simulation)
@Composable
fun WeeklyTrendLineChart(
    modifier: Modifier = Modifier,
    transactions: List<TransactionEntity>,
    currencySymbol: String = "$"
) {
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val points = remember(transactions) {
        // Generate nice mock weekly sequence or actual date metrics
        listOf(1300f, 1550f, 1250f, 1450f, 1800f, 1600f, 2100f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "WEEKLY NET INDEX",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Text(
                text = "Weekly Financial Performance +14% Week-over-Week",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SuccessGreen
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Graph canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxVal = points.maxOrNull() ?: 1f
                    val minVal = points.minOrNull() ?: 0f
                    val deltaVal = (maxVal - minVal).coerceAtLeast(1f)

                    val widthBetweenPoints = size.width / (points.size - 1)
                    val pointsDraw = points.mapIndexed { index, value ->
                        val x = index * widthBetweenPoints
                        val fraction = (value - minVal) / deltaVal
                        val y = size.height - (fraction * size.height * 0.75f) - (size.height * 0.1f)
                        Offset(x, y.toFloat())
                    }

                    // Draw line gradient path background
                    val pathGradient = Path().apply {
                        moveTo(0f, size.height)
                        pointsDraw.forEach { offset ->
                            lineTo(offset.x, offset.y)
                        }
                        lineTo(size.width, size.height)
                        close()
                    }

                    drawPath(
                        path = pathGradient,
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryBlue.copy(alpha = 0.25f), Color.Transparent),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height)
                        )
                    )

                    // Draw line curve
                    val curvePath = Path().apply {
                        moveTo(pointsDraw.first().x, pointsDraw.first().y)
                        for (i in 1 until pointsDraw.size) {
                            val pPrev = pointsDraw[i - 1]
                            val pCurr = pointsDraw[i]
                            val cp1 = Offset(pPrev.x + widthBetweenPoints / 2f, pPrev.y)
                            val cp2 = Offset(pCurr.x - widthBetweenPoints / 2f, pCurr.y)
                            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pCurr.x, pCurr.y)
                        }
                    }

                    drawPath(
                        path = curvePath,
                        color = PrimaryBlue,
                        style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw glowing highlight dots
                    pointsDraw.forEachIndexed { idx, point ->
                        if (idx == pointsDraw.size - 1) {
                            // Draw pulse glowing circle on the latest item
                            drawCircle(
                                color = LightBlue,
                                radius = 14f,
                                center = point
                            )
                            drawCircle(
                                color = PrimaryBlue,
                                radius = 7f,
                                center = point
                            )
                        } else {
                            drawCircle(
                                color = Color.White,
                                radius = 5f,
                                center = point
                            )
                            drawCircle(
                                color = PrimaryBlue,
                                radius = 5f,
                                center = point,
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // X-Axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                days.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}


// 3. Vertical double Bar chart (Income vs Expense)
@Composable
fun MonthlyBarChart(
    modifier: Modifier = Modifier,
    currencySymbol: String = "$"
) {
    // Simulated monthly comparison numbers (Mar, Apr, May)
    val months = listOf("Mar", "Apr", "May")
    val incomeVal = listOf(3800f, 4100f, 5350f)
    val expenseVal = listOf(2200f, 2650f, 1850f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MONTHLY INDEX COMPARISON",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryBlue))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Income", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ErrorRed))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Expense", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Graphical block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                months.forEachIndexed { idx, month ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .height(110.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Column side-by-side
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val incRatio = incomeVal[idx] / 6000f
                                val expRatio = expenseVal[idx] / 6000f

                                // Income Column
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .fillMaxHeight(incRatio)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(PrimaryBlue, AccentBlue)
                                            )
                                        )
                                )
                                // Expense Column
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .fillMaxHeight(expRatio)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(ErrorRed, Color(0xFFFF8B8B))
                                            )
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = month,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


// --- LEVEL XP PROFILE COMPONENT ---

@Composable
fun GamifiedLevelHeader(
    xp: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    // XP Calculation: Level = xp / 200 + 1. Remaining xp in level = xp % 200
    val level = (xp / 250) + 1
    val extraXp = xp % 250
    val xpProgress = extraXp.toFloat() / 250f
    val animatedProgress by animateFloatAsState(
        targetValue = xpProgress,
        animationSpec = tween(durationMillis = 800),
        label = "xp"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(LightBlue.copy(alpha = 0.5f), Color.White.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, PrimaryBlue.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L$level",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Wealth Level: Master",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Text(
                        text = "$xp XP overall • Next Level at ${level * 250} XP",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryBlue,
                trackColor = PrimaryBlue.copy(alpha = 0.15f)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Saving Streak fire Badge
        Box(
            modifier = Modifier
                .background(Color(0xFFFFF3CD), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFFFEBAA), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥")
                Spacer(modifier = Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${streak}D STREAK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF856404)
                    )
                }
            }
        }
    }
}
