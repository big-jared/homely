package application.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import application.di.applicationModule
import application.domain.ApplicationScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.materialkolor.DynamicMaterialTheme
import common.FullScreenProgressIndicator
import dashboard.presentation.DashboardScreen
import homely.composeapp.generated.resources.*
import homely.composeapp.generated.resources.Res
import homely.composeapp.generated.resources.firacode_bold
import homely.composeapp.generated.resources.firacode_light
import homely.composeapp.generated.resources.firacode_medium
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import landing.di.authModule
import landing.presentation.LandingScreen
import onboarding.data.OnboardingScreen
import onboarding.di.onboardingModule
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.logger.Level
import subscription.di.subscriptionModule

val primaryGreen = Color(0xFF4db092)
val onPrimary = Color(0xFFF6F6F6)

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            authModule,
            applicationModule,
            onboardingModule,
            subscriptionModule,
        )
        printLogger(Level.DEBUG)
    }) {
        val applicationScreenModel = koinInject<ApplicationScreenModel>()

        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = primaryGreen,
                onPrimary = onPrimary,
            ),
            typography = FiraTypography(),
        ) {
            val initialized = applicationScreenModel.initialized.collectAsState(false).value
            val signedIn = applicationScreenModel.isSignedIn.collectAsState(false).value

            if (!initialized) {
                FullScreenProgressIndicator()
            } else {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigator(if(signedIn) OnboardingScreen() else LandingScreen()) { navigator ->
                        SlideTransition(navigator)

//                        LaunchedEffect(null) {
//                            applicationScreenModel.isSignedIn.collectLatest { signedIn ->
//                                val screenToStart = when (signedIn) {
//                                    true -> OnboardingScreen()
//                                    false -> LandingScreen()
//                                }
//
//                                // FIXME
//                                try {
//                                    navigator.replaceAll(screenToStart)
//                                } catch (e: Exception) {}
//                            }
//                        }
                    }
                }
            }
        }
    }
}

interface AuthenticatedScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val appScreenModel = koinInject<ApplicationScreenModel>()

        ScreenContent()
    }

    @Composable
    fun ScreenContent()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FiraFontFamily() = FontFamily(
    Font(Res.font.firacode_light, FontWeight.Light),
    Font(Res.font.firacode_medium, FontWeight.Normal),
    Font(Res.font.firacode_medium, FontWeight.Medium),
    Font(Res.font.firacode_bold, FontWeight.Bold),
    Font(Res.font.firacode_retina, FontWeight.Thin),
)

@Composable
fun FiraTypography() = Typography().run {
    val fontFamily = FiraFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        displayMedium = displayMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        displaySmall = displaySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        headlineMedium = headlineMedium.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleMedium = titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleSmall = titleSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodySmall = bodySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelLarge = labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelMedium = labelMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelSmall = labelSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium)
    )
}

abstract class HomelyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val applicationScreenModel = koinInject<ApplicationScreenModel>()

        applicationScreenModel.isSignedIn.collectAsState(false)

        Column(modifier = Modifier.fillMaxSize()) {
            ScreenContent()
        }
    }

    @Composable
    abstract fun ColumnScope.ScreenContent()
}