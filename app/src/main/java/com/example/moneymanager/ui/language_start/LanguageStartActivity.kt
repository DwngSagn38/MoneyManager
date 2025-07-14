package  com.example.moneymanager.ui.language_start

import android.content.Intent
import android.os.Bundle
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityLangugeStartBinding
import com.example.moneymanager.utils.SystemUtil
import com.example.moneymanager.model.LanguageModel
import com.example.moneymanager.ui.intro.IntroActivity
import com.example.moneymanager.widget.tap
import com.example.moneymanager.widget.visible
import java.util.Locale

class LanguageStartActivity : BaseActivity<ActivityLangugeStartBinding>() {
    private lateinit var adapter: LanguageStartAdapter
    private var listLanguage: ArrayList<LanguageModel> = ArrayList()
    private var codeLang = ""
    private var isDialogVisible = false

    override fun setViewBinding(): ActivityLangugeStartBinding {
        return ActivityLangugeStartBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun initView() {
        adapter = LanguageStartAdapter(this, onClick = {
            codeLang = it.code
            adapter.setCheck(it.code)
            binding.ivDone.visible()
            adapter.notifyDataSetChanged()
        })

        binding.recyclerView.adapter = adapter
    }

    override fun viewListener() {
        binding.ivDone.tap {
            SystemUtil.saveLocal(baseContext, codeLang)
            var bundle = Bundle()
            bundle.putString("language_name", codeLang)
            startActivity(Intent(this@LanguageStartActivity, IntroActivity::class.java))
            finish()
        }
    }

    override fun dataObservable() {
        setCodeLanguage()
        initData()
    }

    private fun setCodeLanguage() {
        val codelangDefault = Locale.getDefault().language
        val langDefault = arrayOf("fr", "pt", "es", "de", "in", "en", "hi", "vi", "ja")

        codeLang = if (SystemUtil.getPreLanguage(this).equals("")) {
            if (!mutableListOf(*langDefault).contains(codelangDefault)) {
                "en"
            } else {
                codelangDefault
            }
        } else {
            SystemUtil.getPreLanguage(this)
        }
    }

    private fun initData() {
        var pos = 0
        listLanguage.clear()
        listLanguage.addAll(SystemUtil.listLanguage())
//        listLanguage.forEachIndexed { index, languageModel ->
//            if (languageModel.code == codeLang) {
//                pos = index
//                binding.ivDone.visible()
//                return@forEachIndexed
//            }
//        }
//        val temp = listLanguage[pos]
//        temp.active = true
//        listLanguage.removeAt(pos)
//        listLanguage.add(0, temp)

        adapter.addList(listLanguage)
    }



}