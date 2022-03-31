package com.juan.pescaderia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.juan.pescaderia.R

class LoginOrRegister : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var navController:NavController


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_or_register_layout,container, false)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.popBackStack())
                    requireActivity().finish()
            }
        })
        loginButton = view.findViewById(R.id.login_button)
        registerButton = view.findViewById(R.id.register_button)

        loginButton.setOnClickListener { doLogin() }

        registerButton.setOnClickListener { doRegister() }
        return view
    }

    internal fun doLogin() {
        navController.navigateUp()
        navController.navigate(R.id.login_fragment)
    }
    internal fun doRegister() {
        navController.navigateUp()
        navController.navigate(R.id.new_user_fragment)
    }
}