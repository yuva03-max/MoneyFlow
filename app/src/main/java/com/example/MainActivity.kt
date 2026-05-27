package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.moneyflow.*
import com.example.ui.theme.MoneyFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MoneyFlowViewModel = viewModel()
            
            // Set the central custom MoneyFlow Theme, reactive to darkThemeEnabled toggle in real-time
            MoneyFlowTheme(darkTheme = viewModel.darkThemeEnabled) {
                // Central App state-based screen router
                when (viewModel.currentScreen) {
                    AppScreen.SPLASH -> SplashScreen()
                    
                    AppScreen.ONBOARDING -> OnboardingScreen(
                        page = viewModel.onboardingPage,
                        onNext = { viewModel.onboardingPage = (viewModel.onboardingPage + 1).coerceAtMost(2) },
                        onBack = { viewModel.onboardingPage = (viewModel.onboardingPage - 1).coerceAtLeast(0) },
                        onGetStarted = { viewModel.completeOnboarding() }
                    )
                    
                    AppScreen.LOGIN -> LoginScreen(
                        viewModel = viewModel,
                        onSignupRedirect = { viewModel.currentScreen = AppScreen.SIGNUP },
                        onForgotRedirect = { viewModel.currentScreen = AppScreen.FORGOT_PASSWORD }
                    )
                    
                    AppScreen.SIGNUP -> SignupScreen(
                        viewModel = viewModel,
                        onLoginRedirect = { viewModel.currentScreen = AppScreen.LOGIN }
                    )
                    
                    AppScreen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                        onBack = { viewModel.currentScreen = AppScreen.LOGIN }
                    )
                    
                    AppScreen.MAIN_APP -> MainAppContainer(viewModel = viewModel)
                }
            }
        }
    }
}
