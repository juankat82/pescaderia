package com.juan.pescaderia.services

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import com.google.gson.Gson
import com.juan.pescaderia.databaseupdater.RemoteDatabaseUploaderReceiver
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.productdatabase.ProductDao
import com.juan.pescaderia.persistence.productdatabase.ProductDatabase
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock

internal class EmailServiceMockTest {

    private lateinit var sharedPreferences: SharedPreferencesController
    private lateinit var user: User
    private lateinit var order: Order
    private lateinit var orderList:List<Order>
    private lateinit var productList:List<Product>
    private lateinit var database:ProductDatabase
    private lateinit var remoteUploaderReceiver:RemoteDatabaseUploaderReceiver
    private lateinit var dao: ProductDao

    @BeforeEach
    fun init() {
        sharedPreferences = mock()
        order = mock()
        user = mock()
        database = mock()
        orderList = mock()
        productList = mock()
        remoteUploaderReceiver = mock()
        whenever(sharedPreferences.getUserData()).thenReturn(user)
        whenever(sharedPreferences.getDraftOrder()).thenReturn(listOf(order))


    }

    @Test
    fun onHandleIntent() {
        whenever(sharedPreferences.getDraftOrder()).thenReturn(orderList)
        orderList = sharedPreferences.getDraftOrder()
        Assert.assertFalse(orderList.isNullOrEmpty())

        val order:Order = mock()
        val dao = mock<ProductDao>()
        val emailBody = "Hola Francisco, este es mi pedido para manana"

        whenever(order.id_producto).thenReturn("1111")
        val idProducto = order.id_producto
        dao.getProductById(idProducto)
        verify (dao).getProductById(idProducto)
        Assert.assertTrue(idProducto.equals("1111"))

        sharedPreferences.setSentFlag(true)
        val spyPref = spy(sharedPreferences)
        inOrder(sharedPreferences) {
            spyPref.setSentFlag(true)
        }
    }

    @Test
    fun addToRemoteOrderDatabase() {
        val filter:IntentFilter = mock()
        val context: Context = mock()
        val bundle:Bundle = mock()
        val gson:Gson = mock()
        val remoteDatabaseUpdateIntent:Intent = mock()
        context.registerReceiver(remoteUploaderReceiver,filter)
        bundle.putString("USER",gson.toJson(user))
        bundle.putString("ORDERLIST",gson.toJson(productList))
        remoteDatabaseUpdateIntent.putExtra("DATA_BUNDLE",bundle)
        context.sendBroadcast(remoteDatabaseUpdateIntent)

        whenever(bundle.getString("USER",gson.toJson(user))).thenReturn("usuario")
        whenever(bundle.getString("ORDERLIST",gson.toJson(productList))).thenReturn("usuario")
        val mockedUsuario = bundle.getString("USER",gson.toJson(user))
        val mockedOrderList = bundle.getString("ORDERLIST",gson.toJson(productList))
        Assert.assertTrue(mockedUsuario.isNotEmpty())
        println(mockedUsuario)
        Assert.assertTrue(mockedOrderList.isNotEmpty())
        println(mockedOrderList)
        verify(remoteDatabaseUpdateIntent).putExtra("DATA_BUNDLE",bundle)
        verify(context).sendBroadcast(remoteDatabaseUpdateIntent)
        val spiedService = spy(context)
        spiedService.registerReceiver(remoteUploaderReceiver,filter)
        spiedService.sendBroadcast(remoteDatabaseUpdateIntent)
    }

    @Test
    fun onDestroy() {
        database.close()
        Thread.sleep(1000L)
        verify(database).close()

    }
}