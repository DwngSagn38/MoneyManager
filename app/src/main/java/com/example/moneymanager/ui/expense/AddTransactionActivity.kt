package com.example.moneymanager.ui.expense

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityAddTransactionBinding
import com.example.moneymanager.model.Category
import com.example.moneymanager.widget.tap

class AddTransactionActivity : BaseActivity<ActivityAddTransactionBinding>() {
    lateinit var adapter: CategoryAdapter
    lateinit var fullList: List<Category>
    override fun setViewBinding(): ActivityAddTransactionBinding {
        return ActivityAddTransactionBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val type = intent.getIntExtra("EXTRA_TYPE", -1)
        fullList = getCatagoryList(type)

        adapter = CategoryAdapter(fullList) { category ->
            val intent = Intent(this, SaveTransactionActivity::class.java)
            intent.putExtra("CATEGORY_DATA", category)
            startActivity(intent)
            finish()
        }

        binding.recyclerViewCategory.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerViewCategory.adapter = adapter
    }


    fun filterByName(query: String) {
        val filtered = fullList.filter { it.name.contains(query, ignoreCase = true) }
        adapter.updateList(filtered)
    }

    override fun viewListener() {
        binding.edtSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            filterByName(query)
        }
        binding.ivBack.tap {
            finish()
        }
    }


    override fun dataObservable() {
    }

}