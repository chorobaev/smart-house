package io.aikosoft.smarthouse.data.models

interface Module {
    val moduleType: ModuleType
    val moduleName: String
    val moduleId: String
}