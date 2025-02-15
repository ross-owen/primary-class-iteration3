package com.myowencode.primaryclass.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Student::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class PrimaryClassDatabase : RoomDatabase() {

    companion object {
        const val GENDER_MALE : String = "male"
        const val GENDER_FEMALE : String = "female"
        private const val DB_NAME : String = "primary_class_database"

        @Volatile
        private var INSTANCE: PrimaryClassDatabase?  = null
        
        fun getDatabase(context: Context): PrimaryClassDatabase {
            val i = INSTANCE
            if (i != null) {
                return i
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrimaryClassDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    abstract fun getStudentDao(): StudentDao
}