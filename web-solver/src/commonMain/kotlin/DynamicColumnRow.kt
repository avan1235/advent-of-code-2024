import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal inline fun DynamicColumnRow(
  horizontal: Boolean,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  when {
    horizontal -> Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier,
    ) {
      content()
    }

    else -> Column(
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.Center,
      modifier = modifier,
    ) {
      content()
    }
  }
}
