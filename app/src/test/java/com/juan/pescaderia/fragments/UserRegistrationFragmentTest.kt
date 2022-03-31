package com.juan.pescaderia.fragments

import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.regex.Pattern

private const val CIF_REGEX = "^([ABCDEFGHJKLMNPQRSUVW])([-])(\\d{7})([0-9A-J])\$"
private const val EMAIL_REGEX = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})\$"

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class UserRegistrationFragmentTest {

    @Test
    fun validateAddress() {
        val email = "asdasdasd@asdasd.com"
        val pattern = Pattern.compile(EMAIL_REGEX)
        val mat = pattern.matcher(email).matches()
        Assert.assertTrue(mat)
    }

    @Test
    fun cifIsCorrect() {
        val cif:String = "C-12345678"
        val pattern: Pattern = Pattern.compile(CIF_REGEX)
        var mat = pattern.matcher(cif).matches()
        Assert.assertTrue(mat)
    }
}