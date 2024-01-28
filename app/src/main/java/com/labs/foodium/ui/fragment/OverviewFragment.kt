package com.labs.foodium.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import coil.load
import com.labs.foodium.R
import com.labs.foodium.bindingAdapter.RecipesRowBinding
import com.labs.foodium.databinding.FragmentOverviewBinding
import com.labs.foodium.models.Result
import com.labs.foodium.utils.Constants
import com.labs.foodium.utils.Constants.Companion.RECIPES_DETAILS
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {

    private var overViewDetails: Result? = null
    private var _overviewBinding: FragmentOverviewBinding? = null
    private val binding get() = _overviewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overViewDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(RECIPES_DETAILS, Result::class.java)
        } else {
            arguments?.getParcelable(RECIPES_DETAILS)
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _overviewBinding = FragmentOverviewBinding.inflate(inflater, container, false)
        binding.mainImageView.load(overViewDetails?.image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        binding.titleTextView.text = overViewDetails?.title
        binding.likesTextView.text = overViewDetails?.aggregateLikes.toString()
        binding.timeTextView.text = overViewDetails?.readyInMinutes.toString()
        RecipesRowBinding.parseHtml(binding.summaryTextView, overViewDetails?.summary)


        updateTheColors(overViewDetails?.vegetarian, binding.vegetarianImageView, binding.vegetarianTextView)
        updateTheColors(overViewDetails?.vegan, binding.veganImageView, binding.veganTextView)
        updateTheColors(overViewDetails?.glutenFree, binding.glutenFreeImageView, binding.glutenFreeTextView)
        updateTheColors(overViewDetails?.dairyFree, binding.dairyFreeImageView, binding.dairyFreeTextView)
        updateTheColors(overViewDetails?.veryHealthy, binding.healthyImageView, binding.healthyTextView)
        updateTheColors(overViewDetails?.cheap, binding.cheapImageView, binding.cheapTextView)

        return binding.root
    }

    private fun updateTheColors(stateIsOn: Boolean?, imageView: ImageView, textView: TextView) {
        if (stateIsOn == true) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _overviewBinding = null
    }
}