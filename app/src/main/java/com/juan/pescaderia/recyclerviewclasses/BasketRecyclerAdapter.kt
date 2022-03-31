package com.juan.pescaderia.recyclerviewclasses

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.databinding.BasketViewHolderBinding
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.viewmodel.MyViewModel

class BasketRecyclerAdapter(_totalTextView:TextView,_orderlist: List<Order>, _application: Application) : RecyclerView.Adapter<BasketRecyclerAdapter.BasketItemHolder>() {

    private val application = _application
    var orderList:MutableList<Order> = _orderlist.toMutableList()
    private lateinit var binder:BasketViewHolderBinding
    private lateinit var productList:List<Product>
    private lateinit var myViewModel: MyViewModel
    private var totalTextView = _totalTextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketItemHolder {
        binder = BasketViewHolderBinding.inflate(LayoutInflater.from(parent.context))
        myViewModel = MyViewModel(application)
        productList =  myViewModel.getProductList()
        return BasketItemHolder(binder)
    }

    override fun getItemCount() = orderList.size


    override fun onBindViewHolder(holder: BasketItemHolder, position: Int) {
        var measureUnit = ""
        productList.forEach {
            if (it.foreign_id.equals(orderList[position].id_producto))
                if (it.peso_kg)
                    measureUnit = "€/Kg"
                else
                    measureUnit = "€/U"
        }
        holder.bind(this,position,orderList,measureUnit,totalTextView)
    }

    class BasketItemHolder(binding: BasketViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {

        private val binder = binding
        private lateinit var eraseButton:Button
        private lateinit var amountEditText: EditText
        private lateinit var adapter:BasketRecyclerAdapter

        private fun recalculate(orderList:List<Order>,_totalTextView:TextView)
        {
            var total = 0.0F
            if (orderList.isNotEmpty()) {
                orderList.forEach {
                    total += it.precio_total
                }
            }

            _totalTextView.text = "TOTAL:   $total €"
        }
        fun bind(_adapter: BasketRecyclerAdapter, position: Int, orderList: List<Order>, measureUnit: String, _totalTextView: TextView) {


            binder.order = orderList[position]
            binder.weightorunit = measureUnit
            adapter = _adapter
            eraseButton = binder.eraseButton
            eraseButton.setOnClickListener {
                adapter.orderList.remove(orderList[0])
                adapter.notifyDataSetChanged()
                recalculate(orderList,_totalTextView)
                val sharedPrefs = SharedPreferencesController.getPreferencesInstance(binder.root.context)
                sharedPrefs!!.setDraftOrder(adapter.orderList)
            }
            amountEditText = binder.basketCantidad
            amountEditText.addTextChangedListener( object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        binder.basketTotal.text = "TOTAL:   0.0 €"
                        orderList[position].cantidad = 0F
                        orderList[position].precio_total = 0F
                    }
                    else{
                        val totalPrice = orderList[position].precio*amountEditText.text.toString().toFloat()
                        binder.basketTotal.text = "TOTAL:   $totalPrice €"
                       orderList[position].precio_total = totalPrice
                    }
                    var total = 0.0F
                    orderList.forEach{
                        total += it.precio_total
                    }

                    _totalTextView.text = "TOTAL:   $total €"
                }
            })
        }
    }

}