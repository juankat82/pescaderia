package com.juan.pescaderia.fragments

import com.juan.pescaderia.dataclasses.User
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PersonalDataFragmentTest {

    var user = User("","","","","","","","","","","",true)
    @Test
    fun when_do_change_user_data() { //copy user to user2 and then check they are different. If they are, it passes (have to modify user2 after)
        val user2 = User("","","","","","","","qqqqqqqqqq@eee.com","","","",true)
        Assert.assertTrue(user!=user2)
    }

    @Test
    fun when_add_text() { //Passes if text is joint together
        val src = "my Text"
        var newtext = "This text is "
        newtext +=src
        Assert.assertTrue(newtext.equals("This text is my Text"))
    }
}