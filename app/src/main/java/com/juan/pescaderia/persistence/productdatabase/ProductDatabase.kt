package com.juan.pescaderia.persistence.productdatabase

import android.content.Context
import androidx.room.*
import com.juan.pescaderia.dataclasses.Product


private const val DATABASE_NAME = "RoomPescaderiaDatabase"

@Database(entities = [Product::class], exportSchema = false, version = 1)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context) : ProductDatabase? {
            if (INSTANCE == null)
                synchronized(ProductDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, ProductDatabase::class.java, DATABASE_NAME).build()
                }
            return INSTANCE
        }
        fun destroyDatabase()
        {
            INSTANCE = null
        }
    }
}