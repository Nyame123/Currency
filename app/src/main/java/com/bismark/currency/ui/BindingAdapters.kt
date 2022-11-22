package com.bismark.currency.ui

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bismark.currency.R
import com.bismark.currency.ui.converter.state.ConversionRateState

/**
 * Adapter to control visibility of progress bar in databinding
 **/
@BindingAdapter("hideOnLoading")
fun ProgressBar.showOnLoading(conversionRateState: ConversionRateState?) {
    conversionRateState?.let {
        visibility = if (conversionRateState is ConversionRateState.Loading) View.VISIBLE else View.GONE
    }
}

/**
 * Adapter to control visibility of Error textview in databinding
 **/
@BindingAdapter("showOnError")
fun TextView.showOnError(conversionRateState: ConversionRateState?) {
    conversionRateState?.let {
        when (conversionRateState) {
            is ConversionRateState.Error -> {
                visibility = View.VISIBLE
                text = conversionRateState.message
            }

            else -> {
                visibility = View.GONE
            }
        }
    }
}

/**
 * Adapter to control what is shown on the textview based on the UI state in databinding
 **/
@BindingAdapter("baseCurrency", "rate", requireAll = false)
fun TextView.displayText(baseCurrency: String?, rate: ConversionRateState?) {
    when (rate) {
        is ConversionRateState.Error -> {
            setTextColor(ContextCompat.getColor(context, R.color.red))
            text = rate.message
        }

        is ConversionRateState.Success -> {
            rate.data.rates?.get(baseCurrency)?.let {
                setTextColor(ContextCompat.getColor(context, R.color.black))
                text = it.toString()
            }
        }

        else -> {}
    }
}

/**
 * Adapter to enable entry on Success only in databinding
 **/
@BindingAdapter("enableOnSuccess")
fun AppCompatEditText.enableOnSuccess(conversionRateState: ConversionRateState?) {
    conversionRateState?.let {
        isEnabled = when (conversionRateState) {
            is ConversionRateState.Success -> {
                true
            }

            else -> {
                false
            }
        }
    }
}
