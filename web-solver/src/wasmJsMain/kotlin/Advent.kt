import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  ComposeViewport(document.body!!) {
    var input by remember { mutableStateOf("") }
    val days = remember { Advent2024.days }
    var selectedDay by remember { mutableStateOf(days.first()) }
    val scope = rememberCoroutineScope()
    var part1 by remember { mutableStateOf<String?>(null) }
    var part2 by remember { mutableStateOf<String?>(null) }
    var solving by remember { mutableStateOf(false) }
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp),
    ) {
      Spacer(Modifier.height(24.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
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

        Button(
          onClick = {
            solving = true
            val day = selectedDay
            val input = input.trim()
            scope.launch {
              try {
                coroutineScope {
                  withContext(Dispatchers.Default) {
                    day.solve(input)
                    part1 = day.lines[0]
                    part2 = day.lines[1]
                  }
                }
              } finally {
                solving = false
              }
            }
          },
          enabled = !solving,
        ) {
          Text("Solve")
        }
        AnimatedVisibility(visible = solving) {
          CircularProgressIndicator(Modifier.size(32.dp))
        }
      }

      TextField(
        value = input,
        onValueChange = { input = it },
        colors = TextFieldDefaults.textFieldColors(
          backgroundColor = MaterialTheme.colors.surface,
        ),
        shape = RectangleShape,
        modifier = Modifier.heightIn(max = 540.dp).fillMaxWidth()
      )

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
