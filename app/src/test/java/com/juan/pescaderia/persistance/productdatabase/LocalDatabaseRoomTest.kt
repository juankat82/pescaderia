package com.juan.pescaderia.persistance.productdatabase

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.persistence.productdatabase.ProductDao
import com.juan.pescaderia.persistence.productdatabase.ProductDatabase
import com.juan.pescaderia.persistence.productdatabase.RoomRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.stubbing.Answer
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LocalDatabaseRoomTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val context = InstrumentationRegistry.getInstrumentation().context

    private lateinit var database:ProductDatabase
    private lateinit var productDao:ProductDao
    private val product = Product("-5","a","",false,0.0F,false)
    private val productB = Product("-5","a","changed!",false,0.0F,false)

    @Before
    fun initDB() {

        //allows using Room in mainthread
        database = Room.inMemoryDatabaseBuilder(context,ProductDatabase::class.java).allowMainThreadQueries().build()
        productDao = spy(database.productDao())

    }

    @Test
    fun test_getAllProducts_request() {
        productDao.insertProduct(product)
        Assert.assertEquals(listOf(product),productDao.getAllProducts())
    }
    @Test
    fun test_put_product_into_room_database() {

        productDao.insertProduct(product)
        val product2 = productDao.getProductById("a")
        Assert.assertEquals(product,product2)
    }

    @Test
    fun test_update_database() {
        productDao.insertProduct(product)
        productDao.updateProduct(productB)

        val product2 = productDao.getProductById("a")
        Assert.assertNotEquals(product,product2)
    }

    @Test
    fun test_erase_from_room_database() {
        val product = Product("-5","a","",false,0.0F,false)
        productDao.insertProduct(product)
        var productB = productDao.getProductById("a")
        productDao.deleteProduct(productB)
        productB = productDao.getProductById("a")
        Assert.assertNotEquals(product,productB)
    }

    @After
    fun closeDB() {
        database.close()
    }

}