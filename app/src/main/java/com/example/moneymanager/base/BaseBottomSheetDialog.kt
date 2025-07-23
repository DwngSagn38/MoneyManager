package com.example.moneymanager.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.example.moneymanager.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialog<VB : ViewBinding> : BottomSheetDialogFragment() {
    lateinit var binding: VB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = makeBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = (dialog as BottomSheetDialog?)?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            val layoutParams = it.layoutParams
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.layoutParams = layoutParams

        }

        dialog?.window?.decorView?.setBackgroundResource(android.R.color.transparent)
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setupView()
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
    protected abstract fun makeBinding(inflater: LayoutInflater, container: ViewGroup?): VB


    protected abstract fun setupView()

    override fun onStart() {
        super.onStart()

    }

}
