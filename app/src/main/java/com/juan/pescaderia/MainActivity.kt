package com.juan.pescaderia

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.get
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.juan.pescaderia.fragments.EmptyFragment
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.testhooks.IdlingEntity
import com.juan.pescaderia.viewmodel.MyViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EmptyFragment.IEnableMenuBar,
    EmptyFragment.IEnableOrDisableMenuItems {

    private lateinit var toolbar:Toolbar
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var sharedPreferencesController: SharedPreferencesController
    private var beenClicked = false
    private lateinit var toggle: ActionBarDrawerToggle
    private var notifEnabled:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(applicationContext)!!
            setupNavigation()

            val conManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = conManager.activeNetworkInfo
            if (netInfo != null)
                myViewModel.loadUserList(null)
            else
                Toast.makeText(applicationContext, R.string.problema_internet, Toast.LENGTH_SHORT).show()
    }


    private fun setupNavigation() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        drawerLayout.bringToFront()
        navigationView.itemIconTintList = null
        navController = findNavController(this,R.id.nav_host_fragment)

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(navigationView,navController)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.isDrawerIndicatorEnabled = false
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        notifEnabled = sharedPreferencesController.isNotificationsAllowed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_layout,menu)
        val checkBox = menu!!.getItem(0)
        checkBox.isChecked = notifEnabled
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        val itemCheckBoxId = R.id.check_notification_status
        if (id == itemCheckBoxId)
        {
            item.isChecked = !notifEnabled
            notifEnabled = !notifEnabled
            sharedPreferencesController.setAllowNotifications(notifEnabled)
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp() :Boolean {
        onBackPressed()
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        item.isChecked = true
        drawerLayout.closeDrawers()
        val id: Int = item.itemId
        navController.navigateUp()
        when (id) {
                R.id.nav_change_user_data -> {
                    beenClicked = true
                    navController.navigate(R.id.user_data_fragment)
                }
                R.id.nav_create_new_user -> {
                    beenClicked = true
                    navController.navigate(R.id.new_user_fragment)
                }
                R.id.nav_new_order -> {
                    beenClicked = true
                    navController.navigate(R.id.new_order_fragment)
                }
                R.id.nav_admin_users -> {
                    beenClicked = true
                    navController.navigate(R.id.admin_users_fragment)
                }
                R.id.nav_read_order -> {
                    beenClicked = true
                    navController.navigate(R.id.read_last_order_fragment)
                }
                R.id.nav_admin_orders -> {
                    beenClicked = true
                    navController.navigate(R.id.admin_orders_fragment)
                }
                R.id.nav_admin_products -> {
                    beenClicked = true
                    navController.navigate(R.id.admin_products_fragment)
                }
                R.id.nav_basket -> {
                    beenClicked = true
                    navController.navigate(R.id.basket_fragment)
                }

                R.id.nav_login_logout -> {
                    AlertDialog.Builder(this).apply {
                        setPositiveButton(R.string.si) { dialog, _ ->
                            sharedPreferencesController.resetUserData()
                            enableBar(false)
                            navController.navigate(R.id.login_or_register_fragment)
                            dialog!!.cancel()
                        }
                        setNegativeButton(R.string.no) { dialog, _ ->
                            drawerLayout.closeDrawers()
                            dialog!!.cancel()
                        }
                    }.setTitle(R.string.logout).setMessage(R.string.dialog_message).create().show()
                }
        }
        return true
    }

    override fun enableDisableItems(map: MutableMap<Int, Boolean>) {
        map.forEach{ (index, bool) -> {
            navigationView.menu.findItem(index).setVisible(bool)
        }()}
    }
    override fun enableBar(enabled: Boolean) {
        toggle.isDrawerIndicatorEnabled = enabled
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers()
        else
        {
            if ((navController.currentDestination?.id == navController.graph[R.id.empty_fragment].id) || (navController.currentDestination?.id == navController.graph[R.id.login_or_register_fragment].id))
                finish()
            else
                navController.popBackStack()
        }
    }

    @Subscribe
    fun onEvent(idlingEntity: IdlingEntity) {

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
