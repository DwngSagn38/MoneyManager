package  com.example.moneymanager.ui.permission

import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityPermissionBinding
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.sharePreferent.SharePrefUtils
import com.example.moneymanager.ui.currency.BaseCurrencyActivity
import com.example.moneymanager.ui.main.MainActivity
import com.example.moneymanager.utils.helper.Default.ACCESS_COARSE_LOCATION
import com.example.moneymanager.utils.helper.Default.ACCESS_FINE_LOCATION
import com.example.moneymanager.utils.helper.Default.CAMERA_PERMISSION
import com.example.moneymanager.widget.gone
import com.example.moneymanager.widget.tap
import com.example.moneymanager.widget.visible


class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    private lateinit var arrayPermission: Array<String>

    override fun setViewBinding(): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun initView() {
        arrayPermission = arrayOf(CAMERA_PERMISSION,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION)
        if (checkPermission(arrayPermission)) {
            allowCameraPermission()
        }

    }

    override fun viewListener() {
        binding.apply {
            ivSetCameraPermission.tap {
                showDialogPermission(arrayOf(CAMERA_PERMISSION,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION))
            }
            tvContinue.tap {
                SharePrefUtils.forceGoToMain(this@PermissionActivity)
                val currency = PreferenceManager(this@PermissionActivity).getCheckCurrency()
                if (currency){
                    showActivity(MainActivity::class.java)
                }else{
                    showActivity(BaseCurrencyActivity::class.java)
                }
                finishAffinity()
            }
        }

    }

    override fun dataObservable() {
    }

    override fun onPermissionGranted() {
        if (checkPermission(arrayPermission)) {
            allowCameraPermission()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onResume() {
        if (checkPermission(arrayPermission)) {
            allowCameraPermission()
        }
        super.onResume()
    }

    private fun allowCameraPermission() {
        binding.ivSetCameraPermission.gone()
        binding.ivSelectCameraPermission.visible()
    }

}