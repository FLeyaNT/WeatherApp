package com.example.weatherapp.ui.favorites.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun FavoriteCityButton(isFavorite: Boolean, busy: Boolean, onClick: () -> Unit) {
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        enabled = !busy,
        modifier = Modifier.size(52.dp),
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            ),
            modifier = Modifier.size(36.dp),
            contentDescription = if (isFavorite) {
                stringResource(R.string.remove_favorite)
            } else {
                stringResource(R.string.add_favorite)
            }
        )
    }
}
