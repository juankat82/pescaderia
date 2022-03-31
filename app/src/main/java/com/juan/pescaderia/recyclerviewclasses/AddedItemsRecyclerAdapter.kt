package com.juan.pescaderia.recyclerviewclasses

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.ListviewOrderedProductLayoutBinding
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product

class AddedItemsRecyclerAdapter(_orderList:List<Order>, _productList:List<Product>) : RecyclerView.Adapter<AddedItemsRecyclerAdapter.AddedOrder>() {

    private var orderList:MutableList<Order> = _orderList.toMutableList()
    private var productList= _productList
    private lateinit var binder:ListviewOrderedProductLayoutBinding

    fun getOrderList() = orderList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedOrder {
        val inflater = LayoutInflater.from(parent.context)
        binder = ListviewOrderedProductLayoutBinding.inflate(inflater)
        return AddedOrder(binder,this)
    }

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(holder: AddedOrder, position: Int) {
        var measureUnit = ""
        var pos = -1
        productList.forEach {
            pos = pos+1
                if (it.foreign_id.equals(orderList[position].id_producto))
                    if (it.peso_kg)
                        measureUnit = "€/Kg"
                    else
                        measureUnit = "€/Unidad"
        }
        holder.bind(measureUnit,orderList[position],pos,orderList)
    }

    fun addNewOrder(order:Order) {
        var isExistProduct = false
        orderList.forEach{
            if (it.id_producto.equals(order.id_producto))
                isExistProduct = true
        }
        if (isExistProduct)
            Toast.makeText(binder.root.context, R.string.no_se_puede_agregar_a_la_lista,Toast.LENGTH_SHORT).show()
        else {
            orderList.add(order)
            notifyItemInserted(orderList.size-1)
        }
    }

    class AddedOrder(binding: ListviewOrderedProductLayoutBinding, addedItemsRecyclerAdapter: AddedItemsRecyclerAdapter) : RecyclerView.ViewHolder(binding.root) {

        private var binder = binding
        private var positionInOrderList = 0
        private var minusButton = binding.removeItemButton
        private var adapter = addedItemsRecyclerAdapter

        fun bind(measure: String, order: Order, position: Int, orderList: MutableList<Order>) {
           positionInOrderList = position
           binder.weightorunit = measure
           binder.order = order
           minusButton.setOnClickListener {
               orderList.remove(order)
               adapter.notifyDataSetChanged()
           }
        }
    }
}