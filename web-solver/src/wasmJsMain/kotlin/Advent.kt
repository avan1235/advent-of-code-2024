import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

fun main() = adventWebSolver(Advent2024)

@OptIn(ExperimentalComposeUiApi::class)
private fun adventWebSolver(advent: Advent) {
  ComposeViewport(document.body!!) {
    val days = remember { advent.days }
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(days.first()) }
    var solution by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showLog by remember { mutableStateOf(false) }
    var log by remember { mutableStateOf(StringBuilder(), policy = neverEqualPolicy()) }
    var runningJob by remember { mutableStateOf<Job?>(null) }

    fun cancelRunningJob() {
      runningJob?.cancel()
      solution = null
      errorMessage = null
      log = log.clear()
      input = ""
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp),
    ) {
      Spacer(Modifier.height(24.dp))

      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Dropdown(
            preselected = selectedDay,
            onOptionSelected = {
              if (selectedDay != it) {
                cancelRunningJob()
                selectedDay = it
              }
            },
            options = days,
            representation = { "Day ${it.n}" },
            modifier = Modifier.heightIn(max = 380.dp)
          )
          val uriHandler = LocalUriHandler.current
          OutlinedButton(
            onClick = {
              uriHandler.openUri("/playground.html?year=${advent.year}&day=${selectedDay.n}")
            },
          ) {
            Text("Go to Solution")
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Checkbox(
              checked = showLog,
              onCheckedChange = { showLog = it },
            )
            Text("Show Log")
          }
        }
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Button(
            onClick = {
              val day = selectedDay
              val input = input
              runningJob = scope.launch(Dispatchers.Default) {
                try {
                  coroutineScope {
                    val debug = Channel<String>()
                    launch {
                      for (line in debug) {
                        log = log.appendLine(line)
                      }
                    }
                    day.SolveContext(debug).use { context ->
                      with(day) { context.solve(input) }.run {
                        solution = "Part 1: ${part1}\nPart 2: $part2"
                      }
                    }
                  }
                } catch (e: AdventDay.Exception) {
                  errorMessage = e.message
                } catch (e: CancellationException) {
                  throw e
                } catch (e: Exception) {
                  errorMessage = e.stackTraceToString()
                } finally {
                  runningJob = null
                }
              }
            },
            enabled = runningJob == null,
          ) {
            Text("Solve")
          }
          OutlinedButton(
            onClick = ::cancelRunningJob,
            enabled = runningJob != null,
          ) {
            Text("Cancel")
          }
        }
      }

      Column {
        TextField(
          value = input,
          onValueChange = { input = it },
          colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
          ),
          shape = RectangleShape,
          modifier = Modifier
            .heightIn(
              min = TextBoxMinHeight,
              max = TextBoxMaxHeight
            )
            .fillMaxWidth()
        )
        AnimatedVisibility(visible = runningJob != null) {
          LinearProgressIndicator(Modifier.fillMaxWidth())
        }
      }

      solution?.let {
        Text(it)
      }

      errorMessage?.let {
        Text(it, color = Color.Red)
      }

      AnimatedVisibility(
        visible = showLog
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(TextBoxMaxHeight)
        ) {
          val listState = rememberLazyListState()
          val lines = log.lines()
          LaunchedEffect(lines.size) {
            listState.animateScrollToItem(lines.lastIndex)
          }
          LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .fillMaxSize()
              .border(Dp.Hairline, Color.LightGray, RectangleShape)
              .padding(horizontal = LineSpacingHeight),
          ) {
            itemsIndexed(
              items = lines,
              key = { idx, _ -> idx },
              itemContent = { _, line -> Text(line) }
            )
          }
          VerticalScrollbar(
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(listState),
          )
        }
      }

      Spacer(Modifier.height(24.dp))
    }
  }
}

private val TextBoxMinHeight: Dp = 120.dp
private val TextBoxMaxHeight: Dp = 480.dp
private val LineSpacingHeight: Dp = 4.dp
