import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var solution by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
              solution = null
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
            val input = input
            scope.launch {
              try {
                coroutineScope {
                  withContext(Dispatchers.Default) {
                    val daySolution = day.solve(input)
                    solution = "Part 1: ${daySolution.part1}\nPart 2: ${daySolution.part2}"
                  }
                }
              } catch (e: AdventDay.Exception) {
                errorMessage = e.message
              } catch (e: Exception) {
                errorMessage = e.stackTraceToString()
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

      solution?.let {
        Text(it)
      }
      errorMessage?.let {
        Text(it, color = Color.Red)
      }

      Spacer(Modifier.height(24.dp))
    }
  }
}
