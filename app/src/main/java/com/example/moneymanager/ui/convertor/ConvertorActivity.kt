package com.example.moneymanager.ui.convertor

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityConventorBinding
import com.example.moneymanager.entity.RetrofitInstance
import com.example.moneymanager.model.ConventorModel
import com.example.moneymanager.widget.gone
import com.example.moneymanager.widget.tap
import com.example.moneymanager.widget.visible

import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ConvertorActivity : BaseActivity<ActivityConventorBinding>() {

    private var switchSoundCheck = ""
    private var switchVibrationCheck = ""
    private var precision = 0
    private var decimalSeparator = 0
    private var thousandsSeparator = 0
    private lateinit var conventorAdapter: ConvertorAdapter
    private var checkconventorModel: String? = ""
    private var rateFrom: Double? = 0.0
    private var rateTo: Double? = 0.0
    private var dataTime: String? = ""

    private val sharedPreferences by lazy {
        getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    override fun setViewBinding() = ActivityConventorBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding.edtAmount.showSoftInputOnFocus = true
        binding.edtAmount.requestFocus()

        sharedPreferences.edit().putString("fromconventorModel", "AUD").apply()
        sharedPreferences.edit().putString("toconventorModel", "USD").apply()
        sharedPreferences.edit().putInt("fromconventorModelId", 1).apply()
        sharedPreferences.edit().putInt("toconventorModelId", 0).apply()

        binding.tvTopconventor.text = "AUD"
        binding.tvCenterconventor.text = "USD"

        setNumberButtonClickListeners()
        initconventorModelView()
    }

    private fun initconventorModelView() {
        val initialList = getInitialconventorModelList()
        fetchconventorModelData(initialList)
        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                equal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        conventorAdapter = ConvertorAdapter(initialList, object : OnConvertorClickListener {
            override fun onItemClick(conventorModel: ConventorModel) {
                binding.editTextSearch.clearFocus()
                binding.editTextSearch.text = null
                if (checkconventorModel == "fromconventorModel") {
                    sharedPreferences.edit().putString("fromconventorModel", conventorModel.contentDisplay).apply()
                    sharedPreferences.edit().putInt("fromconventorModelId", conventorModel.id).apply()
                    rateFrom = conventorModel.rate
                    binding.tvTopconventor.text = conventorModel.contentDisplay
                } else {
                    sharedPreferences.edit().putString("toconventorModel", conventorModel.contentDisplay).apply()
                    sharedPreferences.edit().putInt("toconventorModelId", conventorModel.id).apply()
                    rateTo = conventorModel.rate
                    binding.tvCenterconventor.text = conventorModel.contentDisplay
                }
            }
        })

        binding.rvOptionCurrency.adapter = conventorAdapter


        binding.clBottomSheet.tap {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.clBottomSheet.gone()
        }


        binding.tvTopconventor.tap {
            checkconventorModel = "fromconventorModel"
            val fromId = sharedPreferences.getInt("fromconventorModelId", 1)
            conventorAdapter.updateSelection(fromId)
            binding.clBottomSheet.visible()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        binding.tvCenterconventor.tap {
            checkconventorModel = "toconventorModel"
            val toId = sharedPreferences.getInt("toconventorModelId", 1)
            conventorAdapter.updateSelection(toId)
            binding.clBottomSheet.visible()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                conventorAdapter.filterList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.editTextSearch.setHint(if (hasFocus) "" else getString(R.string.search))
        }

        binding.ivBack.tap { finish() }
    }

    private fun getInitialconventorModelList(): MutableList<ConventorModel> {
        return mutableListOf(
            ConventorModel(0, "USD", "United States Dollar USD", 1.0, false),
            ConventorModel(1, "AUD", "Australian Dollar AUD", 1.0, false),
            ConventorModel(2, "BGN", "Bulgarian Lev BGN", 1.0, false),
            ConventorModel(3, "BRL", "Brazilian Real BRL", 1.0, false),
            ConventorModel(4, "CAD", "Canadian Dollar CAD", 1.0, false),
            ConventorModel(5, "CHF", "Swiss Franc CHF", 1.0, false),
            ConventorModel(6, "CNY", "Chinese Yuan CNY", 1.0, false),
            ConventorModel(7, "CZK", "Czech Koruna CZK", 1.0, false),
            ConventorModel(8, "DKK", "Danish Krone DKK", 1.0, false),
            ConventorModel(9, "EUR", "Euro EUR", 1.0, false),
            ConventorModel(10, "GBP", "British Pound GBP", 1.0, false),
            ConventorModel(11, "HKD", "Hong Kong Dollar HKD", 1.0, false),
            ConventorModel(12, "HUF", "Hungarian Forint HUF", 1.0, false),
            ConventorModel(13, "IDR", "Indonesian Rupiah IDR", 1.0, false),
            ConventorModel(14, "ILS", "Israeli New Shekel ILS", 1.0, false),
            ConventorModel(15, "INR", "Indian Rupee INR", 1.0, false),
            ConventorModel(16, "ISK", "Icelandic Krona ISK", 1.0, false),
            ConventorModel(17, "JPY", "Japanese Yen JPY", 1.0, false),
            ConventorModel(18, "KRW", "South Korean Won KRW", 1.0, false),
            ConventorModel(19, "MXN", "Mexican Peso MXN", 1.0, false),
            ConventorModel(20, "MYR", "Malaysian Ringgit MYR", 1.0, false),
            ConventorModel(21, "NOK", "Norwegian Krone NOK", 1.0, false),
            ConventorModel(22, "NZD", "New Zealand Dollar NZD", 1.0, false),
            ConventorModel(23, "PHP", "Philippine Peso PHP", 1.0, false),
            ConventorModel(24, "PLN", "Polish Zloty PLN", 1.0, false),
            ConventorModel(25, "RON", "Romanian Leu RON", 1.0, false),
            ConventorModel(26, "SEK", "Swedish Krona SEK", 1.0, false),
            ConventorModel(27, "SGD", "Singapore Dollar SGD", 1.0, false),
            ConventorModel(28, "THB", "Thai Baht THB", 1.0, false),
            ConventorModel(29, "TRY", "Turkish Lira TRY", 1.0, false),
            ConventorModel(30, "ZAR", "South African Rand ZAR", 1.0, false)
        )
    }

    private fun equal() {
        val amount = binding.edtAmount.text.toString().toFloatOrNull()
        if (amount == null) {
            Toast.makeText(this, getString(R.string.please_enter_a_valid_number), Toast.LENGTH_SHORT).show()
        } else {
            val result = (amount / rateFrom!! * rateTo!!)
            binding.tvResult.text = getFormatNumber(result, precision, decimalSeparator, thousandsSeparator)
        }
    }

    private fun fetchconventorModelData(initialList: MutableList<ConventorModel>) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = RetrofitInstance.api.getCurrencyRates()
            withContext(Dispatchers.Main) {
                response?.let {
                    val updatedList = initialList.map { conventorModel ->
                        val rate = response.rates[conventorModel.contentDisplay] ?: 1.0
                        conventorModel.copy(rate = rate)
                    }
                    val timestamp = response.timestamp
                    val date = Date(timestamp * 1000L)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dataTime = getString(R.string.update) + " ${dateFormat.format(date)}"
                    conventorAdapter.updateList(updatedList.toMutableList())
                    binding.tvDate.text = dataTime
                    rateTo = 1.0
                    rateFrom = response.rates["AUD"] ?: 1.0
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun click() {
        if (switchVibrationCheck == "Selected") vibratePhone(this)
        if (switchSoundCheck == "Selected") SoundClick(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNumberButtonClickListeners() {


    }

    override fun onStart() {
        super.onStart()
        precision = sharedPreferences.getInt("clLimitPrecision", 1)
        decimalSeparator = sharedPreferences.getInt("clDecimalSeparator", 0)
        thousandsSeparator = sharedPreferences.getInt("clThousandsSeparator", 0)
        switchSoundCheck = sharedPreferences.getString("switchSound", "") ?: "noSelected"
        switchVibrationCheck = sharedPreferences.getString("switchVibration", "") ?: "noSelected"
    }

    override fun viewListener() {}
    override fun dataObservable() {}
}
