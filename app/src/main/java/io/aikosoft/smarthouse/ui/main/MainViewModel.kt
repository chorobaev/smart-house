package io.aikosoft.smarthouse.ui.main

import io.aikosoft.smarthouse.base.BaseViewModel
import io.aikosoft.smarthouse.data.repositories.AuthRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun logout() = authRepository.logout()
}