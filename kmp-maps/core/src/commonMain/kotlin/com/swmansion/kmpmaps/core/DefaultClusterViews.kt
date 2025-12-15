package com.swmansion.kmpmaps.core

import androidx.annotation.RestrictTo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** @suppress */
@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public fun DefaultCluster(size: Int) {
    Box(
        modifier =
            Modifier.size(40.dp)
                .background(Color.Blue, CircleShape)
                .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = size.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
    }
}

/** @suppress */
@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public fun DefaultPin(marker: Marker) {
    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "Standard Marker",
            tint = Color(0xFFEA4335),
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = marker.title ?: "Marker title",
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )
    }
}
