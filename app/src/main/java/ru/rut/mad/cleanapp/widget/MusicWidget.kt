package ru.rut.mad.cleanapp.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import ru.rut.mad.cleanapp.R

class MusicWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val trackTitle = prefs[MusicWidgetState.trackTitleKey] ?: "Выберите трек"
            val isPlaying = prefs[MusicWidgetState.isPlayingKey] ?: false

            MusicWidgetContent(trackTitle = trackTitle, isPlaying = isPlaying)
        }
    }

    @Composable
    private fun MusicWidgetContent(trackTitle: String, isPlaying: Boolean) {
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        Row(
            modifier = GlanceModifier.fillMaxSize()
                .background(ImageProvider(R.drawable.ic_launcher_background))
                .cornerRadius(16.dp).padding(8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = "Album Art", modifier = GlanceModifier.size(64.dp)
            )
            Spacer(GlanceModifier.width(8.dp))
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(text = trackTitle, style = TextStyle(color = ColorProvider(Color.White)))
                Spacer(GlanceModifier.height(4.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.End,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(playPauseIcon),
                        contentDescription = "Play/Pause",
                        modifier = GlanceModifier.size(36.dp)
                            .clickable(onClick = actionRunCallback<PlayPauseActionCallback>())
                    )
                    Image(
                        provider = ImageProvider(R.drawable.ic_skip_next),
                        contentDescription = "Next",
                        modifier = GlanceModifier.size(36.dp)
                            .clickable(onClick = actionRunCallback<NextTrackActionCallback>())
                    )
                }
            }
        }
    }
}