package com.example.ui.moneyflow

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.api.SmartAdvice
import com.example.data.moneyflow.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Helper function to map categories to appropriate icons
fun getCategoryIcon(category: String, type: String): ImageVector {
    if (type == "INCOME") {
        return when (category) {
            "Salary" -> Icons.Default.Work
            "Freelance" -> Icons.Default.Computer
            "Business" -> Icons.Default.Storefront
            "Passive income" -> Icons.Default.TrendingUp
            else -> Icons.Default.AttachMoney
        }
    }
    return when (category) {
        "Food" -> Icons.Default.Restaurant
        "Shopping" -> Icons.Default.LocalMall
        "Transport" -> Icons.Default.DirectionsCar
        "Bills" -> Icons.Default.ReceiptLong
        "Education" -> Icons.Default.School
        "Health" -> Icons.Default.MedicalServices
        "Entertainment" -> Icons.Default.LocalPlay
        "Travel" -> Icons.Default.Flight
        else -> Icons.Default.Category
    }
}

// Helper to format date longs
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, PrimaryBlue, AccentBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Soft backgrounds circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha = 0.05f), radius = 350f, center = Offset(size.width * 0.1f, size.height * 0.2f))
            drawCircle(Color.White.copy(alpha = 0.05f), radius = 500f, center = Offset(size.width * 0.9f, size.height * 0.8f))
        }

        val infiniteTransition = rememberInfiniteTransition(label = "splash")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate"
        )
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutBack),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // MoneyFlow Emblem Circle
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(54.dp)
                        .rotate(rotation)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MoneyFlow",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI-Powered Wealth Intelligence",
                color = LightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = LightBlue,
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp
            )
        }
    }
}


