package com.example.moneymanager.base

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.example.moneymanager.utils.SystemUtil
import com.example.moneymanager.view.dialog.PermissionDialog


abstract class BaseActivity2<T : ViewBinding> : AppCompatActivity() {
    private var _binding: T? = null
    lateinit var binding: T
    private var TAG = ""
    private var currentApiVersion = 0
    private var isServiceBound = false


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { callback ->
            if (callback.containsValue(false)) {
                onPermissionDenied()
            } else {
                onPermissionGranted()
            }
        }

    private val requestPermissionActivity =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { callback ->
            if (callback.resultCode == RESULT_OK)
                onPermissionGranted()
            else
                onPermissionDenied()
        }

    protected abstract fun setViewBinding(): T
    protected abstract fun initView()
    protected abstract fun viewListener()
    protected abstract fun dataObservable()
    open fun onPermissionGranted() {}
    open fun onPermissionDenied() {}
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        try {
        SystemUtil.setLocale(this)
        _binding = setViewBinding()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        TAG = "$localClassName check"
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        _binding?.let {
            binding = it
            setContentView(binding.root)
            initView()
            dataObservable()
            viewListener()
//                binding.root.viewTreeObserver.addOnGlobalLayoutListener { fullScreen() }
        }

//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            fullScreenImmersive(window)
//        }

    }



    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun fullScreen() {
        val windowInsetsController: WindowInsetsControllerCompat? =
            if (Build.VERSION.SDK_INT >= 30) {
                ViewCompat.getWindowInsetsController(window.decorView)
            } else {
                WindowInsetsControllerCompat(window, binding.root)
            }

        if (windowInsetsController == null) {
            return
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.systemGestures())
    }

    fun showActivity(activity: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }

    fun showActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    open fun showDialogPermission(
        permissions: Array<String>,
    ) {
        for (per in permissions) {
            if (!checkPermission(per)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, per)) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package",
                        applicationContext.packageName,
                        null
                    )
                    intent.data = uri
                    val dialogPermission = PermissionDialog(this) {
                        requestPermissionActivity.launch(intent)
                    }
                    dialogPermission.show()
                } else {
                    requestPermissionLauncher.launch(permissions)
                }
                return
            }
        }
        onPermissionGranted()
    }

    override fun onPause() {
        super.onPause()
    }

    protected open fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected open fun checkPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            val allow = ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            if (!allow) return false
        }
        return true
    }

}