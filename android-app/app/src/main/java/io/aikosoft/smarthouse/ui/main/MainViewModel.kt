package io.aikosoft.smarthouse.ui.main

import androidx.lifecycle.LiveData
import io.aikosoft.smarthouse.base.BaseViewModel
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import io.aikosoft.smarthouse.data.repositories.AuthRepository
import io.aikosoft.smarthouse.data.repositories.UserRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn
    val modules: LiveData<List<ModuleSmartHouseLiz>>
        get() = userRepository.getAllModulesSmartHouseLiz(authRepository.uid)

    fun logout() = authRepository.logout()
}