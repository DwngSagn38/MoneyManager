package com.example.moneymanager.base

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.example.moneymanager.R
import com.example.moneymanager.model.Category
import com.example.moneymanager.utils.SystemUtil
import com.example.moneymanager.view.dialog.PermissionDialog
import java.util.Locale


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    //_binding là biến nullable dùng để lưu trữ binding
    private var _binding: T? = null
    //binding là biến lateinit dùng đẻ khởi tạo binding sau này
    lateinit var binding: T
    //Tag dùng đẻ log
    private var TAG = ""
    //currentApiVersion lưu phiên bản hiện taại
    private var currentApiVersion = 0
    //isServiceBound dùng đẻ kểm tra xem service có được liên kết hay không
    private var isServiceBound= false
    private var decimalSeparatorDisplay = ""
    private var thousandsSeparatorDisplay = ""


    //kết nối với service
    private val connection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            isServiceBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isServiceBound = false
        }
    }


    // yêu cầu cấp quyền với ActivityResult API
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

    //các phương thức trưừu tượng được các lớp con sử dụng
    protected abstract fun setViewBinding(): T
    protected abstract fun initView()
    protected abstract fun viewListener()
    protected abstract fun dataObservable()
    open fun onPermissionGranted() {}
    open fun onPermissionDenied() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        currentApiVersion = Build.VERSION.SDK_INT
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = flags
            val decorView = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decorView.systemUiVisibility = flags
                }
            }
        }
        super.onCreate(savedInstanceState)
       try {
            // Thiết lập ngôn ngữ hệ thống
           SystemUtil.setLocale(this)
            // Khởi tạo binding
            _binding = setViewBinding()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            TAG = "$localClassName check"
            _binding?.let {
                binding = it
                setContentView(binding.root)
                initView()
                dataObservable()
                viewListener()
            }
    } catch (e: Exception) {         e.printStackTrace()

       }


        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CRASH", "Uncaught: ${throwable.message}", throwable)
        }

    }
    fun getFormatNumber(number: Double, precision: Int, decimalSeparator: Int, thousandsSeparator: Int): String {
        when (decimalSeparator) {
            0 ->
                decimalSeparatorDisplay = "."

            1 ->
                decimalSeparatorDisplay = ","
        }
        when (thousandsSeparator) {
            0 ->
                thousandsSeparatorDisplay = ""
            1 ->
                thousandsSeparatorDisplay = ", "

            2 ->
                thousandsSeparatorDisplay = " "

            3 ->
                thousandsSeparatorDisplay = ". "

            4 ->
                thousandsSeparatorDisplay = "'"
        }
        return formatNumber(number, precision, decimalSeparatorDisplay, thousandsSeparatorDisplay)
    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun vibratePhone(context: Context) {
//        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        if (vibrator.hasVibrator()) {
//            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
//        }
//    }

    fun formatNumber(
        number: Double,
        precision: Int,
        decimalSeparator: String,
        thousandsSeparator: String
    ): String {
        val validPrecision = precision.coerceIn(0, 6)

        val rawFormatted = if (validPrecision == 0) {
            "%.0f".format(Locale.US, number)
        } else {
            "%,.${validPrecision}f".format(Locale.US, number)
        }

        val converter = rawFormatted.replace(".", "?")
            .replace(",", thousandsSeparator)
            .replace("?", decimalSeparator)

        return converter
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus){
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound){
            unbindService(connection)
            isServiceBound = false
        }
    }

    //thiết lập lại màn hình
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

    fun showActivity(activity: Class<*>, bundle: Bundle?){
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }


    // Chuyển đến Activity khác không cần bundle
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

    protected open fun checkPermission(permission: String): Boolean{
        return ActivityCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    protected open fun checkPermission(permission: Array<String>): Boolean{
        for (permisson in permission){
            val allow = ActivityCompat.checkSelfPermission(
                this, permisson
            ) == PackageManager.PERMISSION_GRANTED
            if (!allow) return false
        }
        return true
    }

    protected fun takeScreenshot(): Bitmap {
        val rootView: View = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        rootView.draw(canvas)
        return bitmap
    }

    protected fun shareScreenshot(screenshot: Bitmap) {
        val path = MediaStore.Images.Media.insertImage(contentResolver, screenshot, "screenshot", null)
        path?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, Uri.parse(it))
            }
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }
    }
    object check {
        var checkChoosepinLocation: Int? = null
        var checkChooseLocation: Int? = null
        var checkPin:Int? = null
    }
    fun getCatagoryList(int: Int): List<Category> {
        return when (int) {
            1 -> ListExpenses
            2 -> incomeCategories
            3 -> loansCategory
            else -> ListExpenses + incomeCategories + loansCategory
        }
    }

    val ListExpenses = listOf(
        Category(1, "Social", "Expense", R.drawable.ic_social,  Color.parseColor("#CB41C8")),
        Category(2, "Pets", "Expense", R.drawable.ic_pets,  Color.parseColor("#EF5267")),
        Category(3, "Shopping", "Expense", R.drawable.ic_shopping,  Color.parseColor("#45BEE3")),
        Category(4, "Food & Dining", "Expense", R.drawable.ic_food_and_drink,  Color.parseColor("#FF9C23")),
        Category(5, "Transportation", "Expense", R.drawable.ic_transportation,  Color.parseColor("#5271EF")),
        Category(6, "Groceries", "Expense", R.drawable.ic_groceries,  Color.parseColor("#B38210")),
        Category(7, "Education", "Expense", R.drawable.ic_education,  Color.parseColor("#30CDCF")),
        Category(8, "Bills & Utilities", "Expense", R.drawable.ic_bills_and_utilites,  Color.parseColor("#45BEE3")),
        Category(9, "Housing", "Expense", R.drawable.ic_housing,  Color.parseColor("#CB41C8")),
        Category(10, "Healthcare", "Expense", R.drawable.ic_healthcare,  Color.parseColor("#E07B00")),
        Category(11, "Investments", "Expense", R.drawable.ic_investment_income,  Color.parseColor("#00BB80")),
        Category(12, "Beauty", "Expense", R.drawable.ic_beauty,  Color.parseColor("#F8344E")),
        Category(13, "Children", "Expense", R.drawable.ic_children,  Color.parseColor("#3557E2")),
        Category(14, "Gifts", "Expense", R.drawable.ic_gifts,  Color.parseColor("#FF0022")),
        Category(15, "Sports", "Expense", R.drawable.ic_sports,  Color.parseColor("#5BB23B")),
        Category(16, "Gym", "Expense", R.drawable.ic_gym,  Color.parseColor("#ED354D")),
        Category(17, "Tobacco", "Expense", R.drawable.ic_tobacco,  Color.parseColor("#52EF93")),
        Category(18, "Drinks", "Expense", R.drawable.ic_drinks,  Color.parseColor("#612712")),
        Category(19, "Travel", "Expense", R.drawable.ic_travel,  Color.parseColor("#04FFB0")),
        Category(20, "Donations", "Expense", R.drawable.ic_donations,  Color.parseColor("#F88800")),
        Category(21, "Lottery", "Expense", R.drawable.ic_lottery,  Color.parseColor("#EF5252")),
        Category(22, "Transit", "Expense", R.drawable.ic_transit,  Color.parseColor("#F8A745")),
        Category(23, "Electronics", "Expense", R.drawable.ic_electronics,  Color.parseColor("#06E4E7")),
        Category(24, "Books", "Expense", R.drawable.ic_books,  Color.parseColor("#DB5508")),
        Category(25, "Other", "Expense", R.drawable.ic_other,  Color.parseColor("#BB52EF"))
    )
    val incomeCategories = listOf(
        Category(26, "Salary", "Income", R.drawable.ic_salary,  Color.parseColor("#CB41C8")),
        Category(27, "Allowance", "Income", R.drawable.ic_allowance,  Color.parseColor("#EF5267")),
        Category(28, "Awards", "Income", R.drawable.ic_awards,  Color.parseColor("#45BEE3")),
        Category(29, "Investments", "Income", R.drawable.ic_investment_income,  Color.parseColor("#2AC491")),
        Category(30, "Business", "Income", R.drawable.ic_business,  Color.parseColor("#90AF16")),
        Category(31, "Interest Income", "Income", R.drawable.ic_interest_income,  Color.parseColor("#45BEE3")),
        Category(32, "Extra Income", "Income", R.drawable.ic_extra_income,  Color.parseColor("#30CDCF")),
        Category(33, "Other", "Income", R.drawable.ic_other,  Color.parseColor("#BB52EF"))
    )
    val loansCategory = listOf(
        Category(34,"Borrowed","Loans", R.drawable.ic_debt,  Color.parseColor("#CB41C8")),
        Category(35,"Loans","Loans", R.drawable.ic_loans3,  Color.parseColor("#EF5267"))
    )

}