package io.aikosoft.smarthouse.ui.detail

import androidx.lifecycle.LiveData
import io.aikosoft.smarthouse.base.BaseViewModel
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import io.aikosoft.smarthouse.data.repositories.UserRepository
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    fun getModule(moduleId: String): LiveData<ModuleSmartHouseLiz> =
        userRepository.getModuleSmartHouseLizById(moduleId)

    fun getTemperature(moduleId: String): LiveData<String> =
        userRepository.getTemperatureSmartHouseLizById(moduleId)

    fun getHumidity(moduleId: String): LiveData<String> =
        userRepository.getHimiditySmartHouseLizById(moduleId)

    fun turnRelyOn(moduleId: String, relyId: Int): LiveData<Boolean> =
        userRepository.turnRelyOn(moduleId, relyId)


    fun turnRelyOff(moduleId: String, relyId: Int): LiveData<Boolean> =
        userRepository.turnRelyOff(moduleId, relyId)

    fun turnRely(moduleId: String, relyId: Int, turnOn: Boolean): LiveData<Boolean> =
        userRepository.turnRely(moduleId = moduleId, relyId = relyId, turnOn = turnOn)
}