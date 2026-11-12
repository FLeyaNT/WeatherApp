package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.util.precipitationCondition

@Composable
fun PrecipitationProbability(
    probability: Int?,
    weatherCode: Int,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(6.dp),
    reserveIconSpace: Boolean = true
) {
    val condition = if (probability != null && probability > 0) {
        precipitationCondition(weatherCode)
    } else {
        null
    }

    val spokenText = if (probability == null) {
        stringResource(R.string.precipitation_no_data)
    } else {
        stringResource(R.string.precipitation_probability, probability) +
            (condition?.let { ", " + stringResource(it.description) } ?: "")
    }

    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.heightIn(min = 24.dp).clearAndSetSemantics {
            contentDescription = spokenText
        }
    ) {
        Text(probability?.let { "$it%" } ?: "—")
        // В дневных строках сохраняем колонку значка, даже когда осадков нет.
        if (condition != null || reserveIconSpace) {
            Box(
                modifier = Modifier.width(24.dp),
                contentAlignment = Alignment.Center
            ) {
                condition?.let {
                    Text(it.icon, fontSize = 20.sp, lineHeight = 24.sp)
                }
            }
        }
    }
}
