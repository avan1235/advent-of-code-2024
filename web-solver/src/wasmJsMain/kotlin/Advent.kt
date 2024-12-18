import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  ComposeViewport(document.body!!) {
    var input by remember { mutableStateOf("") }
    val days = remember { adventDays() }
    var selectedDay by remember { mutableStateOf(days.first()) }
    val scope = rememberCoroutineScope()
    var part1 by remember { mutableStateOf<String?>(null) }
    var part2 by remember { mutableStateOf<String?>(null) }
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp),
    ) {
      Spacer(Modifier.height(24.dp))

      Dropdown(
        preselected = selectedDay,
        onOptionSelected = {
          if (selectedDay != it) {
            part1 = null
            part2 = null
            selectedDay = it
          }
        },
        options = days,
        representation = { "Day ${it.n}" },
        modifier = Modifier.heightIn(max = 380.dp)
      )

      TextField(
        value = input,
        onValueChange = { input = it },
        modifier = Modifier.heightIn(max = 540.dp)
      )

      Button(
        onClick = {
          val day = selectedDay
          val input = input.trim()
          scope.launch {
            day.solve(input)
            part1 = day.lines[0]
            part2 = day.lines[1]
          }
        }
      ) {
        Text("Solve")
      }

      part1?.let {
        Text("Part 1: $it")
      }

      part2?.let {
        Text("Part 2: $it")
      }

      Spacer(Modifier.height(24.dp))
    }
  }
}
