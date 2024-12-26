import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

fun main() = adventWebSolver(Advent2024)

@OptIn(ExperimentalComposeUiApi::class)
private fun adventWebSolver(advent: Advent) {
  ComposeViewport(document.body!!) {
    AdventSolver(advent)
  }
}
