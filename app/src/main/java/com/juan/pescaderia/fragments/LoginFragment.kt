package com.juan.pescaderia.fragments

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.emailjavaclasses.MailSender
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.viewmodel.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

private const val ADMIN_EMAIL = ""
private const val ADMIN_CIF = ""

class LoginFragment : Fragment() {

    private lateinit var doLoginTextView:TextView
    private lateinit var recoverPasswordTextView:TextView
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText:EditText
    private lateinit var loginButton: Button
    private lateinit var backButton: Button
    private lateinit var acceptButton:Button
    private lateinit var acceptAnswerButton:Button
    private lateinit var acceptCodeButton:Button
    private lateinit var codeTextView:TextView
    private lateinit var codeEditText: EditText
    private lateinit var forgottenPasswordTextView:TextView
    private lateinit var typeEmailTextView: TextView
    private lateinit var navController: NavController
    private lateinit var cifTextView:TextView
    private lateinit var cifEditText: EditText
    private lateinit var recoverQuestionTextView:TextView
    private lateinit var recoverQuestionText:TextView
    private lateinit var respuestaEditTextView: EditText
    private lateinit var textResetPassword:TextView
    private lateinit var typeNewPasswordTextView: TextView
    private lateinit var typeNewPasswordEditText: EditText
    private lateinit var reTypeNewPasswordTextView: TextView
    private lateinit var reTypeNewPasswordEditText: EditText
    private lateinit var enterNewPasswordButton: Button
    private lateinit var baseLayout:ConstraintLayout
    private lateinit var userList:List<User>
    private val myViewModel: MyViewModel by viewModels()
    private val defaultUser = User("","-1","","","","","","","","","", true)
    private var user:User = defaultUser
    private lateinit var sharedPreferencesController: SharedPreferencesController
    private var restorePasswordToken:String = ""
    private lateinit var queryService:QueryService
    private lateinit var conManager: ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null
    private lateinit var progressBarLayout:LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_layout,container, false)
        queryService = QueryService.getInstance(requireContext())!!
        navController = findNavController()
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        conManager =requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        baseLayout = view.findViewById(R.id.login_base_layout)
        baseLayout.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            if (baseLayout.hasFocus())
                hideKeyboard(view)
        }
        progressBarLayout = view.findViewById(R.id.progress_order_bar_layout_login_screen)
        doLoginTextView = view.findViewById(R.id.haz_login_text_view)
        recoverPasswordTextView = view.findViewById(R.id.recuperar_password_text_view)
        recoverQuestionTextView = view.findViewById(R.id.pregunta_recuperacion_textView)
        recoverQuestionText = view.findViewById(R.id.pregunta_recuperacion_text)
        cifTextView = view.findViewById(R.id.cif_text_view)
        cifEditText = view.findViewById(R.id.cif_edittext)
        emailEditText = view.findViewById(R.id.name_edittext)
        typeEmailTextView = view.findViewById(R.id.escribe_email_text_view)
        passwordEditText = view.findViewById(R.id.password_edittext)
        respuestaEditTextView = view.findViewById(R.id.respuesta_edittext)
        acceptCodeButton = view.findViewById(R.id.aceptar_codigo_button)
        codeTextView = view.findViewById(R.id.escriba_codigo_textview)
        codeEditText = view.findViewById(R.id.escriba_codigo_edittext)
        loginButton = view.findViewById(R.id.login_button)
        textResetPassword = view.findViewById(R.id.intro_nuevas_contrasenas_textview)
        typeNewPasswordTextView = view.findViewById(R.id.escriba_nuevo_password_textview)
        typeNewPasswordEditText = view.findViewById(R.id.escriba_nuevo_password_edittext)
        reTypeNewPasswordTextView = view.findViewById(R.id.repita_nuevo_password_textview)
        reTypeNewPasswordEditText = view.findViewById(R.id.repita_nuevo_password_edittext)
        enterNewPasswordButton  = view.findViewById(R.id.boton_cambio_contrasena)


        loginButton.setOnClickListener {
            hideKeyboard(view)
            if (emailEditText.text.isEmpty() || passwordEditText.text.isEmpty()) {
                Toast.makeText(requireContext(), R.string.email_and_password_required, Toast.LENGTH_SHORT).show()
            } else {
                val loginFunction = { userList:List<User> -> checkAndSetUser(userList) }
                netInfo = conManager.activeNetworkInfo
                isNetActive = checkNetActive()

                if (isNetActive)
                {
                    baseLayout.alpha = 0.5F
                    progressBarLayout.visibility = View.VISIBLE
                    queryService.locateAndSetUserInSystem(loginFunction,progressBarLayout,baseLayout)

                }
                else
                    Toast.makeText(requireContext(), R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
            }
        }

        backButton = view.findViewById(R.id.cancelar_button)
        backButton.setOnClickListener { requireActivity().onBackPressed() }
        acceptButton = view.findViewById(R.id.boton_aceptar)
        acceptButton.setOnClickListener {

            val findUserFunction = { list:List<User> -> requestUser(list) }
            netInfo = conManager.activeNetworkInfo
            isNetActive = checkNetActive()

            if (isNetActive)
                queryService.findUserToRecover(findUserFunction)
            else
                Toast.makeText(requireContext(), R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
        }
        acceptAnswerButton = view.findViewById(R.id.boton_aceptar_respuesta)
        acceptAnswerButton.setOnClickListener {
            restorePasswordToken = System.currentTimeMillis().toString()
            sharedPreferencesController.setToken(restorePasswordToken)

            if (user.recoverAnswer.toLowerCase(Locale.getDefault()).equals(respuestaEditTextView.text.toString().toLowerCase(Locale.getDefault()))) {
                netInfo = conManager.activeNetworkInfo
                isNetActive = checkNetActive()
                if (isNetActive){
                    progressBarLayout.visibility = View.VISIBLE
                    baseLayout.alpha = 0.5F
                    GlobalScope.launch(Dispatchers.IO) {
                        val to = user.email
                        val from = SharedPreferencesController.getCompanyEmail()
                        val subject = "Recuperar contrasena pescaderia"
                        val body = "Copie el siguiente numero y peguelo en su aplicacion para poder restablecer su contrasena olvidada: \nNUMERO:   $restorePasswordToken"
                        GlobalScope.launch(Dispatchers.IO) {
                            val sender = MailSender()
                            sender.sendMail(subject, body, from, to, requireContext()).runCatching {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (this) {
                                        visibleUIAfterPassRecovery()
                                        Toast.makeText(
                                            requireContext(),
                                            R.string.codigo_enviado,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        coroutineContext.cancel()
                                    } else
                                        coroutineContext.cancel()
                                }, 200L)
                            }
                        }
                    }
                }
                else
                    Toast.makeText(requireContext(), R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(requireContext(), R.string.respuesta_no_coincide, Toast.LENGTH_SHORT)
                    .show()
        }
        enterNewPasswordButton.setOnClickListener {
            val newPassword = typeNewPasswordEditText.text.trim().toString()
            val newRepeatPassword = reTypeNewPasswordEditText.text.trim().toString()
            netInfo = conManager.activeNetworkInfo
            isNetActive = checkNetActive()
            if (isNetActive) {
                if (newPassword.equals(newRepeatPassword)) {
                    progressBarLayout.visibility = View.VISIBLE
                    baseLayout.alpha = 0.5F
                    user.apply {
                        password = newPassword
                    }
                    Toast.makeText(requireContext(), R.string.contrasena_cambiada_con_exito, Toast.LENGTH_SHORT).show()
                    sharedPreferencesController.setUserData(user)
                    sharedPreferencesController.setToken("")
                    queryService.updateUserData(user._id!!, user,progressBarLayout,baseLayout,requireActivity())
                    myViewModel.getAllUsers()
                } else
                    Toast.makeText(requireContext(), R.string.contrasena_no_coincide, Toast.LENGTH_SHORT).show()
            }else
                Toast.makeText(requireContext(), R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
        }
        acceptCodeButton.setOnClickListener {
            if (codeEditText.text.toString().trim().equals(restorePasswordToken)) {
                doLoginTextView.visibility = View.INVISIBLE
                recoverPasswordTextView.visibility = View.INVISIBLE
                emailEditText.visibility = View.INVISIBLE
                passwordEditText.visibility = View.INVISIBLE
                loginButton.visibility = View.INVISIBLE
                acceptButton.visibility = View.INVISIBLE
                acceptAnswerButton.visibility = View.INVISIBLE
                acceptCodeButton.visibility = View.INVISIBLE
                codeTextView.visibility = View.INVISIBLE
                codeEditText.visibility = View.INVISIBLE
                forgottenPasswordTextView.visibility = View.INVISIBLE
                typeEmailTextView.visibility = View.INVISIBLE
                cifTextView.visibility = View.INVISIBLE
                cifEditText.visibility = View.INVISIBLE
                recoverQuestionTextView.visibility = View.INVISIBLE
                recoverQuestionText.visibility = View.INVISIBLE
                respuestaEditTextView.visibility = View.INVISIBLE
                textResetPassword.visibility = View.VISIBLE
                typeNewPasswordTextView.visibility = View.VISIBLE
                typeNewPasswordEditText.visibility = View.VISIBLE
                reTypeNewPasswordTextView.visibility = View.VISIBLE
                reTypeNewPasswordEditText.visibility = View.VISIBLE
                enterNewPasswordButton.visibility = View.VISIBLE
            }
            else
                Toast.makeText(requireContext(), R.string.codigo_no_coincide, Toast.LENGTH_SHORT).show()
        }
        forgottenPasswordTextView = view.findViewById(R.id.olvido_password_text_view)
        forgottenPasswordTextView.setOnClickListener {
            passwordEditText.visibility = View.INVISIBLE
            passwordEditText.visibility = View.INVISIBLE
            doLoginTextView.visibility = View.INVISIBLE
            forgottenPasswordTextView.visibility = View.INVISIBLE
            loginButton.visibility = View.INVISIBLE
            typeEmailTextView.visibility = View.VISIBLE
            cifTextView.visibility = View.VISIBLE
            cifEditText.visibility = View.VISIBLE
            acceptButton.visibility = View.VISIBLE
            recoverPasswordTextView.visibility = View.VISIBLE
            userList = myViewModel.getAllUsers()
        }

        return view
    }

    fun getPasswordToken () = restorePasswordToken

    internal fun requestUser(_userList:List<User>)
    {
        val email = emailEditText.text.trim().toString()
        val cif = cifEditText.text.trim().toString()

        _userList.forEach {
            if (it.email.trim().equals(email) && it.cif.trim().equals(cif))
                user = it
        }

        if (!user.user_id.equals("-1")) {
            recoverQuestionTextView.visibility = View.VISIBLE
            recoverQuestionText.visibility = View.VISIBLE
            recoverQuestionText.text = user.recoverQuestion
            respuestaEditTextView.visibility = View.VISIBLE
            respuestaEditTextView.visibility = View.VISIBLE
            acceptButton.visibility = View.INVISIBLE
            acceptAnswerButton.visibility = View.VISIBLE
        }
        else
            Toast.makeText(requireContext(), R.string.usuario_desconocido, Toast.LENGTH_SHORT).show()
    }
    internal fun checkAndSetUser(_userList: List<User>)
    {
        val enableMenuBar: EmptyFragment.IEnableMenuBar = activity as EmptyFragment.IEnableMenuBar
        val enabledItems: EmptyFragment.IEnableOrDisableMenuItems = activity as EmptyFragment.IEnableOrDisableMenuItems
        var enabledMenuItems:MutableMap<Int,Boolean>

        var found = false
        val userList = _userList
        val email = emailEditText.text.toString()
        val pass = passwordEditText.text.toString()
        progressBarLayout.visibility = View.GONE
        userList.forEach {
            if (it.email.equals(email) && it.password.equals(pass) && !found) {
                found = true
                if (found) {
                    sharedPreferencesController.setUserData(it)

                    if (!it.name.equals("admin") && !it.email.equals(ADMIN_EMAIL) && !it.cif.equals(ADMIN_CIF))
                    {
                        val userCanPlaceOrders = user.approvedByAdmin
                        val arrayOptions:Array<Boolean> = arrayOf(true,false,true,true,true,userCanPlaceOrders,false,false,false)
                        enabledMenuItems = enableMenuBarOptions(false,arrayOptions)
                    }
                    else
                        enabledMenuItems = enableMenuBarOptions(true, arrayOf())

                    enabledItems.enableDisableItems(enabledMenuItems)
                    enableMenuBar.enableBar(true)
                }
            }
        }
        if (found) {
            requireActivity().onBackPressed()
            Toast.makeText(requireContext(), "${getText(R.string.welcome)} ${email.split("@")[0]}!", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.empty_fragment)
        } else
            Toast.makeText(requireContext(), R.string.user_doesnt_exist, Toast.LENGTH_SHORT).show()
    }
    internal fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }
    internal fun visibleUIAfterPassRecovery() {
        acceptAnswerButton.visibility = View.INVISIBLE
        respuestaEditTextView.visibility = View.INVISIBLE
        recoverQuestionTextView.visibility = View.INVISIBLE
        recoverQuestionText.visibility = View.INVISIBLE
        acceptCodeButton.visibility = View.VISIBLE
        codeTextView.visibility = View.VISIBLE
        codeEditText.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            progressBarLayout.visibility = View.GONE
            baseLayout.alpha = 1.0F
            Thread.sleep(1500)
        },200L)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
         myViewModel.loadUserList(null)
    }

    internal fun hideKeyboard(view:View) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    internal fun enableMenuBarOptions(isAdmin:Boolean,_arrayOptions:Array<Boolean>) : MutableMap<Int,Boolean> {

        val changeUserDataEnabled = R.id.nav_change_user_data
        val createNewUserEnabled = R.id.nav_create_new_user
        val lastOrderEnabled = R.id.nav_read_order
        val basketEnabled = R.id.nav_basket
        val loginEnabled = R.id.nav_login_logout
        val adminUsersEnabled = R.id.nav_admin_users
        val adminOrdersEnabled = R.id.nav_admin_orders
        val adminProductsEnabled = R.id.nav_admin_products
        val createNewOrderEnabled = R.id.nav_new_order
        val map = mutableMapOf<Int,Boolean>()
        val list:List<Int> = mutableListOf(changeUserDataEnabled, createNewUserEnabled, createNewOrderEnabled, lastOrderEnabled, basketEnabled, loginEnabled, adminUsersEnabled, adminOrdersEnabled,adminProductsEnabled)
        map.clear()

        if (isAdmin) {
            for (i in list)
                map.put(i,true)
        }
        else
        {
            for (i in list.indices)
                map.put(list[i],_arrayOptions[i])
        }

        return map
    }
}
