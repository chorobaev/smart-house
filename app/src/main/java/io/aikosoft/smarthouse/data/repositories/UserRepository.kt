package io.aikosoft.smarthouse.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import io.aikosoft.smarthouse.utility.HIGH
import io.aikosoft.smarthouse.utility.LOW
import io.aikosoft.smarthouse.utility.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : Logger {

    fun getAllModulesSmartHouseLiz(uid: String): LiveData<List<ModuleSmartHouseLiz>> {
        val data = MutableLiveData<List<ModuleSmartHouseLiz>>()
        getAllModuleIds(uid).observeForever { ids ->
            log("Module ids: $ids")
            getAllModules(ids).observeForever { modules ->
                log("Modules: $modules")
                data.value = modules
            }
        }
        return data
    }

    fun getModuleSmartHouseLizById(moduleId: String): LiveData<ModuleSmartHouseLiz> {
        val data = MutableLiveData<ModuleSmartHouseLiz>()
        firebaseDatabase.getReference("modules/$moduleId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    data.value = dataSnapshot.getValue(ModuleSmartHouseLiz::class.java)
                }
            })
        return data
    }

    fun getHimiditySmartHouseLizById(moduleId: String): LiveData<String> {
        val data = MutableLiveData<String>()
        firebaseDatabase.getReference("modules/$moduleId/humidity")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(String::class.java)?.let {
                        data.value = it
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        return data
    }

    fun getTemperatureSmartHouseLizById(moduleId: String): LiveData<String> {
        val data = MutableLiveData<String>()
        firebaseDatabase.getReference("modules/$moduleId/temperature")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(String::class.java)?.let {
                        data.value = it
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        return data
    }

    fun turnRelyOn(moduleId: String, relyId: Int): LiveData<Boolean> =
        turnRely(moduleId, relyId, true)

    fun turnRelyOff(moduleId: String, relyId: Int): LiveData<Boolean> =
        turnRely(moduleId, relyId, false)

    fun turnRely(moduleId: String, relyId: Int, turnOn: Boolean): LiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        firebaseDatabase.getReference("modules/$moduleId/R$relyId")
            .setValue(if (turnOn) HIGH else LOW)
            .addOnCompleteListener {
                data.value = it.isSuccessful
            }
        return data
    }

    fun getAllModuleIds(uid: String): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()
        firebaseDatabase.getReference("users/$uid/modules")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val moduleIds = ArrayList<String>()
                    for (dsh in p0.children) {
                        dsh.getValue(String::class.java)?.let {
                            moduleIds.add(it)
                        }
                    }
                    data.value = moduleIds
                }
            })
        return data
    }

    fun getAllModules(moduleIds: List<String>): LiveData<List<ModuleSmartHouseLiz>> {
        val data = MutableLiveData<List<ModuleSmartHouseLiz>>()

        firebaseDatabase.getReference("modules/")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val modules = ArrayList<ModuleSmartHouseLiz>()
                    for (dsh in dataSnapshot.children) {
                        if (moduleIds.contains(dsh.key)) {
                            dsh.getValue(ModuleSmartHouseLiz::class.java)?.let {
                                modules.add(it)
                            }
                        }
                    }
                    data.value = modules
                }
            })
        return data
    }
}