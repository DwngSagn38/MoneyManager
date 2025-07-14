package com.example.moneymanager.view.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.example.moneymanager.databinding.DialogPermissionBinding
import com.example.moneymanager.base.BaseDialog


class PermissionDialog(
    activity1: Activity,
    private var action: () -> Unit
) : BaseDialog<DialogPermissionBinding>(activity1, true) {
    override fun getContentView(): DialogPermissionBinding {
        return DialogPermissionBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {

    }

    override fun bindView() {
        binding.apply {
//            txtGo.tap {
//                action.invoke()
//                dismiss()
//            }
        }
    }


}