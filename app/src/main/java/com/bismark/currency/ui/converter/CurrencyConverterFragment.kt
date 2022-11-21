package com.bismark.currency.ui.converter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bismark.currency.R
import com.bismark.currency.databinding.FragmentCurrencyConverterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment() {

    val viewModel by activityViewModels<CurrencyConverterViewModel>()
    lateinit var currencyConverterBinding: FragmentCurrencyConverterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currencyConverterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_currency_converter,
            container, false
        )
        currencyConverterBinding.viewModel = viewModel
        currencyConverterBinding.lifecycleOwner = viewLifecycleOwner
        return currencyConverterBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCurrencySelected()
        onSwapCurrencies()
        view.findViewById<Button>(R.id.detail_btn).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun onSwapCurrencies(){
        currencyConverterBinding.flipBtn.setOnClickListener {
            currencyConverterBinding.toCurrencySpinner.setSelection(viewModel.fromCurrencyPosition)
            currencyConverterBinding.fromCurrencySpinner.setSelection(viewModel.toCurrencyPosition)
        }
    }

    private fun onCurrencySelected() {
        currencyConverterBinding.fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.fromCurrencySelected.update {
                    parent?.getItemAtPosition(position).toString()
                }
                viewModel.fromCurrencyPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        currencyConverterBinding.toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.toCurrencySelected.update {
                    parent?.getItemAtPosition(position).toString()
                }
                viewModel.toCurrencyPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}
