package com.juan.pescaderia.dataclasses

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DataclassTest {

    lateinit var email: Email
    lateinit var fullOrderForAdmin: FullOrderForAdmin
    lateinit var order: Order
    lateinit var product: Product
    lateinit var textMessage: TextMessage
    lateinit var user: User

    @Before
    fun init() {
        email = Email("","","","")
        fullOrderForAdmin = FullOrderForAdmin("","","","","","")
        order = Order("","","","","",0F,0F,0F,"")
        product = Product("","","",true,0F,false)
        textMessage = TextMessage("","","")
        user = User("","","","","","","","","","","",true)
    }

    @Test   //runs if classes instances ARE NOT NULL
    fun when_classes_are_successfully_created() {
        Assert.assertNotNull(email)
        Assert.assertNotNull(fullOrderForAdmin)
        Assert.assertNotNull(order)
        Assert.assertNotNull(product)
        Assert.assertNotNull(textMessage)
        Assert.assertNotNull(user)
    }

}