package io.aikosoft.smarthouse.data.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

data class ModuleSmartHouseLiz(
    val name: String = "",
    val id: String = "",
    val type: ModuleType = ModuleType.SMART_HOUSE_LIZ,
    val temperature: String = "0",
    val humidity: String = "0",
    @get:PropertyName("R1")
    val R1: String = "0",
    @get:PropertyName("R2")
    val R2: String = "0"
) : Module {

    @get:Exclude
    override val moduleType: ModuleType get() = type
    @get:Exclude
    override val moduleName: String get() = name
    @get:Exclude
    override val moduleId: String get() = id
}