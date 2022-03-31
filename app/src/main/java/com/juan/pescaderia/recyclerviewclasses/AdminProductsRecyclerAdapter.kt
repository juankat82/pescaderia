package com.juan.pescaderia.recyclerviewclasses

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.juan.pescaderia.R
import com.juan.pescaderia.R.string.no
import com.juan.pescaderia.databinding.ProductViewholderLayoutBinding
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.persistence.productdatabase.ProductDatabase
import com.juan.pescaderia.retrofit.retrofit.QueryService
import kotlinx.coroutines.*
import java.lang.Exception

class AdminProductsRecyclerAdapter(_productList:List<Product>) : RecyclerView.Adapter<AdminProductsRecyclerAdapter.ProductViewHolder>() {

    private var productList:MutableList<Product> = _productList.toMutableList()
    private lateinit var conManager:ConnectivityManager
    var isNetActive:Boolean = true
    private var netInfo:NetworkInfo? = null
    private lateinit var context:Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context=recyclerView.context
        conManager = recyclerView.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        conManager.addDefaultNetworkActiveListener {
            ConnectivityManager.OnNetworkActiveListener {
                isNetActive = true
                Toast.makeText(context, R.string.vuelve_internet, Toast.LENGTH_SHORT).show()
            }
        }

        super.onAttachedToRecyclerView(recyclerView)
    }
    fun testTheNet() = runBlocking{
        val connectionChecker = GlobalScope.launch {
            val connected:Boolean = try{
                conManager.activeNetworkInfo!!.isConnected
            }
            catch (e:Exception)
            {
                false
            }
            isNetActive = connected
        }
        connectionChecker.join()

        if (netInfo != null) conManager.addDefaultNetworkActiveListener {
            isNetActive = true
            Toast.makeText(context, R.string.vuelve_internet, Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val v = ProductViewHolder(ProductViewholderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),this)
        v.binder.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return v
    }

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position],position)
    }

    fun getProductList() = productList

    fun setProductList(list:List<Product>) {
        productList = list.toMutableList()
    }
    class ProductViewHolder(binding: ProductViewholderLayoutBinding, adminProductsRecyclerAdapter: AdminProductsRecyclerAdapter) : RecyclerView.ViewHolder(binding.root) {
        private val adapter = adminProductsRecyclerAdapter
        private val context = binding.root.context
        val binder = binding
        private var nameEditText = binder.nombreProducto
        private var precioEditText = binder.precioProducto
        private var radioGroup = binder.kgUnitRadioGroup
        private var eraseItemButton = binder.eraseItemButton
        private var updateItemButton = binder.updateItemButton
        private var database = ProductDatabase.getDatabase(context)
        private var queryService = QueryService.getInstance(context)

        fun bind(product: Product, _position: Int) {
            binder.product = product
            binder.executePendingBindings()
            var name = product.nombre
            var precio = product.precio
            var option = radioGroup.checkedRadioButtonId.toString()

            nameEditText.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isBlank() || s.toString().isEmpty())
                        name = ""
                    else
                        name = s.toString()
                }
            })
            precioEditText.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isBlank() || s.toString().isEmpty())
                        precio = 0.0F
                    else
                        precio = s.toString().toFloat()
                }
            })
            radioGroup.setOnCheckedChangeListener { group, _ ->
                option = group.checkedRadioButtonId.toString()
            }

            eraseItemButton.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setPositiveButton(R.string.si) { dialog, _ ->
                        adapter.productList.removeAt(_position)
                        adapter.notifyDataSetChanged()
                        GlobalScope.async {
                            database!!.productDao().deleteProduct(product)
                            queryService!!.deleteAProductById(product._id!!)
                        }.start()
                        dialog!!.cancel()
                    }
                    setNegativeButton(no) { dialog, _ ->
                        dialog!!.cancel()
                    }
                }.setTitle(R.string.borrar_producto).setMessage(R.string.seguro_eliminar_producto)
                    .create().show()
            }

            updateItemButton.setOnClickListener {
                product.nombre = name
                product.precio = precio
                if (option.equals("2131296590")) {
                    product.peso_kg = true
                    product.unidad = false
                } else {
                    product.peso_kg = true
                    product.unidad = false
                }

                if (adapter.isNetActive) {
                    adapter.getProductList()[_position]= product
                    adapter.notifyDataSetChanged()
                    Toast.makeText(context, R.string.producto_actualizado, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    AlertDialog.Builder(context).apply {
                        setPositiveButton(R.string.si) { _, _ ->
                            adapter.testTheNet()
                        }
                        setNegativeButton(no) { dialog, _ ->
                            dialog!!.cancel()
                        }
                    }.setTitle(R.string.intentar_red_de_nuevo)
                        .setMessage(R.string.comprobar_conexion).create().show()
                }
            }
        }
    }
}
