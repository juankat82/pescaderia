package com.juan.pescaderia.recyclerviewclasses

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.AdminOrderHolderBinding
import com.juan.pescaderia.dataclasses.FullOrderForAdmin
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.retrofit.retrofit.QueryService

class QueryOrdersRecyclerAdapter (_context: Context, _orderList:List<List<Order>>) : RecyclerView.Adapter<QueryOrdersRecyclerAdapter.SingleOrder> () {

    private val context = _context
    private var orderList = _orderList
    private var sortedOrderList: MutableList<FullOrderForAdmin> = mutableListOf()
    private lateinit var queryService: QueryService

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleOrder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdminOrderHolderBinding.inflate(inflater)
        queryService = QueryService.getInstance(context)!!
        val v = SingleOrder(binding,context,this,queryService)
        v.binder.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return v
    }

    override fun onBindViewHolder(holder: SingleOrder, position: Int) {
        holder.bind(sortedOrderList[position])
    }

    override fun getItemCount() = sortedOrderList.size

    fun resetList() {
        orderList = listOf(listOf())
        sortedOrderList = mutableListOf()
        notifyDataSetChanged()
    }

    fun getList() = sortedOrderList
    
    fun setList(_orderList:List<List<Order>>) {

        orderList = _orderList
        val differentDates = orderList.size
        var dateField:String
        var idField:String
        var quantityField = ""
        var cifField:String
        var nameField = ""
        var unitField = ""
        var totalField = ""
        var idProdField = ""

        if (differentDates == 1 ) {
            val onlyOrderList = orderList[0]
            dateField = onlyOrderList[0].date
            idField = onlyOrderList[0].order_id
            cifField = onlyOrderList[0].cif_cliente

            for (i in onlyOrderList.indices) {
                quantityField += "${onlyOrderList[i].cantidad}\n"
                nameField += "${onlyOrderList[i].nombre_producto}\n"
                unitField += "${onlyOrderList[i].precio}\n"
                totalField += "${onlyOrderList[i].precio_total}\n"
                idProdField += "${onlyOrderList[i].id_producto}\n"

            }
            sortedOrderList.add(FullOrderForAdmin(dateField, idField, cifField, nameField, quantityField, unitField))
        } else {
            for (i in 0 until differentDates) {
                val onlyOrderList = orderList[i]
                dateField = onlyOrderList[0].date
                idField = onlyOrderList[0].order_id
                cifField = onlyOrderList[0].cif_cliente

                for (j in onlyOrderList.indices) {
                    quantityField += "${onlyOrderList[j].cantidad}\n"
                    nameField += "${onlyOrderList[j].nombre_producto}\n"
                    unitField += "${onlyOrderList[j].precio}\n"
                    totalField += "${onlyOrderList[j].precio_total}\n"
                    idProdField += "${onlyOrderList[j].id_producto}\n"
                }
                sortedOrderList.add(FullOrderForAdmin(dateField, idField, cifField, nameField, quantityField, unitField))
            }
        }
    }

    class SingleOrder(binding: AdminOrderHolderBinding, _context: Context, queryOrdersRecyclerAdapter: QueryOrdersRecyclerAdapter,_queryService: QueryService) : RecyclerView.ViewHolder (binding.root) {

        val binder = binding
        val queryService = _queryService
        private lateinit var eraseOrderButton: ImageButton
        private val context = _context
        private val adapter = queryOrdersRecyclerAdapter

        fun bind(order: FullOrderForAdmin) {
            binder.date = order.date.replace("_", "/")
            binder.cif = order.quantity
            binder.name = order.name
            binder.unit = order.unitPrice
            binder.total = order.totalPrice
            var totalPricesList = order.totalPrice.replace("\n","-").split("-")
            totalPricesList = totalPricesList.subList(0,totalPricesList.size-1)

            var sum = 0.0F
            totalPricesList.forEach {
                sum += it.trim().toFloat()
            }
            binder.subtotal = sum.toString()
            eraseOrderButton = binder.deleteOrderButton
            eraseOrderButton.setOnClickListener {
                AlertDialog.Builder(context).setTitle(R.string.borrar_pedido_titulo)
                    .setMessage(R.string.seguro_eliminar_pedido)
                    .setPositiveButton(R.string.si) { _, _ ->
                        val chosenDate = order.date
                        val cifUser: String = order.quantity
                        val isInternetAvailable = checkInternet()
                        if (isInternetAvailable)
                            queryService.eraseOrderByDateAndUserCIF(chosenDate,cifUser,adapter,adapterPosition)
                        else
                            Toast.makeText(context, R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ -> dialog?.dismiss() }.show()
                    .create()
            }
        }

        private fun checkInternet() : Boolean {
            val conManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = conManager.activeNetworkInfo
            if (netInfo != null)
                return true
            return false
        }
    }
}
