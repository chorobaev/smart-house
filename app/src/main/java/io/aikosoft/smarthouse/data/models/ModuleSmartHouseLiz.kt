package io.aikosoft.smarthouse.data.models

data class ModuleSmartHouseLiz(
    val name: String = "",
    val id: String = "",
    val type: ModuleType = ModuleType.SMART_HOUSE_LIZ,
    val temperature: String = "0",
    val humidity: String = "0",
    val R1: String = "0",
    val R2: String = "0"
) : Module {
    override val moduleType: ModuleType get() = type
    override val moduleName: String get() = name
}