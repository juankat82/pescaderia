package com.juan.pescaderia.fragments

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.juan.pescaderia.R
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.viewmodel.MyViewModel
import kotlinx.coroutines.*
import retrofit2.await
import java.util.regex.Pattern



private const val CIF_REGEX = "^([ABCDEFGHJKLMNPQRSUVW])([-])(\\d{7})([0-9A-J])\$"
private const val EMAIL_REGEX = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})\$"


class UserRegistrationFragment : Fragment() {

    private lateinit var questionSpinner: AppCompatSpinner
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var button: Button
    private lateinit var cifEditText: AppCompatEditText
    private lateinit var nameEditText: AppCompatEditText
    private lateinit var surnameEditText: AppCompatEditText
    private lateinit var addressEditText: AppCompatEditText
    private lateinit var tfnEditText: AppCompatEditText
    private lateinit var emailEditText: AppCompatEditText
    private lateinit var passwordEditText: AppCompatEditText
    private lateinit var repeatPasswordEditText: AppCompatEditText
    private lateinit var passRecoverAnswerEditText: AppCompatEditText
    private lateinit var userRegistrationBaseLayout:ConstraintLayout
    private lateinit var conManager: ConnectivityManager
    private lateinit var preferencesController:SharedPreferencesController
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null
    private var cif = ""
    private var name = ""
    private var surname = ""
    private var address = ""
    private var phone = ""
    private var email = ""
    private var password = ""
    private var confirmPassword = ""
    private var recoverQuestion = ""
    private var recoverAnswer = ""
    private var queryService: QueryService? = null
    private val myViewModel: MyViewModel by viewModels()
    private var users:List<User> = listOf()
    private lateinit var defaultUser:User
    private lateinit var progressBarLayout: LinearLayout
    private lateinit var baseLayout: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_user_registration_layout,container, false)
        baseLayout = view.findViewById(R.id.user_registration_layout)
        progressBarLayout = view.findViewById(R.id.progress_bar_layout_register_screen)
        preferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        defaultUser = preferencesController.getUserData()
        queryService = QueryService.getInstance(view.context)
        conManager =requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netInfo = conManager.activeNetworkInfo
        initializeViews(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        netInfo = conManager.activeNetworkInfo
        isNetActive = checkNetActive()
        if (isNetActive)
            myViewModel.loadUserList(null)
        else
            Toast.makeText(requireContext(), R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
    }
    internal fun reloadUserList(){
        viewLifecycleOwner.lifecycleScope.launch {
            queryService!!.retrofit.getAllUsers().await()
        }
    }

    internal fun initializeViews(view:View) {

        userRegistrationBaseLayout = view.findViewById(R.id.user_registration_layout)
        userRegistrationBaseLayout.setOnFocusChangeListener { _, _ ->
            if (userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        button = view.findViewById(R.id.accept_button)
        button.setOnFocusChangeListener { _, _ ->
            if (userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }
        button.setOnClickListener {
            netInfo = conManager.activeNetworkInfo
            isNetActive = checkNetActive()
            if (isNetActive)
                doAction()
            else
                Toast.makeText(requireContext(), R.string.no_internet_disponible, Toast.LENGTH_SHORT).show()
        }

        cifEditText = view.findViewById(R.id.cif_usuario)
        cifEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        nameEditText = view.findViewById(R.id.nombre_usuario)
        nameEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        surnameEditText = view.findViewById(R.id.apellidos_usuario)
        surnameEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        addressEditText = view.findViewById(R.id.direccion_usuario)
        addressEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        tfnEditText = view.findViewById(R.id.telefono_usuario)
        tfnEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        emailEditText = view.findViewById(R.id.email_usuario)
        emailEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        passwordEditText = view.findViewById(R.id.contrasena_usuario)
        passwordEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        repeatPasswordEditText = view.findViewById(R.id.repite_contrasena_usuario)
        repeatPasswordEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        passRecoverAnswerEditText = view.findViewById(R.id.respuesta_recuperacion_contrasena)
        passRecoverAnswerEditText.setOnFocusChangeListener { _, _ ->
            if (!userRegistrationBaseLayout.hasFocus())
                hideKeyboard(view)
        }

        questionSpinner = view.findViewById(R.id.pregunta_recuperacion_contrasena_spinner)
        spinnerAdapter = ArrayAdapter(view.context, R.layout.array_adapter_layout, resources.getStringArray(R.array.spinner_questions))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionSpinner.adapter = spinnerAdapter
        questionSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                recoverQuestion = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    internal fun doAction() {
        var counter = 0
        reloadUserList()
        cif = cifEditText.text.toString()
        name = nameEditText.text.toString()
        surname = surnameEditText.text.toString()
        address = addressEditText.text.toString()
        phone = tfnEditText.text.toString()
        email = emailEditText.text.toString()
        password = passwordEditText.text.toString()
        confirmPassword = repeatPasswordEditText.text.toString()
        recoverAnswer = passRecoverAnswerEditText.text.toString()

        if (cif.isBlank() || cif.isEmpty() || !cifIsCorrect(cif)) {
            cifEditText.setText("")
            cifEditText.hint="CIF no valido"
            counter ++
        }
        if (name.isBlank() || name.isEmpty()) {
            nameEditText.setText("")
            nameEditText.hint="Nombre no valido"
            counter ++
        }
        if (surname.isBlank() || surname.isEmpty()) {
            surnameEditText.setText("")
            surnameEditText.hint="Apellido no valido"
            counter ++
        }
        if (address.isBlank() || address.isEmpty()) {
            addressEditText.setText("")
            addressEditText.hint="Direccion no valida"
            counter ++
        }
        if (phone.isBlank() || phone.isEmpty()) {
            tfnEditText.setText("")
            tfnEditText.hint="Telefono no valido"
            counter ++
        }
        if (email.isBlank() || email.isEmpty() || !isEmailAddressValid(email)) {
            emailEditText.setText("")
            emailEditText.hint="Email no valido"
            counter ++
        }
        if (password.isBlank() || password.isEmpty()) {
            passwordEditText.setText("")
            repeatPasswordEditText.setText("")
            passwordEditText.hint="Contrasena no valida"
            counter ++
        }
        if (!confirmPassword.equals(password)) {
            repeatPasswordEditText.setText("")
            passwordEditText.setText("")
            repeatPasswordEditText.hint="Las contrasenas no coinciden"
            counter ++
        }
        if (recoverQuestion.equals(resources.getStringArray(R.array.spinner_questions)[0])) {
            passRecoverAnswerEditText.setText("")
            passRecoverAnswerEditText.hint="Comprueba pregunta y respuesta"
            counter ++
        }
        if (recoverAnswer.isBlank() || recoverAnswer.isEmpty()) {
            passRecoverAnswerEditText.setText("")
            passRecoverAnswerEditText.hint="Comprueba pregunta y respuesta"
            counter ++
        }

        if (counter == 0)
        {
            val newUser = User(null,cif,cif,name,surname,address,phone,email,password,recoverQuestion,recoverAnswer,true)

            baseLayout.alpha = 0.5F
            progressBarLayout.visibility = View.VISIBLE
            if (!defaultUser.user_id.equals("-1"))
                queryService!!.createUserInRemoteDatabase(newUser,requireActivity(),0,progressBarLayout,baseLayout)
            else
                queryService!!.createUserInRemoteDatabase(newUser,requireActivity(),1,progressBarLayout,baseLayout)
        }
    }

    internal fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }

    override fun onStop() {
        super.onStop()
        queryService?.compositeDisposable?.clear()
    }

    internal fun isEmailAddressValid(email: String) : Boolean{
        if (validateAddress(email))
            return true
        return false
    }
    internal fun validateAddress(email:String) : Boolean {
        val pattern = Pattern.compile(EMAIL_REGEX)
        return pattern.matcher(email).matches()
    }
    internal fun cifIsCorrect(cif:String) : Boolean {
        val pattern:Pattern = Pattern.compile(CIF_REGEX)
        return pattern.matcher(cif).matches()
    }
    internal fun hideKeyboard(view:View) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }
}