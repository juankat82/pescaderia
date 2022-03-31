package com.juan.pescaderia.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.BasketRecyclerAdapter
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.services.EmailService
import com.juan.pescaderia.testhooks.IdlingEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class BasketFragment : Fragment() {

    private lateinit var sharedPreferencesConstroller:SharedPreferencesController
    private lateinit var orderList: List<Order>
    private lateinit var navController: NavController
    private lateinit var sendOrderButton:Button
    private lateinit var cancelOrderButton: Button
    private lateinit var queryService: QueryService
    private lateinit var basketRecyclerView: RecyclerView
    private lateinit var basketRecyclerAdapter:BasketRecyclerAdapter
    private lateinit var totalTextView: TextView
    private lateinit var conManager: ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val view = inflater.inflate(R.layout.basket_fragment_layout,container,false)
        sharedPreferencesConstroller = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        orderList = sharedPreferencesConstroller.getDraftOrder()
        conManager =context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        if (sharedPreferencesConstroller.getSentFlag()) {
            orderList = emptyList()
        }

        totalTextView = view.findViewById(R.id.sub_total_textview)
        queryService = QueryService.getInstance(requireContext())!!
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        basketRecyclerView = view.findViewById(R.id.basket_recycler)
        basketRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        basketRecyclerAdapter = BasketRecyclerAdapter(totalTextView, orderList, requireActivity().application)
        basketRecyclerView.adapter = basketRecyclerAdapter

        sendOrderButton = view.findViewById(R.id.finalizar_pedido)
        sendOrderButton.setOnClickListener {
            val myOrder = sharedPreferencesConstroller.getDraftOrder()
            if (myOrder.isEmpty())
                Toast.makeText(requireContext(), R.string.cesta_vacia, Toast.LENGTH_SHORT).show()
            else {
                AlertDialog.Builder(requireContext()).apply {
                    setPositiveButton(R.string.si) { _, _ ->
                        if (myOrder.isNotEmpty()) {
                            val emailIntent = Intent(context, EmailService::class.java)
                            netInfo = conManager.activeNetworkInfo
                            isNetActive = checkNetActive()
                            if (isNetActive) {
                                requireActivity().startService(emailIntent)
                                navController.navigateUp()
                                navController.navigate(R.id.empty_fragment)
                            } else
                                showAToast(R.string.no_internet_disponible)
                        }
                    }
                    setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }.setTitle(R.string.atencion).setMessage(R.string.email_debe_ser_mandado)
                        .create().show()
                }
            }
        }
        cancelOrderButton = view.findViewById(R.id.cancelar_compra_button)
        cancelOrderButton.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setPositiveButton(R.string.si) { dialog, _ ->
                    orderList = emptyList()
                    sharedPreferencesConstroller.setDraftOrder(orderList)
                    Toast.makeText(view.context, R.string.cesta_vaciada, Toast.LENGTH_SHORT)
                        .show()
                    dialog!!.cancel()
                }
                setNegativeButton(R.string.no) { dialog, _ ->
                    dialog!!.cancel()
                }
            }.setTitle(R.string.vaciar_cesta).setMessage(R.string.vaciar_cesta_seguro).create()
                .show()
            requireActivity().onBackPressed()
        }
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
//////////////////TEST/////////////////
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