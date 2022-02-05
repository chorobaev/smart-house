package io.aikosoft.smarthouse.utility

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.util.*


fun Context.makeShortToast(res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
}

fun Context.makeShortToast(msg: String) {
    if (msg.isNotBlank()) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.openKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }

    imm.toggleSoftInputFromWindow(
        view.applicationWindowToken,
        InputMethodManager.SHOW_FORCED, 0
    )
}

fun Boolean?.toVisibility(): Int = if (this == true) VISIBLE else GONE

fun Date?.toStringDate(resources: Resources): String {
    return toString().substringBefore(" GMT")
}

fun Long.toStringHHMM(): String {
    var minutes = div(1000)
    val hours = minutes / 60 % 60
    minutes %= 60
    val hh = if (hours.toString().length < 2) "0$hours" else hours.toString()
    val mm = if (minutes.toString().length < 2) "0$minutes" else minutes.toString()
    return "$hh:$mm"
}

fun Resources.densityToPixels(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).toInt()
