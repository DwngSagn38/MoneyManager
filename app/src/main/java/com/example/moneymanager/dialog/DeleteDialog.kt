package com.example.moneymanager.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.example.moneymanager.databinding.DialogDeleteBinding
import com.example.moneymanager.base.BaseDialog
import com.example.moneymanager.widget.tap

class DeleteDialog (
    activity1: Activity,
    val content: String? = null,
    private var action: () -> Unit,
) : BaseDialog<DialogDeleteBinding>(activity1, true) {


    override fun getContentView(): DialogDeleteBinding {
        return DialogDeleteBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.root.tap { dismiss() }
        binding.apply {

            tvNo.tap {
                dismiss()
            }

            tvYes.tap {
                action.invoke()
                dismiss()
            }
        }
    }
}