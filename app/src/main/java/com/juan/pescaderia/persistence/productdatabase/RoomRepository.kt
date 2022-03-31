package com.juan.pescaderia.persistence.productdatabase

import android.content.Context
import android.util.Log
import com.juan.pescaderia.dataclasses.Product
import kotlinx.coroutines.*

private lateinit var context:Context
private var repository:RoomRepository? = null
private var database:ProductDatabase? = null

class RoomRepository {

    companion object {
        fun getInstance(_context: Context) : RoomRepository? {
            context = _context
            if (repository == null) {
                repository= RoomRepository()
            }
            return repository
        }
    }
    init {
        database = ProductDatabase.getDatabase(context)
    }

    suspend fun getAllProducts() : List<Product> = coroutineScope  {
        val data = GlobalScope.async {database?.productDao()?.getAllProducts() ?: emptyList()}
        Log.i("DEFERREDVALUES","${data.await()}")
        data.await()

    }
    suspend fun readFromRoomDatabaseAfterInsertingFromDownloadUsingRetrofit(database: ProductDatabase) = withContext(Dispatchers.IO) {
        database.productDao().getAllProducts()
    }
    suspend fun getOneProductById(id:String) = withContext(Dispatchers.IO){
        var product:Product? = Product(null,"","",false,0.0F,true)
        product = database?.productDao()?.getProductById(id)
        product
    }
    suspend fun insertProductInRoomDatabase(product:Product) = withContext(Dispatchers.IO){ database?.productDao()?.insertProduct(product) }

    suspend fun insertListInRoomDatabase(prodList:List<Product>) = withContext(Dispatchers.IO) {
        database?.productDao()?.insertProductList(prodList)
        prodList
    }
    suspend fun updateProductRoom(product:Product) = withContext(Dispatchers.IO) {
        database?.productDao()?.updateProduct(product)
        product
    }
    suspend fun deleteRoomDatabaseProductById(product: Product) = withContext(Dispatchers.IO) {
        database?.productDao()?.deleteProduct(product)
        product
    }
    suspend fun deleteAllProductsLocalDB(productList:List<Product>) = withContext(Dispatchers.IO){
        database?.productDao()?.deleteAllProducts()
        productList
    }
}
