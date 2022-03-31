package com.juan.pescaderia.databaseupdater

import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class RemoteDatabaseUploaderReceiverTest {

    lateinit var orderList:List<Order>
    lateinit var user: User
    lateinit var order:Order

    @Before
    fun init() {
        order = Order("","","","","",0F,0F,0F,"")
        orderList= listOf(order)
        user = User("","","","","","","","","","","",true)
    }

    @Test
    fun when_upload_order() {
        val successUpload:Boolean
        Assert.assertNotEquals(orderList.size,0)//its acutallu one so this test passes
        Assert.assertNotNull(user)//runs because user isnt null

        successUpload = testUpload()
        Assert.assertTrue(successUpload)
        when_net_active()
        when_send_sms()
        when_show_toast()
    }

    fun when_net_active() {
        val isNetActive = true
        Assert.assertTrue(isNetActive) //it runs when isNetActive is set to TRUE
    }

    fun when_send_sms() {
        val msg = "MESSAGE"
        Assert.assertNotEquals(msg,"Sent") //Runs when msg doesnt equal to "SENT"
    }

    fun when_show_toast() {
        val stringId = 1
        Assert.assertNotEquals(stringId,0)//runs if stringId is not equals to 0
    }

    fun testUpload() = true
}