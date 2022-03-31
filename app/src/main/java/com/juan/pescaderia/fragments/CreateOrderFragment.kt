package com.juan.pescaderia.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.NewOrderLayoutBinding
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.NewOrderRecyclerAdapter
import com.juan.pescaderia.recyclerviewclasses.AddedItemsRecyclerAdapter
import com.juan.pescaderia.testhooks.IdlingEntity
import com.juan.pescaderia.viewmodel.MyViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CreateOrderFragment : Fragment() {

    private lateinit var newOrderFragmentBinding: NewOrderLayoutBinding
    private val myViewModel:MyViewModel by viewModels()
    private lateinit var productList:List<Product>
    private lateinit var newOrderRecyclerView:RecyclerView
    private lateinit var newOrderRecyclerAdapter:NewOrderRecyclerAdapter
    private lateinit var addedItemRecyclerView:RecyclerView
    private lateinit var addedItemsAdapter:AddedItemsRecyclerAdapter
    private lateinit var newOrderLayout: ConstraintLayout
    private lateinit var sendToBasketButton:Button
    private lateinit var cancelShopping: Button
    private lateinit var navController: NavController
    private lateinit var sharedPreferencesController: SharedPreferencesController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myViewModel.loadAllProducts(false)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        newOrderFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.new_order_layout,container,false)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        val view = newOrderFragmentBinding.root
        newOrderLayout = newOrderFragmentBinding.newOrderLayout
        newOrderLayout.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            if (newOrderLayout.hasFocus())
                hideKeyboard(view)
        }


        productList = myViewModel.getProductList()
        if (productList.isEmpty()) {

            myViewModel.loadAllProductsRemote(false)
            productList = myViewModel.getProductList()
        }

        addedItemRecyclerView = newOrderFragmentBinding.listOrdenes
        addedItemsAdapter = AddedItemsRecyclerAdapter(mutableListOf(),productList)
        addedItemRecyclerView.adapter = addedItemsAdapter
        newOrderRecyclerView = view.findViewById(R.id.new_order_recycler_view)
        newOrderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newOrderRecyclerAdapter = NewOrderRecyclerAdapter(productList,addedItemsAdapter)
        newOrderRecyclerView.adapter = newOrderRecyclerAdapter
        newOrderRecyclerAdapter.notifyDataSetChanged()

        sendToBasketButton = view.findViewById(R.id.anadir_a_cesta_button)
        sendToBasketButton.setOnClickListener {
            val orderList = addedItemsAdapter.getOrderList()
            if (orderList.size > 0) {
                val bundle = Bundle()
                bundle.putString("basket_list", Gson().toJson(orderList))
                navController.navigateUp()
                sharedPreferencesController.setDraftOrder(orderList)
                sharedPreferencesController.setSentFlag(false)
                navController.navigate(R.id.basket_fragment)
            } else
                Toast.makeText(requireContext(), R.string.cesta_vacia, Toast.LENGTH_SHORT).show()
        }
        cancelShopping = view.findViewById(R.id.cancelar_pedido_button)
        cancelShopping.setOnClickListener { requireActivity().onBackPressed() }

        return view
    }
    internal fun hideKeyboard(view:View) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
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