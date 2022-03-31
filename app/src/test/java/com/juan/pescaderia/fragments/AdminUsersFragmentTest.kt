package com.juan.pescaderia.fragments

import com.juan.pescaderia.dataclasses.User
import org.junit.Assert
import org.junit.Before
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.regex.Pattern

internal class AdminUsersFragmentTest {

    var userList:MutableList<User> = mutableListOf()
    var user = User("","","","","","","","","","","",true)
    val CIF_REGEX = "^([ABCDEFGHJKLMNPQRSUVW])([-])(\\d{7})([0-9A-J])\$"
    val EMAIL_REGEX = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})\$"
    val emailPattern = Pattern.compile(EMAIL_REGEX)
    val cifPattern = Pattern.compile(CIF_REGEX)

    @Test
    fun when_load_users() { //tests userList.size isnt 0 (has some value). If so, it fails
        userList.add(user)
        Assert.assertNotEquals(userList.size,0)
    }

    @Test
    fun when_user_is_valid() { //Evaluates user data and if everything is OK, will generate a value of 0 which makes the test pass. If value!=0 then test WONT pass
        user = User("sdfdsf","asdasdasd","C-12345678","asdasdasd","dsfdsfdsf","asdasdasd","asdasdasd","pedrojavier@sdasd.com","wwwwwww","who","me",true)
        var validationPoints = 0
        val cif = user.cif
        val name = user.name
        val surname = user.surname
        val address = user.address
        val phone = user.telephone
        val email = user.email
        val password = user.password
        val confirmPassword = user.password
        val recoverAnswer = user.recoverAnswer

        if (!cifPattern.matcher(cif).matches())
            validationPoints++

        if (name.isBlank() || name.isEmpty())
            validationPoints++

        if (surname.isBlank() || surname.isEmpty())
            validationPoints++

        if (address.isBlank() || address.isEmpty())
            validationPoints++

        if (phone.isBlank() || phone.isEmpty())
            validationPoints++

        if (!emailPattern.matcher(email).matches())
            validationPoints++

        if (password.isBlank() || password.isEmpty())
            validationPoints++

        if (confirmPassword.isBlank() || confirmPassword.isEmpty())
            validationPoints++

        if (!password.equals(confirmPassword))
            validationPoints++

        if (recoverAnswer.isBlank() || recoverAnswer.isEmpty())
            validationPoints++

        Assert.assertTrue(validationPoints==0)

    }

    @Test
    fun when_email_is_valid() { //if email passes through a regex, then it runs


//        Assert.assertTrue(pattern.matcher(user.email).matches()) //This wont RUN as user.email = ""
        user.email = "useremail@user.com"
        Assert.assertTrue(emailPattern.matcher(user.email).matches()) //This will run as user.mail matches the regex ("abcdef@ertty.com" for example)
    }

    @Test
    fun when_cif_is_valid() { //if cif passes through a regex, then it runs

//        Assert.assertTrue(pattern.matcher(user.cif).matches()) //This wont RUN as user.cif = ""
        user.cif="C-12345678"
        Assert.assertTrue(cifPattern.matcher(user.cif).matches()) //This will run as user.cif matches the regex ("C-12345678" -> C- and 8 any single digit numbers)
    }
}