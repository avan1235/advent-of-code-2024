import DynamicColumnRowScope.ColumnScope
import DynamicColumnRowScope.RowScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ColumnScope as AndroidXColumnScope
import androidx.compose.foundation.layout.RowScope as AndroidXRowScope

internal sealed class DynamicColumnRowScope {
  data class ColumnScope(val scope: AndroidXColumnScope) : DynamicColumnRowScope()
  data class RowScope(val scope: AndroidXRowScope) : DynamicColumnRowScope()
}

@Composable
internal inline fun DynamicColumnRow(
  horizontal: Boolean,
  modifier: Modifier = Modifier,
  content: @Composable DynamicColumnRowScope.() -> Unit,
) {
  when {
    horizontal -> Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier,
    ) {
      content(RowScope(this))
    }

    else -> Column(
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
      modifier = modifier,
    ) {
      content(ColumnScope(this))
    }
  }
}
