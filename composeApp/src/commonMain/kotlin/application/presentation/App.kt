package application.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import application.di.applicationModule
import application.domain.ApplicationScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.darken
import common.FullScreenProgressIndicator
import course.di.coursesModule
import family.di.familyModule
import homely.composeapp.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import landing.di.authModule
import landing.presentation.LandingScreen
import onboarding.di.onboardingModule
import onboarding.presentation.OnboardingScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.logger.Level
import state.di.stateModule
import subscription.di.subscriptionModule

val primaryGreen = Color(0xFF4db092)
val primaryContainerGreen = Color(0xFFC9E4DD)
val onPrimary = Color(0xFFF6F6F6)

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            authModule,
            applicationModule,
            onboardingModule,
            familyModule,
            stateModule,
            coursesModule,
            subscriptionModule,
        )
        printLogger(Level.DEBUG)
    }) {
        val applicationScreenModel = koinInject<ApplicationScreenModel>()

        val initialized = applicationScreenModel.initialized.collectAsState(false).value
        val signedIn = applicationScreenModel.isSignedIn.collectAsState(false).value

        if (!initialized) {
            FullScreenProgressIndicator()
        } else {
            LightTheme {
                BottomSheetNavigator(
                    sheetShape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    ),
                    sheetBackgroundColor = MaterialTheme.colorScheme.background
                ) {
                    Navigator(if (signedIn) OnboardingScreen() else LandingScreen()) { navigator ->
                        LightTheme {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                SlideTransition(navigator)
                            }
                        }
                    }
                }
            }
        }
    }
}

interface AuthenticatedScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val appScreenModel = koinInject<ApplicationScreenModel>()

        LaunchedEffect(null) {
            appScreenModel.isSignedIn.collectLatest { signedIn ->
                val screenToStart = when (signedIn) {
                    true -> OnboardingScreen()
                    false -> LandingScreen()
                }

                navigator.replaceAll(screenToStart)
            }
        }

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
        displayLarge = displayLarge.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
        displayMedium = displayMedium.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
        displaySmall = displaySmall.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
        headlineLarge = headlineLarge.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        ),
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

@Composable
fun DarkTheme(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    DynamicMaterialTheme(
        seedColor = primaryGreen.darken(),
        useDarkTheme = true,
        style = PaletteStyle.Rainbow,
        typography = FiraTypography()
    ) {
        content()
    }
}

@Composable
fun LightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = primaryGreen,
            onPrimary = onPrimary,
        ),
        typography = FiraTypography(),
    ) {
        content()
    }
}
