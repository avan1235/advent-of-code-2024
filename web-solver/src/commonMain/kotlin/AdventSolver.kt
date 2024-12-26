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
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

@Composable
internal fun AdventSolver(advent: Advent) {
  val yearStyle = AdventYearStyle.entries.random()
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
        Spacer(Modifier.height(16.dp))

        CompositionLocalProvider(
          LocalTextStyle provides MaterialTheme.typography.titleLarge,
        ) {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
          ) {
            Row(
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.Top,
            ) {
              Text(
                buildAnnotatedString {
                  withColorStyle(AdventBrightGreen) {
                    appendLine("Advent of Code")
                  }
                  withColorStyle(AdventBrightGreen.copy(alpha = 0.33f)) {
                    append(yearStyle.prefix)
                  }
                  withColorStyle(AdventBrightGreen) {
                    append(advent.year.toString())
                  }
                  withColorStyle(AdventBrightGreen.copy(alpha = 0.33f)) {
                    append(yearStyle.suffix)
                  }
                },
                textAlign = TextAlign.End
              )
              Text(
                buildAnnotatedString {
                  withColorStyle(AdventGreen) {
                    appendLine(" Solver")
                  }
                },
                textAlign = TextAlign.Start
              )
            }
          }
        }

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
              .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
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
              itemsIndexed(
                items = lines,
                key = { idx, _ -> idx },
                itemContent = { _, line ->
                  Text(line, style = MaterialTheme.typography.bodyMedium)
                }
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

        Spacer(Modifier.height(16.dp))
      }
    }
  }
}

private val TextBoxMinHeight: Dp = 120.dp
private val TextBoxMaxHeight: Dp = 480.dp
private val LineSpacingHeight: Dp = 4.dp

private const val NotSolvedDescription = "<not-solved>"

@Composable
private inline fun <R : Any> Builder.withColorStyle(
  color: Color,
  style: TextStyle = LocalTextStyle.current,
  block: Builder.() -> R
): R = withStyle(
  style.toSpanStyle().copy(color = color)
) { block() }

private enum class AdventYearStyle(val prefix: String, val suffix: String) {
  Clean("", ""),
  Json("{'year': ", "}"),
  Php("\$year = ", ";"),
  Yaml("year: ", "\n"),
  Python("year = ", "\n"),
  Haskell("λy.", ""),
  Call("y(", ")"),
  Mask("0xffff&", ""),
  C("int y=", ";"),
  Html("<y>", "</y>"),
  Comment("/*", "*/"),
  JS("var y=", ";"),
  ;
}
