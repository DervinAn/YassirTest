package com.example.yassirtest.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.yassirtest.domain.model.Character
import kotlinx.coroutines.delay

@Composable
fun CharacterItem(
    character: Character,
    onClick: () -> Unit,
    highlight: Boolean = false
) {
    val borderColor = when (character.status.lowercase()) {
        "alive" -> Color(0xFF4CAF50)
        "dead" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val backgroundColor = if (highlight)  Color(0x332196F3) else Color(0x335E6972)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(character.image),
                contentDescription = character.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, borderColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Species: ${character.species}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}



@Composable
fun AnimatedCharacterItem(
    character: Character,
    animatedItemIds: SnapshotStateList<Int>,
    onClick: () -> Unit,
    highlight: Boolean = false
) {
    val shouldAnimate = remember(character.id) {
        !animatedItemIds.contains(character.id)
    }

    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            delay(50)
            animatedItemIds.add(character.id)
        }
    }

    AnimatedVisibility(
        visible = animatedItemIds.contains(character.id),
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(400))
    ) {
        CharacterItem(character = character, onClick = onClick, highlight = highlight)
    }
}
