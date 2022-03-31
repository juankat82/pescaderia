package com.juan.pescaderia.persistence.productdatabase

import androidx.room.*
import com.juan.pescaderia.dataclasses.Product


private const val DB_NAME ="product"
@Dao
interface ProductDao {

    @Query("SELECT * FROM $DB_NAME")
    fun getAllProducts() : List<Product>

    @Query("SELECT * FROM $DB_NAME WHERE foreign_id == :id")
    fun getProductById(id:String) : Product

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product:Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductList(productList:List<Product>?)

    @Update
    fun updateProduct(product: Product)

    @Delete
    fun deleteProduct(product: Product)

    @Query("DELETE FROM $DB_NAME")
    fun deleteAllProducts()
}