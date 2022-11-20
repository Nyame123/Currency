package com.bismark.currency.ui

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.ui.converter.state.ConversionRateState

/**
 * Adapter to control visibility of progress bar in databinding
 **/
@BindingAdapter("hideOnLoading")
fun ProgressBar.showOnLoading(conversionRateState: ConversionRateState) {
    visibility = if (conversionRateState is ConversionRateState.Loading) View.VISIBLE else View.GONE
}

/**
 * Adapter to control visibility of Error textview in databinding
 **/
@BindingAdapter("showOnError")
fun TextView.showOnError(conversionRateState: ConversionRateState) {
    when (conversionRateState) {
        is Error -> {
            visibility = View.VISIBLE
            text = conversionRateState.message
        }

        else -> {
            visibility = View.GONE
        }
    }
}

/**
 * Adapter to enable entry on Success only in databinding
 **/
@BindingAdapter("enableOnSuccess")
fun AppCompatEditText.enableOnSuccess(conversionRateState: ConversionRateState) {
    isEnabled = when (conversionRateState) {
        is ConversionRateState.Success -> {
            true
        }

        else -> {
            false
        }
    }
}

/**
 * Adapter to react to amount changes on `Currency Amount` changes in databinding
 **/
@BindingAdapter("amountChanges", "rate", "baseCurrency", requireAll = false)
fun AppCompatEditText.reactToOnFromAmountChanges(amountChanges: Double, rate: ConversionResultRaw?, baseCurrency: String) {
    rate?.rates?.get(baseCurrency)?.let {
        setText((it * amountChanges).toString())
    }
}


