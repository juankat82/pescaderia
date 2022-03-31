package com.juan.pescaderia.recyclerviewclasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.databinding.LastOrderViewholderBinding
import com.juan.pescaderia.dataclasses.Order


class LastOrderRecyclerViewAdapter(_orderList:List<Order>) : RecyclerView.Adapter<LastOrderRecyclerViewAdapter.LastOrderViewHolder>() {

    private val orderList = _orderList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LastOrderViewholderBinding.inflate(inflater)
        return LastOrderViewHolder(binding)
    }

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(holder: LastOrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    class LastOrderViewHolder(binding: LastOrderViewholderBinding) : RecyclerView.ViewHolder(binding.root ) {

        private val bind = binding

        fun bind(order:Order) {
            bind.order = order
            bind.executePendingBindings()
        }
    }
}