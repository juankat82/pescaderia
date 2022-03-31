package com.juan.pescaderia.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.AlterUserDataBinding
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import java.util.regex.Pattern

private const val EMAIL_REGEX = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})\$"

class PersonalDataFragment : Fragment() {

    private lateinit var nameEditText:EditText
    private lateinit var surnameEditText:EditText
    private lateinit var addressEditText:EditText
    private lateinit var phoneEditText:EditText
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText:EditText
    private lateinit var repeatPasswordEditText:EditText
    private lateinit var questionSpinner: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var recoverAnswerEditText:EditText
    private lateinit var changeAcceptChangesButton:Button
    private lateinit var alterUserDataBinding:AlterUserDataBinding
    private lateinit var preferencesController: SharedPreferencesController
    private lateinit var user: User
    private lateinit var recoverQuestion:String
    private lateinit var alterFragmentLayout:ConstraintLayout
    private var position=0
    private var reviewText:String = ""
    private lateinit var queryService: QueryService
    private lateinit var progressBarLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        alterUserDataBinding = DataBindingUtil.inflate(inflater,R.layout.alter_user_data,container,false)
        val view = alterUserDataBinding.root
        alterFragmentLayout = alterUserDataBinding.alterFragmentLayout
        alterFragmentLayout.setOnFocusChangeListener { _, _ ->
            if (alterFragmentLayout.hasFocus())
                hideKeyboard(view)
        }
        progressBarLayout = alterUserDataBinding.alterMyDetailsBarLayout
        queryService = QueryService.getInstance(requireContext())!!
        preferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        user = preferencesController.getUserData()
        alterUserDataBinding.user = user
        configViews(view)
        return view
    }

    internal fun configViews(view:View) {
        recoverQuestion = user.recoverQuestion
        nameEditText = view.findViewById(R.id.cambia_nombre_edittext)
        surnameEditText = view.findViewById(R.id.cambia_apellido_edittext)
        addressEditText = view.findViewById(R.id.cambia_direccion_edittext)
        phoneEditText = view.findViewById(R.id.cambia_phone_edittext)
        emailEditText = view.findViewById(R.id.cambia_email_edittext)
        passwordEditText = view.findViewById(R.id.cambia_password_edittext)
        repeatPasswordEditText = view.findViewById(R.id.cambia_repite_password_edittext)
        questionSpinner = view.findViewById(R.id.cambia_pregunta_spinner)
        spinnerAdapter = ArrayAdapter(requireContext(),R.layout.change_arrayadapter_layout,resources.getStringArray(R.array.spinner_questions))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked)
        questionSpinner.adapter = spinnerAdapter
        position = 0
        for (i in 0 until questionSpinner.adapter.count) {
            if (user.recoverQuestion.equals(questionSpinner.adapter.getItem(i)))
                position = i
        }
        questionSpinner.setSelection(position)
        questionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                recoverQuestion = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                recoverQuestion = user.recoverQuestion
            }
        }
        recoverAnswerEditText = view.findViewById(R.id.cambia_recover_answer_edittext)
        changeAcceptChangesButton = view.findViewById(R.id.boton_cambiar_datos)
        changeAcceptChangesButton.setOnClickListener { doChangeUserData() }
    }

    internal fun doChangeUserData() {

        var counter = 0
        reviewText = ""
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val address = addressEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = repeatPasswordEditText.text.toString()
        val recoverAnswer = recoverAnswerEditText.text.toString()

        if (name.isBlank() || name.isEmpty()) {
            addText("Nombre no valido\n")
            counter++
        }
        if (surname.isBlank() || surname.isEmpty()) {
            addText("Apellido no valido\n")
            counter++
        }
        if (address.isBlank() || address.isEmpty()) {
            addText("Direccion no valida\n")
            counter++
        }
        if (phone.isBlank() || phone.isEmpty()) {
            addText("Telefono no valido\n")
            counter++
        }
        if (email.isBlank() || email.isEmpty() || !isEmailAddressValid(email)) {
            addText("Email no valido\n")
            counter++
        }
        if (password.isBlank() || password.isEmpty()) {
            addText("Contrasena no valida\n")
            counter++
        }
        if (!confirmPassword.equals(password)) {
            addText("Las contrasenas no coinciden\n")
            counter++
        }
        if (recoverQuestion.equals(resources.getStringArray(R.array.spinner_questions)[0])) {
            addText("Elija una pregunta de la lista\n")
            counter++
        }
        if (recoverAnswer.isBlank() || recoverAnswer.isEmpty()) {
            addText("Escriba una respuesta de seguridad\n")
            counter++
        }

        if (counter != 0)
            showErrorDialog(reviewText)
        else
        {
            val newUser = User(user._id,user.user_id,user.cif,name,surname,address,phone,email,password,recoverQuestion,recoverAnswer,true)

            if (user != newUser)
                processChange(newUser)
            else
                Toast.makeText(requireContext(),R.string.didnt_modify_user,Toast.LENGTH_SHORT).show()
        }

    }

    internal fun processChange(myUser:User) {

        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton(R.string.si) { dialog, _ ->
                alterFragmentLayout.alpha = 0.5F
                progressBarLayout.visibility = View.VISIBLE
                queryService.updateUserData(myUser._id!!, myUser, progressBarLayout,alterFragmentLayout ,requireActivity())
                preferencesController.setUserData(myUser)
                dialog.cancel()
            }
            setNegativeButton(R.string.no) { dialog, _ -> dialog!!.cancel() }
        }.setTitle(R.string.modifica_datos_usuario).setMessage(R.string.seguro_cambio).create().show()
    }

    internal fun addText(text:String) {
        reviewText.let {
            reviewText = it+text
        }
    }


    internal fun showErrorDialog(toastText:String) {
        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton(R.string.OK) { dialog, _ ->
                dialog!!.cancel()
            }
        }.setTitle(R.string.error_modificar_usuario).setMessage(toastText).create().show()
    }

    internal fun hideKeyboard(view:View) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
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
}