// ==========================================
// 2. ONBOARDING SCREEN
// ==========================================
@Composable
fun OnboardingScreen(
    page: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onGetStarted: () -> Unit
) {
    val illustrations = listOf(
        Icons.Default.TrendingUp,
        Icons.Default.CrisisAlert,
        Icons.Default.Psychology
    )
    val titles = listOf(
        "Secure Expense Tracking",
        "Targeted Savings Goals",
        "AI Smart Insights"
    )
    val descriptions = listOf(
        "Easily organize budgets with dynamic category metrics. Establish structural rules and stay in ultimate control of cashflow pipelines.",
        "Create custom goals such as Laptop or Emergency Funds. Track exact saved percentages reactively and earn bonus level progression.",
        "Synthesize high-fidelity financial suggestions. Overspending forecasts and smarter savings hints fueled directly by Gemini."
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top branding label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MoneyFlow",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkBlue
                )
            }

            // Central Page Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                // Large styled illustration container
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .background(LightBlue.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(Brush.radialGradient(colors = listOf(LightBlue, Color.White)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = illustrations[page],
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = titles[page],
                    color = DarkText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = descriptions[page],
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Bottom Control Navigation Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == page) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (index == page) PrimaryBlue else Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (page > 0) {
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("onboarding_back_button")
                        ) {
                            Text(
                                "Back",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Button(
                        onClick = { if (page == 2) onGetStarted() else onNext() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("onboarding_next_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (page == 2) "Get Started" else "Next",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. AUTHENTICATION (LOGIN, SIGNUP, FORGOT)
// ==========================================
@Composable
fun LoginScreen(
    viewModel: MoneyFlowViewModel,
    onSignupRedirect: () -> Unit,
    onForgotRedirect: () -> Unit
) {
    var email by remember { mutableStateOf(viewModel.userEmail) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Brand Header
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(LightBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = DarkBlue
            )
            Text(
                text = "Secure Fintech Authorization Portal",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Form inputs
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember Me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = viewModel.rememberMe,
                        onCheckedChange = { viewModel.rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                    )
                    Text("Remember me", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                }
                TextButton(onClick = onForgotRedirect) {
                    Text("Forgot Password?", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Login Button
            Button(
                onClick = {
                    if (viewModel.performLogin(email, password)) {
                        Toast.makeText(context, "Welcome back, Authorized Secure Session verified!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unauthorized: Fill email & 4+ char password.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Secure Log In", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Login Button
            OutlinedButton(
                onClick = {
                    email = "yuvayyss@gmail.com"
                    viewModel.performLogin(email, "g_oauth_token_123456")
                    Toast.makeText(context, "Google OAuth Secure Session Injected!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_login_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Sign in with Google Account", color = DarkText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Signup Prompt
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onSignupRedirect) {
                    Text("Sign Up", color = PrimaryBlue, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}


@Composable
fun SignupScreen(
    viewModel: MoneyFlowViewModel,
    onLoginRedirect: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = DarkBlue
            )
            Text(
                text = "Register for premium financial tracking",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Create Secure Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (viewModel.performSignup(name, email, password)) {
                        Toast.makeText(context, "Account Successfully Registered! Welcome!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error: Standard fields and 6+ char password required.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Register Account", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have a workspace?", fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onLoginRedirect) {
                    Text("Secure Log In", color = PrimaryBlue, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}


@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Recover Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your verified email to receive a recovery link.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Registered Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        Toast.makeText(context, "Security Verification Email queued for dispatch!", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Please insert email", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Dispatch Recovery Link", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = onBack) {
                Text("Back to log in", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}


// ==========================================
// 4. MAIN APP CONTAINER (SCAFFOLD, TABS)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppContainer(viewModel: MoneyFlowViewModel) {
    val unreadNotifCount = viewModel.notifications.collectAsState().value.count { !it.isRead }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Standard Custom M3 Navigation Bar with Navigation Gesture Pill buffer padding
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    Triple(MainTab.HOME, "Home", Icons.Default.Home),
                    Triple(MainTab.ANALYTICS, "Analytics", Icons.Default.Analytics),
                    Triple(MainTab.BUDGET, "Budget", Icons.Default.Wallet),
                    Triple(MainTab.GOALS, "Goals", Icons.Default.CrisisAlert),
                    Triple(MainTab.SETTINGS, "Settings", Icons.Default.Settings)
                )

                tabs.forEach { (tab, label, icon) ->
                    val isSelected = viewModel.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.currentTab = tab },
                        icon = {
                            Box {
                                Icon(imageVector = icon, contentDescription = label)
                                if (tab == MainTab.HOME && unreadNotifCount > 0) {
                                    // Soft red badge for alert messages
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .align(Alignment.TopEnd)
                                            .background(ErrorRed, CircleShape)
                                    )
                                }
                            }
                        },
                        label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (viewModel.currentTab == MainTab.HOME) {
                FloatingActionButton(
                    onClick = {
                        viewModel.isExpenseForm = true
                        viewModel.showAddTransactionDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .testTag("add_transaction_fab")
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Switch Render
            when (viewModel.currentTab) {
                MainTab.HOME -> HomeScreenTab(viewModel)
                MainTab.ANALYTICS -> AnalyticsScreenTab(viewModel)
                MainTab.BUDGET -> BudgetScreenTab(viewModel)
                MainTab.GOALS -> GoalsScreenTab(viewModel)
                MainTab.SETTINGS -> SettingsScreenTab(viewModel)
            }

            // Global forms overlays Dialog popups
            if (viewModel.showAddTransactionDialog) {
                AddTransactionDialog(viewModel)
            }
            if (viewModel.showAddBudgetDialog) {
                AddBudgetDialog(viewModel)
            }
            if (viewModel.showAddGoalDialog) {
                AddGoalDialog(viewModel)
            }
            if (viewModel.showNotificationList) {
                NotificationOverlayDialog(viewModel)
            }
        }
    }
}


// ==========================================
// A. TAB SCREEN: HOME
// ==========================================
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTab(viewModel: MoneyFlowViewModel) {
    val txs by viewModel.transactions.collectAsState()
    val bgs by viewModel.budgets.collectAsState()
    val gls by viewModel.goals.collectAsState()
    val stats by viewModel.userStats.collectAsState()
    val noticeCount = viewModel.notifications.collectAsState().value.count { !it.isRead }

    // Aggregate summary numbers
    val totalIncome = txs.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val currentBalance = totalIncome - totalExpense
    val totalGoalSavings = gls.sumOf { it.savedAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Branding Header Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.userName.take(2).uppercase(),
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Good Morning,",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = viewModel.userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Notification Bell icon trigger
                Box {
                    IconButton(
                        onClick = {
                            viewModel.showNotificationList = true
                            viewModel.markAllNotificationsAsRead()
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .testTag("notification_bell_trigger")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (noticeCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.TopEnd)
                                .background(ErrorRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = noticeCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 1. Gamification XP Profile Panel
        item {
            GamifiedLevelHeader(xp = stats?.xp ?: 0, streak = stats?.streak ?: 0)
        }

        // 2. Main Balance and Income/Expense Card
        item {
            MainBalanceCard(
                totalBalance = currentBalance,
                totalIncome = totalIncome,
                totalExpenses = totalExpense,
                savings = totalGoalSavings,
                currencySymbol = viewModel.selectedCurrency,
                onAddExpenseClick = {
                    viewModel.isExpenseForm = true
                    viewModel.showAddTransactionDialog = true
                },
                onAddIncomeClick = {
                    viewModel.isExpenseForm = false
                    viewModel.showAddTransactionDialog = true
                }
            )
        }

        // --- QUICK ACTION FAB SIMULATION BUTTONS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.isExpenseForm = true
                        viewModel.showAddTransactionDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Expense", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Button(
                    onClick = {
                        viewModel.isExpenseForm = false
                        viewModel.showAddTransactionDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Income", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 3. AI Smart Insights Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪄")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "MoneyFlow AI Insights",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = DarkBlue,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Sparkle animate icon caller
                        IconButton(
                            onClick = { viewModel.triggerAiAnalysis() },
                            enabled = !viewModel.isAiLoading,
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White, CircleShape)
                                .testTag("trigger_ai_reanalysis")
                        ) {
                            Icon(
                                imageVector = if (viewModel.isAiLoading) Icons.Default.HourglassEmpty else Icons.Default.AutoAwesome,
                                contentDescription = "Query Gemini",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (viewModel.isAiLoading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Consulting Gemini Wealth models...", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DarkBlue)
                        }
                    } else {
                        val advice = viewModel.aiAdvice ?: SmartAdvice("💡 Real-time Insights Module Loaded", "Perform transactions to index suggestions here.", "🔮 Establish clean savings multipliers today!")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Part 1: Overspending Summary
                            Text(
                                text = advice.overspendingAlert,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            // Part 2: Actions
                            Text(
                                text = advice.savingsAdvice,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                            // Part 3: Playful Prediction
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = advice.futurePrediction,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Recent Transactions section header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT TRANSACTIONS",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                if (txs.isNotEmpty()) {
                    Text(
                        text = "Swipe/Tap to manage",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Transactions list representation
        if (txs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Transactions Saved",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    }
                }
            }
        } else {
            items(txs.take(10), key = { it.id }) { transaction ->
                val categoryColor = if (transaction.type == "INCOME") SuccessGreen else ErrorRed
                val currencySymbol = viewModel.selectedCurrency

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Circular icon category
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (transaction.type == "INCOME") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(transaction.category, transaction.type),
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = transaction.title,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${transaction.category} • ${formatDate(transaction.date)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Amount & Delete Button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${if (transaction.type == "INCOME") "+" else "-"} $currencySymbol${String.format("%,.2f", transaction.amount)}",
                                color = categoryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = { viewModel.deleteTransaction(transaction) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Gray.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// B. TAB SCREEN: ANALYTICS
// ==========================================
@Composable
fun AnalyticsScreenTab(viewModel: MoneyFlowViewModel) {
    val txs by viewModel.transactions.collectAsState()

    // Spending breakdown
    val categorySpendingMap = txs.filter { it.type == "EXPENSE" }
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

    val highestCategory = categorySpendingMap.maxByOrNull { it.value }?.key ?: "N/A"
    val highestCategoryAmt = categorySpendingMap.maxByOrNull { it.value }?.value ?: 0.0

    // Quick Finance Quality Indicator Index
    val totalInc = txs.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExp = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val healthScore = if (totalInc > 0) {
        ((totalInc - totalExp) / totalInc * 100).coerceIn(0.0, 100.0).toInt()
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.statusBarsPadding()) {
                Text("FINANCIAL INSIGHTS & METRICS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text("Interactive spend charts and predictive indexes.", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }

        // Dynamic Finance health Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(LightBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$healthScore%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "FINANCIAL DIAL SCORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when {
                                healthScore >= 60 -> "Exceptional Discipline! 🔥"
                                healthScore >= 30 -> "Stable Spending Buffer 👍"
                                else -> "High Cash Flow Burn Rate ⚠️"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Based on income retention metrics.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // 1. Spending Category distribution Pie Chart
        item {
            Column {
                Text(
                    text = "CATEGORY SPENDING BREAKDOWN",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryPieChart(
                    categorySpending = categorySpendingMap,
                    currencySymbol = viewModel.selectedCurrency
                )
            }
        }

        // Highest Category Highlights Banner
        if (highestCategory != "N/A") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🚨")
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HIGHEST RECORDED CATEGORY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                            Text(
                                text = "$highestCategory is your primary burning category: ${viewModel.selectedCurrency}${String.format("%,.0f", highestCategoryAmt)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Weekly Trend Balance Wave graph
        item {
            WeeklyTrendLineChart(transactions = txs, currencySymbol = viewModel.selectedCurrency)
        }

        // 3. Dual side-by-side comparative monthly bar graph
        item {
            MonthlyBarChart(currencySymbol = viewModel.selectedCurrency)
        }
    }
}


// ==========================================
// C. TAB SCREEN: BUDGET
// ==========================================
@Composable
fun BudgetScreenTab(viewModel: MoneyFlowViewModel) {
    val bgs by viewModel.budgets.collectAsState()
    val txs by viewModel.transactions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("BUDGET CAP MANAGER", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text("Avoid overspending by capping specific categories.", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.showAddBudgetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_budget_limit_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Limit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (bgs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No active budget caps. Click 'Add Limit'!", color = Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(bgs) { budget ->
                // Calculate spent in this current category limit
                val filteredTxs = txs.filter { it.type == "EXPENSE" && (budget.category == "ALL" || it.category == budget.category) }
                val currentSpent = filteredTxs.sumOf { it.amount }

                val ratio = (currentSpent / budget.limitAmount).toFloat()
                val isLimitExceeded = currentSpent > budget.limitAmount

                val strokeColor = if (isLimitExceeded) ErrorRed else SuccessGreen

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(strokeColor.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(budget.category, "EXPENSE"),
                                        contentDescription = null,
                                        tint = strokeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = budget.category,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            // Usage percent label
                            Box(
                                modifier = Modifier
                                    .background(strokeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${String.format("%.0f", ratio * 100)}% used",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = strokeColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Slider bar progress loader
                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = strokeColor,
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Spent: ${viewModel.selectedCurrency}${String.format("%,.1f", currentSpent)} / Limit: ${viewModel.selectedCurrency}${String.format("%,.0f", budget.limitAmount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )

                            Text(
                                text = if (isLimitExceeded) "Exceeded!" else "Remaining: ${viewModel.selectedCurrency}${String.format("%,.0f", (budget.limitAmount - currentSpent).coerceAtLeast(0.0))}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = strokeColor
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// D. TAB SCREEN: GOALS
// ==========================================
@Composable
fun GoalsScreenTab(viewModel: MoneyFlowViewModel) {
    val gls by viewModel.goals.collectAsState()
    val context = LocalContext.current

    var selectedGoalIdForContribution by remember { mutableStateOf<Int?>(null) }
    var contributionAmount by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SAVINGS TARGETS TRACKER", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text("Quicken emergency vault buffers interactively.", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.showAddGoalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_savings_goal_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Goal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (gls.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No active savings goals. Make one!", color = Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(gls) { goal ->
                val remainingToSave = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
                val ratio = (goal.savedAmount / goal.targetAmount).toFloat()
                val isUnlocked = ratio >= 1f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = goal.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Target Date: ${formatDate(goal.targetDate)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Goal target completion pill
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isUnlocked) SuccessGreen.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isUnlocked) "Achieved! 🏆" else "${String.format("%.1f", ratio * 100)}%",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isUnlocked) SuccessGreen else PrimaryBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Savings slider progress
                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isUnlocked) SuccessGreen else PrimaryBlue,
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saved: ${viewModel.selectedCurrency}${String.format("%,.0f2", goal.savedAmount)} / Target: ${viewModel.selectedCurrency}${String.format("%,.0f", goal.targetAmount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )

                            IconButton(onClick = { viewModel.deleteGoal(goal) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Goal", tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            }
                        }

                        // Contributions panel
                        Spacer(modifier = Modifier.height(6.dp))

                        if (selectedGoalIdForContribution == goal.id) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = contributionAmount,
                                    onValueChange = { contributionAmount = it },
                                    label = { Text("Add Amount") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                                )
                                Button(
                                    onClick = {
                                        val parseAmt = contributionAmount.toDoubleOrNull()
                                        if (parseAmt != null && parseAmt > 0) {
                                            viewModel.addSavingsGoalFunds(goal, parseAmt)
                                            contributionAmount = ""
                                            selectedGoalIdForContribution = null
                                            Toast.makeText(context, "Saved funds synchronized!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Add")
                                }
                            }
                        } else {
                            if (!isUnlocked) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.addSavingsGoalFunds(goal, 50.0) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("+ $50", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.addSavingsGoalFunds(goal, 100.0) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("+ $100", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { selectedGoalIdForContribution = goal.id },
                                        modifier = Modifier.weight(1.2f),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Custom", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// E. TAB SCREEN: SETTINGS
// ==========================================
@Composable
fun SettingsScreenTab(viewModel: MoneyFlowViewModel) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.statusBarsPadding()) {
                Text("MoneyFlow Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text("Customize currency, dark theme and profile metrics.", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }

        // Profile panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(LightBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.userName.take(2).uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = viewModel.userName, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(text = viewModel.userEmail, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Configuration Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {

                    // Currency choosing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Default Currency", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        val currencyOptions = listOf("$", "€", "£", "¥", "₹")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            currencyOptions.forEach { cur ->
                                val sel = viewModel.selectedCurrency == cur
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (sel) PrimaryBlue else Color.LightGray.copy(alpha = 0.2f))
                                        .clickable { viewModel.selectedCurrency = cur },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cur,
                                        fontWeight = FontWeight.Black,
                                        color = if (sel) Color.White else DarkText,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                    // Dark mode toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Dark Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Switch(
                            checked = viewModel.darkThemeEnabled,
                            onCheckedChange = { viewModel.toggleDarkMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                    // Language choosing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("App Language", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        var showLanDialog by remember { mutableStateOf(false) }
                        Text(
                            text = viewModel.selectedLanguage,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable { showLanDialog = true }
                                .padding(6.dp)
                        )

                        if (showLanDialog) {
                            val languages = listOf("English", "Español", "Français", "Deutsch")
                            AlertDialog(
                                onDismissRequest = { showLanDialog = false },
                                title = { Text("Choose Language") },
                                text = {
                                    Column {
                                        languages.forEach { lan ->
                                            Text(
                                                text = lan,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.selectedLanguage = lan
                                                        showLanDialog = false
                                                    }
                                                    .padding(vertical = 12.dp),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                },
                                confirmButton = {}
                            )
                        }
                    }
                }
            }
        }

        // Action Utilities Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {

                    // CSV Export
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val path = viewModel.exportDataAsCSV()
                                if (path.isNotEmpty()) {
                                    Toast.makeText(context, "CSV Export queued! Path saved safely.", Toast.LENGTH_LONG).show()
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Export Database CSV", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Stores all expense history in a spreadsheet file.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                    // Backup Restoration
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.triggerBackupRestoreSimulation() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cloud Backup & Restoration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Synchronize and secure settings instantly.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Logout button
        item {
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Log Out", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}


// ==========================================
// FORM DIALOGS: ADD EXPENSE / ADD INCOME
// ==========================================
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(viewModel: MoneyFlowViewModel) {
    val categoriesExpense = listOf("Food", "Shopping", "Transport", "Bills", "Education", "Health", "Entertainment", "Travel", "Other")
    val categoriesIncome = listOf("Salary", "Freelance", "Business", "Passive income", "Other")
    val currentCategories = if (viewModel.isExpenseForm) categoriesExpense else categoriesIncome

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { viewModel.showAddTransactionDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 32.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Header dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (viewModel.isExpenseForm) "Add Expense Detail" else "Record Income",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { viewModel.showAddTransactionDialog = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Form")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switch Expense / Income
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewModel.isExpenseForm) ErrorRed else Color.Transparent)
                            .clickable {
                                viewModel.isExpenseForm = true
                                viewModel.transCategory = categoriesExpense.first()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Expense",
                            color = if (viewModel.isExpenseForm) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!viewModel.isExpenseForm) SuccessGreen else Color.Transparent)
                            .clickable {
                                viewModel.isExpenseForm = false
                                viewModel.transCategory = categoriesIncome.first()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Income",
                            color = if (!viewModel.isExpenseForm) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title Input
                OutlinedTextField(
                    value = viewModel.transTitle,
                    onValueChange = { viewModel.transTitle = it },
                    label = { Text("Transaction Title (e.g. Starbucks Mocha)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trans_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount input
                OutlinedTextField(
                    value = viewModel.transAmount,
                    onValueChange = { viewModel.transAmount = it },
                    label = { Text("Amount value") },
                    leadingIcon = { Text(viewModel.selectedCurrency, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trans_amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Category choosing Header
                Text(
                    "CHOOSE CATEGORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Category scroll Row options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentCategories.forEach { cat ->
                        val sel = viewModel.transCategory == cat
                        val selectColor = if (viewModel.isExpenseForm) ErrorRed else SuccessGreen
                        Box(
                            modifier = Modifier
                                .border(
                                    BorderStroke(1.dp, if (sel) selectColor else Color.LightGray),
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    if (sel) selectColor.copy(alpha = 0.12f) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.transCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = getCategoryIcon(cat, if (viewModel.isExpenseForm) "EXPENSE" else "INCOME"),
                                    contentDescription = null,
                                    tint = if (sel) selectColor else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sel) selectColor else Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Payment Method choose select row (Cash, Card, Transfer, PayPal)
                Text(
                    "PAYMENT METHOD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val ways = listOf("Card", "Cash", "Bank Transfer", "PayPal")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ways.forEach { py ->
                        val sel = viewModel.transPaymentMethod == py
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    BorderStroke(1.dp, if (sel) PrimaryBlue else Color.LightGray),
                                    RoundedCornerShape(10.dp)
                                )
                                .background(
                                    if (sel) PrimaryBlue.copy(alpha = 0.12f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.transPaymentMethod = py }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = py,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (sel) PrimaryBlue else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker trigger button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val picked = Calendar
                                        .getInstance()
                                        .apply {
                                            set(Calendar.YEAR, year)
                                            set(Calendar.MONTH, month)
                                            set(Calendar.DAY_OF_MONTH, day)
                                        }
                                    viewModel.transDate = picked.timeInMillis
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Date: ${formatDate(viewModel.transDate)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mock Bill upload layout
                Column {
                    Text(
                        "ATTACH BILL DOCUMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.transBillImageUri == null) {
                        OutlinedButton(
                            onClick = {
                                // Simulate photo selection
                                viewModel.transBillImageUri = "sim_attachment_file_path"
                                Toast.makeText(context, "Bill document attached successfully!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("attach_bill_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Capture or Upload Bill paper", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SuccessGreen, RoundedCornerShape(12.dp))
                                .background(SuccessGreen.copy(alpha = 0.08f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("bill_receipt_thumbnail.png attached", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            IconButton(onClick = { viewModel.transBillImageUri = null }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Document", tint = ErrorRed)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes Input
                OutlinedTextField(
                    value = viewModel.transNotes,
                    onValueChange = { viewModel.transNotes = it },
                    label = { Text("Transaction Notes/Memo (Optional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save button
                Button(
                    onClick = { viewModel.addTransaction() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_transaction_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Record", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}


// ==========================================
// FORM DIALOGS: BUDGETS LIMIT
// ==========================================
@Composable
fun AddBudgetDialog(viewModel: MoneyFlowViewModel) {
    val optionsCategoryExpense = listOf("Food", "Shopping", "Transport", "Bills", "Education", "Health", "Entertainment", "Travel")

    Dialog(onDismissRequest = { viewModel.showAddBudgetDialog = false }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set Category Limit", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    IconButton(onClick = { viewModel.showAddBudgetDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Choose category title
                Text("CHOOSE CATEGORY CAP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    optionsCategoryExpense.forEach { category ->
                        val isSel = viewModel.budgetCategory == category
                        Box(
                            modifier = Modifier
                                .border(1.dp, if (isSel) PrimaryBlue else Color.LightGray, RoundedCornerShape(10.dp))
                                .background(if (isSel) PrimaryBlue.copy(alpha = 0.12f) else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { viewModel.budgetCategory = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(category, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSel) PrimaryBlue else Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.budgetLimitAmount,
                    onValueChange = { viewModel.budgetLimitAmount = it },
                    label = { Text("Limit cap amount") },
                    leadingIcon = { Text(viewModel.selectedCurrency, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.addBudget() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_budget_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Limit Cap", fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}


// ==========================================
// FORM DIALOGS: SAVINGS GOAL
// ==========================================
@Composable
fun AddGoalDialog(viewModel: MoneyFlowViewModel) {
    val context = LocalContext.current

    val templates = listOf(
        Pair("Laptop Fund", 2000.0),
        Pair("Bike Fund", 5000.0),
        Pair("Emergency Fund", 8000.0),
        Pair("Vacation Fund", 3500.0)
    )

    Dialog(onDismissRequest = { viewModel.showAddGoalDialog = false }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Savings Goal", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    IconButton(onClick = { viewModel.showAddGoalDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Suggestion goals template chips
                Text("POPULAR TEMPLATES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    templates.forEach { (tplName, tplAmt) ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.goalName = tplName
                                    viewModel.goalTargetAmount = tplAmt.toString()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(tplName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                OutlinedTextField(
                    value = viewModel.goalName,
                    onValueChange = { viewModel.goalName = it },
                    label = { Text("Savings Goal Title") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Target Amount Input
                OutlinedTextField(
                    value = viewModel.goalTargetAmount,
                    onValueChange = { viewModel.goalTargetAmount = it },
                    label = { Text("Target savings cap amount") },
                    leadingIcon = { Text(viewModel.selectedCurrency, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_target_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date selecting trigger
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, yr, mn, dy ->
                                    val pik = Calendar
                                        .getInstance()
                                        .apply {
                                            set(Calendar.YEAR, yr)
                                            set(Calendar.MONTH, mn)
                                            set(Calendar.DAY_OF_MONTH, dy)
                                        }
                                    viewModel.goalTargetDate = pik.timeInMillis
                                },
                                c.get(Calendar.YEAR) + 1,
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target Date: ${formatDate(viewModel.goalTargetDate)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.addGoal() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_savings_goal_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Savings Goal", fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}


// ==========================================
// NOTIFICATIONS DIALOG OVERLAY
// ==========================================
@Composable
fun NotificationOverlayDialog(viewModel: MoneyFlowViewModel) {
    val notices by viewModel.notifications.collectAsState()

    Dialog(onDismissRequest = { viewModel.showNotificationList = false }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Finance Notifications", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = { viewModel.showNotificationList = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (notices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications", fontWeight = FontWeight.Bold, color = Color.LightGray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notices) { item ->
                            val iconVal = when (item.type) {
                                "BUDGET_ALERT" -> Icons.Default.Dangerous
                                "BILL_REMINDER" -> Icons.Default.Alarm
                                "SAVINGS_MILESTONE" -> Icons.Default.Stars
                                "CHALLENGE" -> Icons.Default.Casino
                                else -> Icons.Default.VerifiedUser
                            }
                            val iconTint = when (item.type) {
                                "BUDGET_ALERT" -> ErrorRed
                                "BILL_REMINDER" -> Color(0xFFFF9F43)
                                "SAVINGS_MILESTONE" -> SuccessGreen
                                else -> PrimaryBlue
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = iconVal, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(item.message, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
                                    }
                                    IconButton(onClick = { viewModel.deleteNotification(item.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Notice", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
