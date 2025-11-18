package ru.rut.mad.cleanapp.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget()

    /**
     * Переопределяем onReceive для добавления логов.
     * Это самый надежный способ убедиться, что система доставила наше событие.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // ВАЖНО: Сначала вызываем super.onReceive, чтобы Glance выполнил свою магию
        super.onReceive(context, intent)

        // Теперь добавляем свой лог
        val action = intent.action
        if (action == MusicWidgetActions.ACTION_PLAY_PAUSE || action == MusicWidgetActions.ACTION_NEXT_TRACK) {
            Log.d("MusicWidgetReceiver", "Received action: $action")
        }
    }
}