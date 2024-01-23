package com.labs.foodium.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import coil.load
import com.labs.foodium.R
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _overviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)
        binding.mainImageView.load(overViewDetails?.image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        binding.titleTextView.text = overViewDetails?.title
        binding.likesTextView.text = overViewDetails?.aggregateLikes.toString()
        binding.timeTextView.text = overViewDetails?.readyInMinutes.toString()
        overViewDetails?.summary.let {summary ->
            val summaryCleaned = Jsoup.parse(summary.toString()).text()
            binding.summaryTextView.text = summaryCleaned
        }

        if (overViewDetails?.vegetarian == true) {
            binding.vegetarianImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            binding.vegetarianTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if (overViewDetails?.vegan == true) {
            binding.veganImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            binding.veganTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if (overViewDetails?.glutenFree == true) {
            binding.glutenFreeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            binding.glutenFreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }


        if (overViewDetails?.dairyFree == true) {
            binding.dairyFreeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            binding.dairyFreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if (overViewDetails?.veryHealthy == true) {
            binding.healthyImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            binding.healthyTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if (overViewDetails?.cheap == true) {
            binding.cheapImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            binding.cheapTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _overviewBinding = null
    }
}