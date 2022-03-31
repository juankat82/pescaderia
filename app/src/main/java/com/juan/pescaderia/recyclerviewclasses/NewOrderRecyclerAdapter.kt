package com.juan.pescaderia.recyclerviewclasses

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.OrderProductViewholderBinding
import com.juan.pescaderia.dataclasses.Order
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
private val dateTimeFormat = SimpleDateFormat("dd_MM_yyyy")
private val calendar = Calendar.getInstance().time

class NewOrderRecyclerAdapter(_productList: List<Product>, _orderListViewAdapter: AddedItemsRecyclerAdapter) : RecyclerView.Adapter<NewOrderRecyclerAdapter.ProductHolder>() {

    private var productList = _productList
    private val orderListViewAdapter = _orderListViewAdapter
    private lateinit var sharedPreferencesController: SharedPreferencesController
    lateinit var user: User
    private var orderId:String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProductHolder {
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(parent.context)!!
        user = sharedPreferencesController.getUserData()!!
        if (sharedPreferencesController.getSentFlag())
            sharedPreferencesController.setNewOrderId(System.currentTimeMillis().toString())
        orderId = sharedPreferencesController.getNewOrderId()

        val inflater = LayoutInflater.from(parent.context)
        val binding = OrderProductViewholderBinding.inflate(inflater)
        return ProductHolder(orderId,binding,orderListViewAdapter,user)
    }

    override fun getItemCount() = productList.size


    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(productList[position])
    }

    class ProductHolder(_orderId:String,binding: OrderProductViewholderBinding, _orderListView: AddedItemsRecyclerAdapter, _user: User) : RecyclerView.ViewHolder(binding.root) {

        private val orderId = _orderId
        private val user =_user
        private val binder = binding
        private lateinit var amountEditText:EditText
        private lateinit var valorTotalTextview:TextView
        private var orderListViewAdapter = _orderListView

        fun bind(product:Product) {
            binder.producto = product
            binder.executePendingBindings()
            amountEditText = binder.cantidadDeseadaEdittext
            valorTotalTextview = binder.valorTotalTextview
            amountEditText.addTextChangedListener( object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty())
                        valorTotalTextview.text = "0.0"
                    else
                        valorTotalTextview.text = (product.precio.toDouble()*amountEditText.text.toString().toDouble()).toString()
                }

            })

            binder.botonMas.setOnClickListener {
                amountEditText.setText(
                    (amountEditText.text.toString().toDouble() + 1.0).toString()
                )
            }
            binder.botonMenos.setOnClickListener {
                val amount = amountEditText.text.toString().toDouble() - 1
                if (amount > -1)
                    amountEditText.setText(
                        (amountEditText.text.toString().toDouble() - 1.0).toString()
                    )
            }
            binder.addToOrderButton.setOnClickListener {
                val nombre = product.nombre
                val amount = amountEditText.text.toString().toFloat()
                val precio = product.precio
                val precioTotal = amount * precio
                val cif = user.cif
                val date = dateTimeFormat.format(calendar.time)
                val productId = product.foreign_id
                val order =
                    Order(null,date, orderId, cif, nombre, amount, precio, precioTotal, productId)
                if (amount > 0) {
                    hideKeyboard(binder.root)
                    orderListViewAdapter.addNewOrder(order)
                } else
                    Toast.makeText(
                        binder.root.context,
                        R.string.no_puede_a_cero,
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
        private fun hideKeyboard(view:View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}