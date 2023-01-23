package com.stevdzasan.diaryapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stevdzasan.diaryapp.data.database.entity.ImageToDelete
import com.stevdzasan.diaryapp.data.database.entity.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 2,
    exportSchema = false
)
abstract class ImagesDatabase: RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}