package com.juan.pescaderia.viewmodels

import android.app.Application
import android.os.Build
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.viewmodel.MyViewModel
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.rule.ActivityTestRule
import com.juan.pescaderia.MainActivity
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ViewModelTest {

//    val mainActivity = MainActivity()
////    @get:Rule
////    var instantExecutorRule = InstantTaskExecutorRule()
//    @get:Rule
//    var  mActivityRule:ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
//    val context = InstrumentationRegistry.getInstrumentation().context
//
//
//    private lateinit var viewModel: MyViewModel
//    val observer: Observer<User> = mock()
//
//    @Before
//    fun before() {
//        val app = RuntimeEnvironment.application as Application
//        val application = app.stub { MyViewModel(this.mock) }
//        viewModel = MyViewModel(application)
//        //viewModel.doGetOneUserById("")
//        viewModel.userLiveData.observeForever(observer)
//    }
//
//    @Test
//    fun testing_product_list_view_model() {
//        val expectedUser = User("1","-1","","","","","","","","","", true)
//        viewModel.doGetOneUserById("-1")
//        viewModel.userLiveData.value = User("1","-1","","","","","","","","","", true)
//        val captor = ArgumentCaptor.forClass(User::class.java)
//        captor.run {
//            verify(observer, times(2)).onChanged(capture())
//            Assert.assertEquals(expectedUser,value) //value is taken from the captor
//        }
//
//    }


}