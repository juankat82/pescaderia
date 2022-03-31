package com.juan.pescaderia.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.LastOrderFragmentLayoutBinding
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.LastOrderRecyclerViewAdapter
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.services.EmailService
import com.juan.pescaderia.viewmodel.MyViewModel
import java.text.SimpleDateFormat
import java.util.*


class ReadLastOrderFragment : Fragment(){

    private val datePattern = "dd_MM_yyyy"
    private val locale = Locale.getDefault()
    private val dateTimeFormat = SimpleDateFormat(datePattern,locale)
    private val calendar = Calendar.getInstance().time
    private lateinit var readLastOrderBinding: LastOrderFragmentLayoutBinding
    private lateinit var sharedPreferencesController:SharedPreferencesController
    private lateinit var lastOrder:List<Order>
    private lateinit var lastOrderRecyclerView:RecyclerView
    private lateinit var lastOrderRecyclerViewAdapter:LastOrderRecyclerViewAdapter
    private lateinit var orderAgainButton: Button
    private lateinit var leaveLastOrder:Button
    private lateinit var queryService: QueryService
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var conManager: ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        readLastOrderBinding = DataBindingUtil.inflate(inflater, R.layout.last_order_fragment_layout,container,false)
        val view = readLastOrderBinding.root
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        conManager =context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        lastOrder = sharedPreferencesController.getDraftOrder()
        lastOrderRecyclerView = readLastOrderBinding.lastOrderRecyclerView
        lastOrderRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        lastOrderRecyclerViewAdapter = LastOrderRecyclerViewAdapter(lastOrder)
        lastOrderRecyclerView.adapter = lastOrderRecyclerViewAdapter
        lastOrderRecyclerViewAdapter.notifyDataSetChanged()
        queryService = QueryService.getInstance(requireContext())!!

        readLastOrderBinding.total = lastOrder.map { it.precio_total }.sum().toString()
        orderAgainButton = view.findViewById(R.id.order_again_button)
        if (lastOrderRecyclerViewAdapter.itemCount > 0) {
            orderAgainButton.setOnClickListener {
                val newOrderList = mutableListOf<Order>()
                AlertDialog.Builder(requireContext()).apply {
                    setPositiveButton(R.string.si, { dialog, id ->
                        lastOrder.forEach {
                            val newOrder = it.apply {
                                date = dateTimeFormat.format(calendar.time)
                                order_id = System.currentTimeMillis().toString()
                            }
                            newOrderList.add(newOrder)
                        }
                        sharedPreferencesController.setDraftOrder(newOrderList)
                        val emailIntent = Intent(requireContext(), EmailService::class.java)
                        netInfo = conManager.activeNetworkInfo
                        isNetActive = checkNetActive()
                        if (isNetActive) {
                            requireActivity().startService(emailIntent)
                            navController.navigateUp()
                            navController.navigate(R.id.empty_fragment)
                        } else
                            showAToast(R.string.no_internet_disponible)
                        dialog!!.cancel()
                    })
                    setNegativeButton(R.string.no, { dialog, id ->
                        dialog!!.cancel()
                    })
                }.setTitle(R.string.repetir_pedido).setMessage(R.string.esta_seguro_enviar_repedido)
                    .create().show()
            }
        }
        else
            Toast.makeText(requireContext(),R.string.reorden_no_posible,Toast.LENGTH_SHORT).show()
        leaveLastOrder = view.findViewById(R.id.dont_order_again_button)
        leaveLastOrder.setOnClickListener { requireActivity().onBackPressed() }
        return view
    }

    private fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }

    private fun showAToast(str:Int)
    {
        Handler(Looper.getMainLooper()).postDelayed( {
            Toast.makeText(context,context?.resources?.getString(str), Toast.LENGTH_SHORT).show()
        },200L)
    }
}