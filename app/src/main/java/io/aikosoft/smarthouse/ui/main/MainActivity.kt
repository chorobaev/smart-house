package io.aikosoft.smarthouse.ui.main

import android.app.Activity
import android.content.Intent
import android.view.View.GONE
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseActivity
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import io.aikosoft.smarthouse.ui.detail.DetailActivity
import io.aikosoft.smarthouse.ui.main.adapter.ModulRVAdapter
import io.aikosoft.smarthouse.ui.mount.MountActivity
import io.aikosoft.smarthouse.utility.makeShortToast
import io.aikosoft.smarthouse.utility.toVisibility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var moduleRVAdapter: ModulRVAdapter

    override val layoutRes: Int get() = R.layout.activity_main

    override fun initViewModel() {
        super.initViewModel()
        viewModel = getViewModel()
    }

    override fun initUI(firstInit: Boolean) {
        super.initUI(firstInit)

        if (viewModel.isLoggedIn) {
            fetchModules()
        } else {
            startAuthUI()
        }
        initRecyclerView()
    }

    private fun startAuthUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun observeViewModel() {
        super.observeViewModel()
        val lifeOwner = this as LifecycleOwner
        with(viewModel) {
            loading.observe(lifeOwner, Observer {
                progress_bar.visibility = it.toVisibility()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isLoggedIn) {
            fetchModules()
        }
    }

    private fun fetchModules() {
        log("Fetching modules")
        viewModel.modules.observe(this, Observer {
            it?.let { onModulesFetched(it) }
        })
    }

    private fun onModulesFetched(modules: List<ModuleSmartHouseLiz>) {
        progress_bar.visibility = GONE
        moduleRVAdapter.updateModules(modules)
    }

    private fun initRecyclerView() {
        moduleRVAdapter = ModulRVAdapter()
        moduleRVAdapter.setOnModuleClickListener {
            startDetailActivity(it.moduleId)
        }

        recycler_view.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = moduleRVAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                makeShortToast("Signed in")
                fetchModules()
            } else {
                makeShortToast("${response?.error}")
                finish()
            }
        }
    }

    override fun initOnClicks() {
        super.initOnClicks()

        fab_mount.setOnClickListener {
            startMountActivity()
        }
    }

    private fun startDetailActivity(moduleId: String) {
        Intent(this, DetailActivity::class.java).also {
            it.putExtra(DetailActivity.EXTRA_MODEL_ID, moduleId)
            startActivity(it)
        }
    }

    private fun startMountActivity() {
        Intent(this, MountActivity::class.java).also {
            startActivity(it)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 13
        private const val LED1 = "LED1"
        private const val HIGH = "0"
        private const val LOW = "1"
    }
}
