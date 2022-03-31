package com.juan.pescaderia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController


class EmptyFragment : Fragment() {

    private var preferencesRepo: SharedPreferencesController? = null
    private var user: User? = null
    private lateinit var navController:NavController
    private val adminEmail = ""
    private val cif = ""

    interface  IEnableMenuBar{
        fun enableBar(enabled:Boolean)
    }

    interface IEnableOrDisableMenuItems{
        fun enableDisableItems(map: MutableMap<Int,Boolean>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.empty_layout,container, false)

        navController = findNavController()
        preferencesRepo = SharedPreferencesController.getPreferencesInstance(view.context)
        user = preferencesRepo?.getUserData()

        val enableMenuBar: IEnableMenuBar = activity as IEnableMenuBar
        val enabledItems:IEnableOrDisableMenuItems = activity as IEnableOrDisableMenuItems
        val disabledMenu:MutableMap<Int,Boolean>

        if (user?.user_id.equals("-1")) {
            navController.navigate(R.id.login_or_register_fragment)
            enableMenuBar.enableBar(false)
        }
        else
        {
            if (!user?.name.equals("admin") && !user?.email.equals(adminEmail) && !user?.cif.equals(cif))
            {
                val userCanPlaceOrders = user?.approvedByAdmin ?: false
                val arrayOptions:Array<Boolean> = arrayOf(true,false,true,true,true,userCanPlaceOrders,false,false,false)

                disabledMenu = enableMenuBarOptions(false,arrayOptions)
                enabledItems.enableDisableItems(disabledMenu)
            }
            else
                enableMenuBarOptions(true, arrayOf())

            enableMenuBar.enableBar(true)
        }
        return view
    }

    internal fun enableMenuBarOptions(isAdmin:Boolean,_arrayOptions:Array<Boolean>) : MutableMap<Int,Boolean> {

        val changeUserDataEnabled = R.id.nav_change_user_data
        val createNewUserEnabled = R.id.nav_create_new_user
        val lastOrderEnabled = R.id.nav_read_order
        val basketEnabled = R.id.nav_basket
        val loginEnabled = R.id.nav_login_logout
        val adminUsersEnabled = R.id.nav_admin_users
        val adminOrdersEnabled = R.id.nav_admin_orders
        val adminProductsEnabled = R.id.nav_admin_products
        val createNewOrderEnabled = R.id.nav_new_order
        val map = mutableMapOf<Int,Boolean>()
        val list:List<Int> = mutableListOf(changeUserDataEnabled, createNewUserEnabled, createNewOrderEnabled, lastOrderEnabled, basketEnabled, loginEnabled, adminUsersEnabled, adminOrdersEnabled,adminProductsEnabled)
        map.clear()

        if (isAdmin) {
            for (i in list)
                map.put(i,true)
        }
        else
        {
            for (i in 0 until list.size)
                map.put(list[i],_arrayOptions[i])
        }

        return map
    }
}
