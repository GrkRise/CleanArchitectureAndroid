package ru.rut.mad.cleanapp.workers

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import java.io.File
import java.io.FileOutputStream

// Ключи для передачи данных между ViewModel и Worker
const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val KEY_FILTERED_URI = "KEY_FILTERED_URI"

class FilterWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val imageLoader: ImageLoader // <-- ЗАВИСИМОСТЬ ПОЛУЧЕНА ЧЕРЕЗ КОНСТРУКТОР
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val imageUriString = inputData.getString(KEY_IMAGE_URI)
        return try {
            require(!imageUriString.isNullOrBlank()) { "Image URI is empty" }

            // Используем УЖЕ СУЩЕСТВУЮЩИЙ ImageLoader из Koin
            val request = ImageRequest.Builder(appContext)
                .data(imageUriString)
                .size(Size.ORIGINAL)
                // Говорим Coil загрузить Bitmap в формате, который можно изменять
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                // Отключаем аппаратное ускорение для этого запроса
                .allowHardware(false)
                .build()

            // ImageLoader сначала проверит кэш, и только если там нет картинки, пойдет в сеть
            val result = imageLoader.execute(request)
            val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            require(bitmap != null) { "Failed to decode bitmap" }

            val filteredBitmap = applyBlackAndWhiteFilter(bitmap)
            val filteredUri = saveBitmapToFile(appContext, filteredBitmap)
            val outputData = workDataOf(KEY_FILTERED_URI to filteredUri.toString())

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e("FilterWorker", "Error applying filter", throwable)
            Result.failure()
        }
    }

    private fun applyBlackAndWhiteFilter(src: Bitmap): Bitmap {
        val config = src.config ?: Bitmap.Config.ARGB_8888
        val result = Bitmap.createBitmap(src.width, src.height, config)
        val canvas = Canvas(result)
        val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(colorMatrix) }
        canvas.drawBitmap(src, 0f, 0f, paint)
        return result
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
        val outputDir = File(context.cacheDir, "filtered_images").apply { mkdirs() }
        val outputFile = File(outputDir, "${System.currentTimeMillis()}.png")
        FileOutputStream(outputFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return Uri.fromFile(outputFile)
    }
}