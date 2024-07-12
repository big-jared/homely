import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import application.presentation.App
import kotlinx.coroutines.runBlocking

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Homely",
    ) {
        App()
        
        runBlocking {  }
    }
}