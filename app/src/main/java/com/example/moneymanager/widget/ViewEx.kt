package com.example.moneymanager.widget


import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 1500)
        action(it)
    }
}


fun View.tapin(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 10)
        action(it)
    }
}

fun View.tapRotate(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 500)
        action(it)
    }
}


fun View.visible() {
    visibility = View.VISIBLE // hiện view
}

fun View.gone() {
    visibility = View.GONE // ẩn view
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.onAvoidDoubleClick(
    throttleDelay: Long = 600,
    onClick: (View) -> Unit
) {
    tap {
        onClick(this)
        isClickable = false
        postDelayed({ isClickable = true }, throttleDelay)
    }
}

private var lastClick = 0L
fun <T : View> T.onClick(delayBetweenClick: Long = 0, block: T.() -> Unit) {
    tap {
        when {
            delayBetweenClick <= 0 -> {
                block()
            }

            System.currentTimeMillis() - lastClick > delayBetweenClick -> {
                lastClick = System.currentTimeMillis()
                block()
            }

            else -> {

            }
        }
    }
}

fun EditText.getCleanFloat(): Float {
    return this.text.toString().replace("[^\\d.]".toRegex(), "").toFloatOrNull() ?: 0f
}

fun Activity.hideKeyboard() {
    val view = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}





