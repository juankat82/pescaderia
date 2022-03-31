package com.juan.pescaderia.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juan.pescaderia.R
import com.juan.pescaderia.databinding.AdminUsersLayoutBinding
import com.juan.pescaderia.dataclasses.User
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController
import com.juan.pescaderia.retrofit.retrofit.QueryService
import com.juan.pescaderia.testhooks.IdlingEntity
import com.juan.pescaderia.viewmodel.MyViewModel
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.regex.Pattern

private const val CIF_REGEX = "^([ABCDEFGHJKLMNPQRSUVW])([-])(\\d{7})([0-9A-J])\$"
private const val EMAIL_REGEX = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})\$"

class AdminUsersFragment : Fragment() {

    private val viewModel: MyViewModel by viewModels()
    private lateinit var newAdminUsersLayoutBinding: AdminUsersLayoutBinding
    private var queryService:QueryService? = null
    private lateinit var userSpinner: Spinner
    private lateinit var recoverQuestionSpinner: AppCompatSpinner
    private lateinit var usersArrayAdapter: ArrayAdapter<String>
    private lateinit var questionsArrayAdapter: ArrayAdapter<String>
    private lateinit var userAdminConstraintLayout:ConstraintLayout
    private lateinit var userDataLayout:ConstraintLayout
    private lateinit var eraseUserButton: ImageButton
    private lateinit var cancelButton: Button
    private lateinit var eraseAllUsersButton: Button
    private lateinit var updateUserButton: Button
    private var selectedUser:User? = null
    private lateinit var allUsers:List<User>
    private lateinit var cifEditText:EditText
    private lateinit var nameEditText:EditText
    private lateinit var surnameEditText:EditText
    private lateinit var addressEditText:EditText
    private lateinit var tfnEditText:EditText
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText:EditText
    private lateinit var repeatPasswordEditText:EditText
    private lateinit var securityAnswerEditText:EditText
    private lateinit var allowedToOrderCheckBox:CheckBox
    private var questionList: MutableList<String> = mutableListOf()
    private var id:String = ""
    private var user_id:String = ""
    private var cif:String = ""
    private var name:String = ""
    private var surname:String = ""
    private var address:String = ""
    private var telephone:String = ""
    private var email:String = ""
    private var password:String = ""
    private var repeatPassword = ""
    private var recoverQuestion:String = ""
    private var recoverAnswer:String = ""
    private var approvedByAdmin:Boolean = false
    private lateinit var conManager: ConnectivityManager
    private var isNetActive:Boolean = true
    private var netInfo: NetworkInfo? = null
    private lateinit var thisFragment:AdminUsersFragment
    private lateinit var sharedPreferencesController:SharedPreferencesController
    private lateinit var progressBarLayout:LinearLayout
    private lateinit var userAdminLayout: ConstraintLayout
    private lateinit var reloadUsers:ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        newAdminUsersLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.admin_users_layout,container,false)
        sharedPreferencesController = SharedPreferencesController.getPreferencesInstance(requireContext())!!
        queryService = QueryService.getInstance(requireContext())
        conManager =requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        allUsers = viewModel.getAllUsers()
        userAdminConstraintLayout = newAdminUsersLayoutBinding.adminUsersBaseLayout

        userAdminConstraintLayout.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (userAdminConstraintLayout.hasFocus())
                    hideKeyboard(newAdminUsersLayoutBinding.root)
            }
        })
        initViews()
        loadUsers(0)
        return newAdminUsersLayoutBinding.root
    }

    internal fun initViews() {
        thisFragment = this
        userDataLayout = newAdminUsersLayoutBinding.viewCustomerDataLayout
        userSpinner = newAdminUsersLayoutBinding.usersSpinner
        progressBarLayout = newAdminUsersLayoutBinding.progressBarUsersLayout
        userAdminLayout = newAdminUsersLayoutBinding.adminUsersBaseLayout2
        recoverQuestionSpinner = newAdminUsersLayoutBinding.recoverQuestionChange
        usersArrayAdapter = ArrayAdapter(requireContext(),R.layout.order_name_spinner_text_view)
        usersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionsArrayAdapter = ArrayAdapter(requireContext(),R.layout.order_name_spinner_text_view)
        questionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recoverQuestionSpinner.adapter = questionsArrayAdapter
        queryService?.getAllUsersForSpinner(usersArrayAdapter,null,1)
        userSpinner.adapter = usersArrayAdapter
        eraseUserButton = newAdminUsersLayoutBinding.removeUserButton
        cancelButton = newAdminUsersLayoutBinding.cancelChangesButton
        eraseAllUsersButton = newAdminUsersLayoutBinding.eraseAllUsers
        questionList.clear()
        reloadUsers = newAdminUsersLayoutBinding.adminUsersReload
        requireContext().resources.getStringArray(R.array.spinner_questions).forEach {
            if (!it.equals("Selecciona una pregunta")){
                questionList.add(it)
                questionsArrayAdapter.add(it)
            }
        }
        questionsArrayAdapter.notifyDataSetChanged()

        cifEditText = newAdminUsersLayoutBinding.cifChange
        nameEditText = newAdminUsersLayoutBinding.nameChange
        surnameEditText = newAdminUsersLayoutBinding.surnameChange
        addressEditText = newAdminUsersLayoutBinding.addressChange
        tfnEditText = newAdminUsersLayoutBinding.telephoneChange
        emailEditText = newAdminUsersLayoutBinding.emailChange
        passwordEditText = newAdminUsersLayoutBinding.passwordChange
        repeatPasswordEditText = newAdminUsersLayoutBinding.passwordRepeatChange
        securityAnswerEditText = newAdminUsersLayoutBinding.recoverAnswerChange
        allowedToOrderCheckBox = newAdminUsersLayoutBinding.approvedChange
        updateUserButton = newAdminUsersLayoutBinding.updateUserButton
        enableViewListeners()
    }

    internal fun loadUsers(option:Int) //option 0 means it loads upon startup. 1 means loads via reload button (to implement yet)
    {
        netInfo = conManager.activeNetworkInfo
        isNetActive = checkNetActive()
        if(isNetActive)
        {
            disableViewListeners()
            viewModel.loadUserList(null)
            if (option==1 || option == 0)
            {
                sharedPreferencesController.setLoadingUsersScreenValue(true)
                queryService?.getAllUsersForSpinner(usersArrayAdapter,thisFragment,1)

            }
        }
        else
            Toast.makeText(requireContext(),R.string.no_internet_disponible,Toast.LENGTH_SHORT).show()

    }

    internal fun disableViewListeners() {
        val isLoading = sharedPreferencesController.getLoadingUsersScreenValue()
        if (isLoading) {
            userAdminLayout.alpha = 0.5F
            userDataLayout.visibility = View.GONE
            progressBarLayout.visibility = View.VISIBLE
            cancelButton.setOnClickListener(null)
            eraseAllUsersButton.setOnClickListener(null)
            updateUserButton.setOnClickListener(null)
        }
    }

    private fun checkNetActive() : Boolean {
        if (netInfo!=null)
            return true
        return false
    }
    fun enableViewListeners() {
        userAdminLayout.alpha =1.0F
        if (userAdminLayout.alpha == 1.0F)
            progressBarLayout.visibility = View.GONE

        if (!userDataLayout.isVisible)
            userDataLayout.visibility = View.VISIBLE

        userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (userSpinner.selectedItemPosition==0)
                    userDataLayout.apply { visibility = View.INVISIBLE }
                else {
                    userDataLayout.apply { visibility = View.VISIBLE }
                    val chosenUser = (userSpinner.selectedView as TextView).text
                    val cif = chosenUser.split("--")[0]

                    GlobalScope.launch {
                        allUsers = viewModel.getAllUsers()
                        allUsers.forEach{
                            if (it.cif.equals(cif)) {
                                selectedUser = it
                                newAdminUsersLayoutBinding.client = selectedUser
                                Handler(Looper.getMainLooper()).postDelayed( {
                                    recoverQuestionSpinner.setSelection(questionList.indexOf(it.recoverQuestion))
                                },200L)
                                recoverQuestion = it.recoverQuestion
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        eraseUserButton.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setPositiveButton(R.string.si) { dialog, _ ->
                    selectedUser?._id?.let {
                        queryService?.deleteUserById(it)
                    }
                    usersArrayAdapter.remove("${selectedUser?.cif}--${selectedUser?.name} ${selectedUser?.surname}")
                    usersArrayAdapter.setNotifyOnChange(true)
                    usersArrayAdapter.notifyDataSetChanged()
                    userSpinner.setSelection(0)
                    dialog!!.cancel()
                }
                setNegativeButton(R.string.no) { dialog, _ -> dialog!!.cancel() }
            }.setTitle(R.string.erase_this_user).setMessage(R.string.seguro_eliminar_usuario)
                .create().show()
        }

        reloadUsers.setOnClickListener { loadUsers(1) }
        cancelButton.setOnClickListener { requireActivity().onBackPressed() }
        eraseAllUsersButton.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setPositiveButton(R.string.si) { dialog, _ ->
                    if (allUsers.isNullOrEmpty()) {
                        GlobalScope.launch {
                            allUsers = viewModel.getAllUsers()
                            queryService?.deleteAllUsers(allUsers)
                        }
                    } else {
                        queryService?.deleteAllUsers(allUsers)
                    }
                    dialog!!.cancel()
                }
                setNegativeButton(R.string.no, { dialog, _ -> dialog!!.cancel() })
            }.setTitle(R.string.erase_this_user).setMessage(R.string.seguro_eliminar_usuario)
                .create().show()
        }

        allowedToOrderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            allowedToOrderCheckBox.isChecked = isChecked
        }


        updateUserButton.setOnClickListener {
            val tempUser: User? = newAdminUsersLayoutBinding.client
            id = tempUser!!._id!!
            user_id = tempUser.user_id
            cif = cifEditText.text.toString()
            name = nameEditText.text.toString()
            surname = surnameEditText.text.toString()
            address = addressEditText.text.toString()
            telephone = tfnEditText.text.toString()
            email = emailEditText.text.toString()
            password = passwordEditText.text.toString()
            repeatPassword = repeatPasswordEditText.text.toString()
            recoverQuestion = recoverQuestionSpinner.selectedItem as String
            recoverAnswer = securityAnswerEditText.text.toString()
            approvedByAdmin = allowedToOrderCheckBox.isChecked
            val userToUpdate = User(
                id,
                user_id,
                cif,
                name,
                surname,
                address,
                telephone,
                email,
                password,
                recoverQuestion,
                recoverAnswer,
                approvedByAdmin
            )

            if (checkUserIsValid(userToUpdate, repeatPassword)) {
                queryService?.updateUserData(
                    userToUpdate._id!!,
                    userToUpdate,
                    progressBarLayout,
                    null,
                    requireActivity()
                )
            } else
                Toast.makeText(
                    requireContext(),
                    R.string.no_se_puede_actualizar_usuario,
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    internal fun checkUserIsValid(_user:User,_repeatPassword:String) : Boolean {

        var validationPoints = 0
        val cif = _user.cif
        val name = _user.name
        val surname = _user.surname
        val address = _user.address
        val phone = _user.telephone
        val email = _user.email
        val password = _user.password
        val confirmPassword = _repeatPassword
        val recoverAnswer = _user.recoverAnswer

        if (!cifIsCorrect(cif))
            validationPoints++

        if (name.isBlank() || name.isEmpty())
            validationPoints++

        if (surname.isBlank() || surname.isEmpty())
            validationPoints++

        if (address.isBlank() || address.isEmpty())
            validationPoints++

        if (phone.isBlank() || phone.isEmpty())
            validationPoints++

        if (!isEmailAddressValid(email))
            validationPoints++

        if (password.isBlank() || password.isEmpty())
            validationPoints++

        if (confirmPassword.isBlank() || confirmPassword.isEmpty())
            validationPoints++

        if (!password.equals(confirmPassword))
            validationPoints++

        if (recoverAnswer.isBlank() || recoverAnswer.isEmpty())
            validationPoints++

        if (validationPoints > 0)
            return false

        return true
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
        val pattern: Pattern = Pattern.compile(CIF_REGEX)
        return pattern.matcher(cif).matches()
    }

    private fun hideKeyboard(view:View) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    ///////TEST/////////////////
    @Subscribe
    fun onEvent(idlingEntity: IdlingEntity) {

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
