package com.bismark.currency.ui.converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bismark.currency.R
import com.bismark.currency.databinding.FragmentCurrencyConverterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment() {

    val currencyConverterViewModel by activityViewModels<CurrencyConverterViewModel>()
    lateinit var currencyConverterBinding: FragmentCurrencyConverterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currencyConverterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_currency_converter,
            container, false
        )
        currencyConverterBinding.viewModel = currencyConverterViewModel
        currencyConverterBinding.lifecycleOwner = viewLifecycleOwner
        return currencyConverterBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleEditTextOnTextChange()
        onCurrencySelected()
        onSwapCurrencies()
        view.findViewById<Button>(R.id.detail_btn).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun handleEditTextOnTextChange() {
        with(currencyConverterBinding) {
            fromAmountEdt.doOnTextChanged{ text, _, _, _ ->
                text?.let {
                    if(text.isNotEmpty()) {
                        currencyConverterViewModel.conversionRateInfo?.rates?.get(currencyConverterViewModel.toCurrency)
                            ?.let {
                                toAmountEdt.setText((it * text.toString().toDouble()).toString())
                            }
                    }else{
                        toAmountEdt.setText("")
                    }
                }
            }
        }
    }

    private fun onSwapCurrencies() {
        currencyConverterBinding.flipBtn.setOnClickListener {
            currencyConverterBinding.toCurrencySpinner.setSelection(currencyConverterViewModel.fromCurrencyPosition)
            currencyConverterBinding.fromCurrencySpinner.setSelection(currencyConverterViewModel.toCurrencyPosition)
            currencyConverterBinding.fromAmountEdt.setText("1.0")
            currencyConverterBinding.toAmountEdt.setText("")
        }
    }

    private fun onCurrencySelected() {
        currencyConverterBinding.fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currencyConverterViewModel.fromCurrencySelected.update {
                    parent?.getItemAtPosition(position).toString()
                }
                currencyConverterBinding.fromAmountEdt.setText("1.0")
                currencyConverterBinding.toAmountEdt.setText("")
                currencyConverterViewModel.fromCurrencyPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        currencyConverterBinding.toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currencyConverterViewModel.toCurrencySelected.update {
                    parent?.getItemAtPosition(position).toString()
                }
                currencyConverterViewModel.toCurrencyPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}
