import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun <T> Dropdown(
  preselected: T,
  options: List<T>,
  representation: (T) -> String,
  onOptionSelected: (T) -> Unit = {},
  modifier: Modifier = Modifier,
  offset: DpOffset = DpOffset.Zero,
  scrollState: ScrollState = rememberScrollState(),
  properties: PopupProperties = remember { PopupProperties() },
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(preselected) }

  Box(
    contentAlignment = Alignment.CenterStart,
    modifier = modifier
      .height(36.dp)
      .clip(RoundedCornerShape(4.dp))
      .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp))
      .runIf(!expanded) { clickable { expanded = true } },
  ) {
    Text(
      text = representation(selectedOption),
      fontSize = 14.sp,
      modifier = Modifier
        .padding(start = 16.dp, end = 32.dp)
    )
    val degree by animateFloatAsState(if (expanded) 180f else 0f)
    Icon(
      imageVector = Icons.Filled.ArrowDropDown,
      contentDescription = "contentDescription",
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .rotate(degree)
    )
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = modifier,
      offset = offset,
      scrollState = scrollState,
      properties = properties,
    ) {
      options.forEach { selectionOption ->
        DropdownMenuItem(
          modifier = Modifier
            .height(40.dp),
          onClick = {
            selectedOption = selectionOption
            expanded = false
            onOptionSelected(selectionOption)
          },
          text = {
            Text(text = representation(selectionOption))
          }
        )
      }
    }
  }
}
