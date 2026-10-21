
package com.example.virtualqueue.ui.theme
import com.example.virtualqueue.R

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

private val InterFont = FontFamily(
    Font(R.font.inter_28pt_regular, FontWeight.Normal),
    Font(R.font.inter_28pt_medium, FontWeight.Medium),
    Font(R.font.inter_28pt_semibold, FontWeight.SemiBold),
    Font(R.font.inter_18pt_bold, FontWeight.Bold)
)

private fun TextStyle.withInter(): TextStyle =
    copy(fontFamily = InterFont)

val Typography: Typography = Typography().run {
    copy(
        displayLarge = displayLarge.withInter(),
        displayMedium = displayMedium.withInter(),
        displaySmall = displaySmall.withInter(),

        headlineLarge = headlineLarge.withInter(),
        headlineMedium = headlineMedium.withInter(),
        headlineSmall = headlineSmall.withInter(),

        titleLarge = titleLarge.withInter(),
        titleMedium = titleMedium.withInter(),
        titleSmall = titleSmall.withInter(),

        bodyLarge = bodyLarge.withInter(),
        bodyMedium = bodyMedium.withInter(),
        bodySmall = bodySmall.withInter(),

        labelLarge = labelLarge.withInter(),
        labelMedium = labelMedium.withInter(),
        labelSmall = labelSmall.withInter()
    )
}
