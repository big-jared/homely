import screen.SignInScreen
import service.ApplicationScreenModel
import service.applicationModule
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.materialkolor.DynamicMaterialTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.logger.Level

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            userModule,
            applicationModule,
        )
        printLogger(Level.DEBUG)
    }) {
        val applicationScreenModel = koinInject<ApplicationScreenModel>()
        val signedIn = applicationScreenModel.isSignedIn.collectAsState(false)
        
        DynamicMaterialTheme(
            seedColor = applicationScreenModel.seedColor.value,
            style = applicationScreenModel.theme.value
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Navigator(SignInScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}

abstract class HomelyScreen: Screen {
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








//            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                FilledTonalButton(onClick = {
//                    seedColor = Color.hsv(
//                        Random.nextDouble(360.0).toFloat(),
//                        Random.nextDouble(1.0).toFloat(),
//                        Random.nextDouble(1.0).toFloat()
//                    )
//                }) {
//                    Text("Change color")
//                }
//                FilledTonalButton(onClick = {
//                    theme = PaletteStyle.entries.toTypedArray().random()
//                }) {
//                    Text("Change theme")
//                }
//                FilledTonalButton(onClick = { showContent = !showContent }) {
//                    Text("Click me!")
//                }
//                AnimatedVisibility(showContent) {
//                    val greeting = remember { Greeting().greet() }
//                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                        Image(painterResource(Res.drawable.compose_multiplatform), null)
//                        Text("Compose: $greeting")
//                    }
//                }
//            }