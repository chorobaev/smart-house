package io.aikosoft.smarthouse.ui.main

import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseActivity
import io.aikosoft.smarthouse.ui.mount.MountActivity
import io.aikosoft.smarthouse.utility.makeShortToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    override val layoutRes: Int get() = R.layout.activity_main

    override fun initViewModel() {
        super.initViewModel()
        viewModel = getViewModel()
    }

    override fun initUI(firstInit: Boolean) {
        super.initUI(firstInit)

        if (!viewModel.isLoggedIn) {
            startAuthUI()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                makeShortToast("Signed in")
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
