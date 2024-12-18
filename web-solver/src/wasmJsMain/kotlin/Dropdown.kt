import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier = Modifier
      .size(250.dp, 32.dp)
      .clip(RoundedCornerShape(4.dp))
      .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp))
      .clickable { expanded = !expanded },
  ) {
    Text(
      text = representation(selectedOption),
      fontSize = 14.sp,
      modifier = Modifier.padding(start = 10.dp)
    )
    Icon(
      Icons.Filled.ArrowDropDown, "contentDescription",
      Modifier.align(Alignment.CenterEnd)
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
          onClick = {
            selectedOption = selectionOption
            expanded = false
            onOptionSelected(selectionOption)
          }
        ) {
          Text(text = representation(selectionOption))
        }
      }
    }
  }
}
