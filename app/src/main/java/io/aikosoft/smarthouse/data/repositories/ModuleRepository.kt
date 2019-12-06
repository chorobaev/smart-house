package io.aikosoft.smarthouse.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.aikosoft.smarthouse.data.models.Module
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import io.aikosoft.smarthouse.data.models.ModuleType
import io.aikosoft.smarthouse.data.rest.ModuleClient
import io.aikosoft.smarthouse.utility.Logger
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleRepository @Inject constructor(
    private val moduleClient: ModuleClient,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
): Logger {
    fun setSsidAndPassword(ssid: String, password: String): Single<String> =
        moduleClient.sendWiFi(ssid, password)

    fun mountModule(module: Module): Single<Unit> =
        when (module.moduleType) {
            ModuleType.SMART_HOUSE_LIZ -> putModuleSmartHouseLiz(module as ModuleSmartHouseLiz)
        }


    private fun putModuleSmartHouseLiz(module: ModuleSmartHouseLiz): Single<Unit> =
        Single.create { single ->
            log("Setting ${module.name} to firebase.")
            val uid = firebaseAuth.uid ?: single.onError(Throwable("Not signed in!"))
            firebaseDatabase.getReference("users/$uid/modules").push()
                .setValue(module.name)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        single.onSuccess(Unit)
                    } else {
                        single.onError(Throwable(it.exception))
                    }
                }
        }

    private fun <M> Task<M>.asSeingle(module: M): Single<M> =
        Single.create { single ->
            addOnSuccessListener {
                single.onSuccess(module)
            }
            addOnFailureListener {
                single.onError(Throwable(it))
            }
        }

}