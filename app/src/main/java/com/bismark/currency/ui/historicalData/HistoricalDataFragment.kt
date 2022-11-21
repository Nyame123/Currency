package com.bismark.currency.ui.historicalData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bismark.currency.R
import com.bismark.currency.databinding.FragmentHistoricalDataBinding
import com.bismark.currency.ui.converter.CurrencyConverterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HistoricalDataFragment : Fragment() {

    val viewModel by activityViewModels<CurrencyConverterViewModel>()
    lateinit var historyDataBinding: FragmentHistoricalDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        historyDataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_historical_data,
            container, false
        )

        historyDataBinding.viewModel = viewModel
        historyDataBinding.lifecycleOwner = viewLifecycleOwner
        return historyDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otherCurrency()
        fetchLastThreeHistoricalRate()
    }

    private fun otherCurrency() {
        val inflater = LayoutInflater.from(requireContext())
        viewModel.conversionRateInfo?.rates?.forEach { rateValuePair ->
            val currencyLayoutView = inflater.inflate(R.layout.other_currency_item_layout, null, false)
            currencyLayoutView.findViewById<TextView>(R.id.currency_tv).text = rateValuePair.key
            currencyLayoutView.findViewById<TextView>(R.id.currency_value_tv).text = rateValuePair.value.toString()
            historyDataBinding.otherCurrenciesContent.addView(currencyLayoutView)
        }
    }

    private fun fetchLastThreeHistoricalRate() {
        val historyDates = mutableListOf<String>().apply {
            add(getModifiedDateBy(-1))
            add(getModifiedDateBy(-2))
            add(getModifiedDateBy(-3))
        }
        viewModel.fetchHistoricalRate(
            urls = historyDates,
            base = viewModel.fromCurrencySelected.value,
            symbols = viewModel.toCurrencySelected.value
        )
    }

    private fun getModifiedDateBy(difference: Int): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, difference)

        return simpleDateFormat.format(calendar.time)
    }
}
