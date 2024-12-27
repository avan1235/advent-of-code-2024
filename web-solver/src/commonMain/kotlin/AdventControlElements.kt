import DynamicColumnRowScope.ColumnScope
import DynamicColumnRowScope.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job

@Composable
internal inline fun DynamicColumnRowScope.AdventControlElements(
  horizontal: Boolean,
  selectedDay: AdventDay,
  crossinline onSelectedDayChange: (AdventDay) -> Unit,
  advent: Advent,
  days: List<AdventDay>,
  showLog: Boolean,
  crossinline onShowLogChange: (Boolean) -> Unit,
  crossinline cancelRunningJob: () -> Unit,
  crossinline onSolve: () -> Unit,
  runningJob: Job?,
) {
  AdventDropdown(
    preselected = selectedDay,
    onOptionSelected = {
      if (selectedDay != it) {
        cancelRunningJob()
        onSelectedDayChange(it)
      }
    },
    options = days,
    representation = { "Day ${it.n}" },
    modifier = Modifier
      .fillMaxWidthIf(!horizontal)
      .heightIn(max = 380.dp)
  )
  val uriHandler = LocalUriHandler.current
  TextButton(
    shape = MaterialTheme.shapes.medium,
    modifier = Modifier.fillMaxWidthIf(!horizontal),
    onClick = {
      uriHandler.openUri("/playground.html?year=${advent.year}&day=${selectedDay.n}")
    },
  ) {
    Text("Go to Solution")
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidthIf(!horizontal),
  ) {
    Checkbox(
      checked = showLog,
      onCheckedChange = { onShowLogChange(it) },
    )
    Text(
      text = "Show Log",
      fontSize = MaterialTheme.typography.labelLarge.fontSize,
      lineHeight = MaterialTheme.typography.labelLarge.fontSize,
    )
  }
  when (this) {
    is ColumnScope -> {}
    is RowScope -> with(scope) {
      Spacer(modifier = Modifier.weight(1f))
    }
  }
  OutlinedButton(
    shape = MaterialTheme.shapes.medium,
    onClick = { if (runningJob == null) onSolve() else cancelRunningJob() },
    modifier = Modifier.fillMaxWidthIf(!horizontal),
  ) {
    Text(if (runningJob == null) "Solve" else "Cancel")
  }
}

private fun Modifier.fillMaxWidthIf(condition: Boolean): Modifier =
  if (condition) fillMaxWidth() else this
