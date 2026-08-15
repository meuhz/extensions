package com.example.demo.activity

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.demo.R

class BaseFragment : Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (parentFragmentManager.backStackEntryCount > 0) {
                    parentFragmentManager.popBackStack()
                } else {
                    activity?.finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun pop(name: String? = null, flags: Int = 0) {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack(name, flags)
        } else {
            activity?.finish()
        }
    }

    fun push(fragment: Fragment, tag: String? = null, name: String? = null) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_right_in, R.anim.slide_left_out,
            R.anim.slide_left_in, R.anim.slide_right_out
        )
        transaction.replace(R.id.fragment_container_view, fragment, tag)
        transaction.addToBackStack(name)
        transaction.commit()
    }
}