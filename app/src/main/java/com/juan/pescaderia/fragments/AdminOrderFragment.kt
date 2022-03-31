package com.juan.pescaderia.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.juan.pescaderia.R
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.viewmodel.MyViewModel
import java.util.*
import com.juan.pescaderia.recyclerviewclasses.QueryOrdersRecyclerAdapter
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AdminOrderFragment : Fragment() {

    private val viewModel:MyViewModel by viewModels()
    private lateinit var cancelButton: Button
    private lateinit var searchButton: Button
    private lateinit var reloadUsersButton:ImageButton
    private lateinit var dateButton:ImageButton
    private lateinit var allDatesButton: ImageButton
    private lateinit var userSpinner:Spinner
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var ordersViewPager: ViewPager2
    private lateinit var orderRecyclerAdapter:QueryOrdersRecyclerAdapter
    private lateinit var baseLayout:ConstraintLayout
    private var queryService: QueryService? = null
    private lateinit var navController: NavController
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private lateinit var conManager:ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null
    private lateinit var progressBarLayout:LinearLayout
    private lateinit var orderAdminLayout:ConstraintLayout
    private lateinit var sharedPreferencesController:SharedPreferencesController
    private lateinit var thisFragment: AdminOrderFragment
    lateinit var v:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        v = inflater.inflate(R.layout.admin_orders_layout,container,false)
        thisFragment = this
        conManager =requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        queryService = QueryService.getInstance(requireContext())


        initViews(v)
        loadUsers(0)//0 means system will load it upon entering the fragment, 1 means we refreshed it manually

        return v
    }
    private fun loadUsers(option:Int){
        netInfo = conManager.activeNetworkInfo
        isNetActive = checkNetActive()
        if(isNetActive)
        {
            disableViewListeners()
            viewModel.loadUserList(thisFragment)
            if (option==1)
            {
                sharedPreferencesController.setLoadingProductsScreenValue(true)
                queryService?.getAllUsersForSpinner(arrayAdapter,thisFragment,0)

            }
        }
        else
            Toast.makeText(requireContext(),R.string.no_internet_disponible,Toast.LENGTH_SHORT).show()
    }
    private fun initViews(view: View) {
        progressBarLayout = view.findViewById(R.id.progress_order_bar_layout)
        orderAdminLayout = view.findViewById(R.id.admin_orders_base_layout2)
        baseLayout = view.findViewById(R.id.admin_users_base_layout)
        userSpinner = view.findViewById(R.id.users_spinner)
        arrayAdapter = ArrayAdapter(requireContext(),R.layout.order_name_spinner_text_view)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        queryService?.getAllUsersForSpinner(arrayAdapter,null,0)
        ordersViewPager = view.findViewById(R.id.orders_view_pager)
        orderRecyclerAdapter = QueryOrdersRecyclerAdapter(requireContext(), listOf(listOf()))
        ordersViewPager.adapter = orderRecyclerAdapter
        userSpinner = view.findViewById(R.id.users_spinner)
        userSpinner.isHorizontalScrollBarEnabled = true
        userSpinner.adapter = arrayAdapter
        reloadUsersButton = view.findViewById(R.id.reload_users)
        searchButton = view.findViewById(R.id.buscar_button)
        cancelButton = view.findViewById(R.id.cancel_changes_button)
        dateButton = view.findViewById(R.id.calendar_button)
        allDatesButton = view.findViewById(R.id.every_order_button)
        enableViewListeners()
    }

    fun enableViewListeners() {
        orderAdminLayout.alpha =1.0F

        if (orderAdminLayout.alpha == 1.0F)
            progressBarLayout.visibility = View.GONE

        if (!ordersViewPager.isVisible)
            ordersViewPager.visibility = View.VISIBLE

        baseLayout.setOnFocusChangeListener { _, _ ->
            if (baseLayout.hasFocus())
                hideKeyboard(this@AdminOrderFragment.v)
        }

        if (orderAdminLayout.alpha == 1.0F) {
            var dateChosen = 0 //0 means "all dates", 1 means "a chosen date". 0 will be default
            var mes = "$month"
            var dia = "$day"
            var anio = "$year"
            var dateToQuery = "${dia}_${mes}_${anio}"
            reloadUsersButton.setOnClickListener {
                netInfo = conManager.activeNetworkInfo
                isNetActive = checkNetActive()
                if (!isNetActive)
                    Toast.makeText(
                        requireContext(),
                        R.string.no_internet_disponible,
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    loadUsers(1)
                }
            }
            cancelButton.setOnClickListener { navController.navigateUp() }
            dateButton.setOnClickListener {
                val dpd = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    dia = "$dayOfMonth"
                    mes = "${month + 1}".also { if (month < 10) "0" + "$month" }
                    anio = "$year"
                    dateToQuery = "${dia}_${mes}_${anio}"
                    dateChosen = 1
                }, year, month, day)
                dpd.show()
            }
            allDatesButton.setOnClickListener { dateChosen = 0 }

            searchButton.setOnClickListener {
                netInfo = conManager.activeNetworkInfo
                isNetActive = checkNetActive()
                if (!isNetActive)
                    Toast.makeText(
                        requireContext(),
                        R.string.no_internet_disponible,
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    orderRecyclerAdapter.resetList()

                    if (dateChosen == 0) {
                        if (userSpinner.isNotEmpty() && userSpinner.selectedItemId.toString()
                                .isNotEmpty() && !userSpinner.selectedItemId.toString()
                                .split("--")[0].equals("0")
                        ) {
                            queryService?.getOrdersByCustomerCif(
                                userSpinner.selectedItem.toString().split("--")[0],
                                orderRecyclerAdapter,
                                thisFragment
                            )
                        } else
                            Toast.makeText(
                                requireContext(),
                                R.string.elija_usuario,
                                Toast.LENGTH_SHORT
                            ).show()
                    } else {
                        queryService?.getOrdersByCifAndDate(
                            userSpinner.selectedItem.toString().split("--")[0],
                            dateToQuery,
                            orderRecyclerAdapter,
                            thisFragment
                        )
                    }
                }
            }
        }
    }
    fun disableViewListeners() {
        val isLoading = sharedPreferencesController.getLoadingOrdersScreenValue()

        if (isLoading) {
            orderAdminLayout.alpha = 0.5F
            ordersViewPager.visibility = View.GONE
            progressBarLayout.visibility = View.VISIBLE
            cancelButton.setOnClickListener(null)
            dateButton.setOnClickListener(null)
            allDatesButton.setOnClickListener(null)
            searchButton.setOnClickListener(null)
        }
    }

    private fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }

    private fun hideKeyboard(view:View) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
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
