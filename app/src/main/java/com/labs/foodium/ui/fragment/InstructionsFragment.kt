package com.labs.foodium.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.labs.foodium.R
import com.labs.foodium.databinding.FragmentIngredientsBinding
import com.labs.foodium.databinding.FragmentInstructionsBinding
import com.labs.foodium.models.Result
import com.labs.foodium.utils.Constants


class InstructionsFragment : Fragment() {

    private var instructionData: Result? = null
    private var _instructionsBinding: FragmentInstructionsBinding? = null
    private val binding get() = _instructionsBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instructionData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(Constants.RECIPES_DETAILS, Result::class.java)
        } else {
            arguments?.getParcelable(Constants.RECIPES_DETAILS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _instructionsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_instructions, container, false)
        binding.instructionsWebView.webViewClient = object : WebViewClient() {}
        val loadWebURL: String = instructionData?.sourceUrl.toString()
        binding.instructionsWebView.loadUrl(loadWebURL)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _instructionsBinding = null
    }
}