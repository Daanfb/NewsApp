package com.example.newsapp.ui.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.newsapp.R

@Composable
fun MessageTemplate(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onBackground,
    icon: Painter,
    title: String,
    message: String,
) {
    Column(
        modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterVertically
        ), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(56.dp)
        )

        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MessageTemplate(
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onBackground,
    icon: ImageVector,
    title: String,
    message: String,
) {
    Column(
        modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterVertically
        ), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(56.dp)
        )

        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

object ErrorMessageTemplates{

    @Composable
    fun UnexpectedError(
        modifier: Modifier = Modifier,
        title: String = stringResource(R.string.unexpected_error),
        message: String = stringResource(R.string.unexpected_error_message)
    ) {
        MessageTemplate(
            modifier = modifier,
            icon = Icons.Default.Warning,
            iconColor = MaterialTheme.colorScheme.error,
            title = title,
            message = message
        )
    }

    @Composable
    fun InternetConnectionError(
        modifier: Modifier = Modifier,
        title: String = stringResource(R.string.no_internet_connection),
        message: String = stringResource(R.string.no_internet_connection_message)
    ) {
        MessageTemplate(
            modifier = modifier,
            icon = painterResource(R.drawable.ic_no_internet),
            iconColor = MaterialTheme.colorScheme.error,
            title = title,
            message = message
        )
    }
}