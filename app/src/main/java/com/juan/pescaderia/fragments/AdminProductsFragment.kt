package com.juan.pescaderia.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.Product
import com.juan.pescaderia.persistence.productdatabase.RoomRepository
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.recyclerviewclasses.AdminProductsRecyclerAdapter
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.viewmodel.MyViewModel
import kotlinx.coroutines.*

class AdminProductsFragment : Fragment() {

    private val viewModel:MyViewModel by viewModels()
    private var productList:List<Product> = emptyList()
    private lateinit var viewPager:ViewPager2
    private lateinit var productAdminRecyclerAdapter:AdminProductsRecyclerAdapter
    private lateinit var adminProductsBaseLayout:ConstraintLayout
    private lateinit var adminProductsBaseLayoutLoader:ConstraintLayout
    private lateinit var addNewProductButton: FloatingActionButton
    private var repo: RoomRepository? = null
    private var queryService: QueryService? = null
    private lateinit var inflator:LayoutInflater
    private lateinit var sharedPreferencesController: SharedPreferencesController
    private lateinit var thisFragment:AdminProductsFragment
    private lateinit var progressBarLayout:LinearLayout
    private lateinit var conManager:ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.admin_products_layout,container,false)
        inflator = inflater
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        thisFragment = this
        progressBarLayout = view.findViewById(R.id.progress_bar_layout)
        repo = RoomRepository.getInstance(requireContext())
        queryService = QueryService.getInstance(requireContext())
        adminProductsBaseLayout = view.findViewById(R.id.admin_product_base_layout)
        adminProductsBaseLayoutLoader = view.findViewById(R.id.admin_product_base_layout2)
        adminProductsBaseLayout.setOnFocusChangeListener { _, _ ->
            if (adminProductsBaseLayout.hasFocus())
                hideKeyboard(view)
        }
        viewPager = view.findViewById(R.id.product_viewpager)
        viewModel.loadAllProducts(false)
        productList = viewModel.getProductList()
        productAdminRecyclerAdapter = AdminProductsRecyclerAdapter(productList)
        viewPager.adapter = productAdminRecyclerAdapter
        runBlocking {
            if (productList.isEmpty()) {
                viewModel.loadAllProductsRemote(false)
                productList = viewModel.getProductList()
                productAdminRecyclerAdapter.setProductList(productList)
                productAdminRecyclerAdapter.notifyDataSetChanged()
            }
        }
        if (productList.isEmpty())
            Toast.makeText(requireContext(), R.string.no_productos_disponibles, Toast.LENGTH_SHORT).show()

        addNewProductButton = view.findViewById(R.id.add_new_product)
        enableViewListeners()
        conManager =requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        return view
    }

    fun enableViewListeners() {

        adminProductsBaseLayoutLoader.alpha =1.0F

        if (adminProductsBaseLayoutLoader.alpha == 1.0F)
            progressBarLayout.visibility = View.GONE

        if (!viewPager.isVisible)
            viewPager.visibility = View.VISIBLE

        if (adminProductsBaseLayoutLoader.alpha == 1.0F) {
            addNewProductButton.setOnClickListener {

                netInfo = conManager.activeNetworkInfo
                isNetActive = checkNetActive()
                if (isNetActive) {
                    val myView = inflator.inflate(R.layout.alert_dialog_layout, null)
                    val newProductView = AlertDialog.Builder(requireContext()).create()
                    newProductView.setView(myView)
                    newProductView.show()

                    newProductView.findViewById<ImageButton>(R.id.accept_button).setOnClickListener {
                            netInfo = conManager.activeNetworkInfo
                            isNetActive = checkNetActive()

                            if (isNetActive){
                                var counter = 0
                                val nombreET = myView.findViewById<AppCompatEditText>(R.id.new_dialog_nombre)
                                val precioET = myView.findViewById<AppCompatEditText>(R.id.new_dialog_precio)
                                val radioGroupUnitOrWeight = myView.findViewById<RadioGroup>(R.id.new_kg_unit_radio_group)
                                if (nombreET?.text.toString().isEmpty() || nombreET?.text.toString().isBlank()) {
                                    Toast.makeText(requireContext(), R.string.nombre_vacio, Toast.LENGTH_SHORT).show()
                                    counter += 1
                                }
                                if (precioET?.text.toString().isEmpty() || precioET?.text.toString().isBlank()) {
                                    Toast.makeText(requireContext(), R.string.precio_vacio, Toast.LENGTH_SHORT).show()
                                    counter += 1
                                }

                                val whichOption = radioGroupUnitOrWeight?.checkedRadioButtonId
                                if (whichOption != R.id.new_radio_unit && whichOption != R.id.new_radio_weight) {
                                    Toast.makeText(requireContext(), R.string.no_unidad_o_peso, Toast.LENGTH_SHORT).show()
                                    counter++
                                }
                                if (counter == 0) {
                                    val nombre = nombreET?.text.toString()
                                    val precio = precioET?.text.toString().toFloat()
                                    val unit: Boolean
                                    val weight: Boolean
                                    if (whichOption == R.id.new_radio_unit) {
                                        unit = true
                                        weight = false
                                    } else {
                                        unit = false
                                        weight = true
                                    }
                                    val product = Product(null, (productAdminRecyclerAdapter.getProductList().size).toString(), nombre, weight, precio, unit)
                                    disableViewListeners()
                                    queryService?.putProductListInDatabase(product, productAdminRecyclerAdapter.getProductList(), productAdminRecyclerAdapter, thisFragment)
                                    newProductView.dismiss()
                                }
                                else{
                                    Toast.makeText(requireContext(), R.string.fallo_al_crear_producto, Toast.LENGTH_SHORT).show()
                                }
                            }
                            else
                                Toast.makeText(context, R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()

                        }
                    newProductView.findViewById<ImageButton>(R.id.cancel_button).setOnClickListener { newProductView.dismiss() }
                }
                else
                    Toast.makeText(context, R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun disableViewListeners(){
        val isLoading = sharedPreferencesController.getLoadingProductsScreenValue()
        if (isLoading) {
            adminProductsBaseLayoutLoader.alpha = 0.5F
            viewPager.visibility = View.GONE
            progressBarLayout.visibility = View.VISIBLE
            addNewProductButton.setOnClickListener(null)
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
}