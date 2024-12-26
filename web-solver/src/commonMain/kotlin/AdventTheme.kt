import advent_of_code_2024.web_solver.generated.resources.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.Font

@Composable
internal fun AdventTheme(content: @Composable () -> Unit) {
  val fontFamily = SourceCodeProFontFamily()

  MaterialTheme(
    colorScheme = darkColorScheme(
      primary = green,
      background = blue,
      surface = blue,
      surfaceContainer = black,
    ),
    shapes = Shapes(
      extraSmall = CutCornerShape(0.dp),
      small = CutCornerShape(0.dp),
      medium = CutCornerShape(0.dp),
      large = CutCornerShape(0.dp),
      extraLarge = CutCornerShape(0.dp),
    ),
    typography = MaterialTheme.typography.run {
      copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
      )
    },
  ) { content() }
}

@Composable
private fun SourceCodeProFontFamily() = FontFamily(
  Font(Res.font.SourceCodePro_Black, weight = FontWeight.Black, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_BlackItalic, weight = FontWeight.Black, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_Bold, weight = FontWeight.Bold, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_BoldItalic, weight = FontWeight.Bold, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_ExtraBold, weight = FontWeight.ExtraBold, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_ExtraBoldItalic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_ExtraLight, weight = FontWeight.ExtraLight, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_ExtraLightItalic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_Light, weight = FontWeight.Light, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_LightItalic, weight = FontWeight.Light, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_Medium, weight = FontWeight.Medium, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_MediumItalic, weight = FontWeight.Medium, style = FontStyle.Italic),
  Font(Res.font.SourceCodePro_SemiBold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
  Font(Res.font.SourceCodePro_SemiBoldItalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
)

private val blue = Color(0xff0f0f23)
private val black = Color(0xff10101a)
private val green = Color(0xff009900)
