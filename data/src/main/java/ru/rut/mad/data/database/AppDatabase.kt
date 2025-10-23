package ru.rut.mad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CatCacheEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}