import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

@Composable
internal fun AdventSolver(advent: Advent) {
  AdventTheme {
    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      var input by remember { mutableStateOf("") }
      var solution by remember { mutableStateOf<String?>(null) }
      var showLog by remember { mutableStateOf(false) }
      var log by remember { mutableStateOf(StringBuilder(), policy = neverEqualPolicy()) }
      var runningJob by remember { mutableStateOf<Job?>(null) }
      val days = remember { advent.days }
      val scope = rememberCoroutineScope()
      var selectedDay by remember { mutableStateOf(days.first()) }
      LaunchedEffect(selectedDay) {
        document.title = "Day ${selectedDay.n} - Advent of Code 2024 | Solver"
      }
      var horizontal by remember { mutableStateOf(true) }

      fun cancelRunningJob() {
        runningJob?.cancel()
        solution = null
        log = log.clear()
        input = ""
      }

      fun onSolve() {
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
                  solution = "Part 1: ${part1 ?: "<not-solved>"}\nPart 2: ${part2 ?: NotSolvedDescription}"
                }
              }
            }
          } catch (e: AdventDay.Exception) {
            log = log.appendLine(e.message)
          } catch (e: CancellationException) {
            throw e
          } catch (e: Exception) {
            log = log.appendLine(e.stackTraceToString())
          } finally {
            runningJob = null
          }
        }
      }

      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp)
          .onGloballyPositioned { horizontal = it.size.width >= 1200 },
      ) {
        Spacer(Modifier.height(24.dp))

        DynamicColumnRow(horizontal) {
          ControlElements(
            horizontal = horizontal,
            selectedDay = selectedDay,
            onSelectedDayChange = { selectedDay = it },
            advent = advent,
            days = days,
            showLog = showLog,
            onShowLogChange = { showLog = it },
            cancelRunningJob = ::cancelRunningJob,
            onSolve = ::onSolve,
            runningJob = runningJob,
          )
        }

        Column {
          TextField(
            value = input,
            onValueChange = { input = it },
            colors = TextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = RectangleShape,
            modifier = Modifier
              .heightIn(
                min = TextBoxMinHeight,
                max = TextBoxMaxHeight
              )
              .fillMaxWidth()
          )
          AnimatedVisibility(
            visible = runningJob != null,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
          }
        }

        solution?.let {
          Text(it)
        }

        AnimatedVisibility(
          visible = showLog,
          enter = expandVertically(),
          exit = shrinkVertically()
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
              modifier = Modifier.fillMaxSize()
                .border(1.dp, AdventWhite, RectangleShape)
                .padding(horizontal = LineSpacingHeight),
            ) {
              itemsIndexed(items = lines, key = { idx, _ -> idx }, itemContent = { _, line -> Text(line) })
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
}

private val TextBoxMinHeight: Dp = 120.dp
private val TextBoxMaxHeight: Dp = 480.dp
private val LineSpacingHeight: Dp = 4.dp

private const val NotSolvedDescription = "<not-solved>"
