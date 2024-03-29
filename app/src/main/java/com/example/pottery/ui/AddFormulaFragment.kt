package com.example.pottery.ui

import android.app.Activity.MODE_PRIVATE
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pottery.R
import com.example.pottery.adapters.NewItemAdapter
import com.example.pottery.databinding.FragmentAddFormulaBinding
import com.example.pottery.room.Formula
import com.example.pottery.viewModels.FormulaViewModel
import com.example.pottery.viewModels.NewItem
import java.io.IOException
import java.util.*

const val REQUEST_ID_MULTIPLE_PERMISSIONS=10
class AddFormulaFragment : Fragment() {

    private lateinit var binding: FragmentAddFormulaBinding
    private val viewModel: FormulaViewModel by viewModels()
    private var currentPhotoPath = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFormulaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chooseImage = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            if (it != null) {
                val name = UUID.randomUUID().toString()
                savePhotoToInternalStorage(name, it)
                currentPhotoPath = name
                binding.ivPicture.setImageBitmap(it)
            }
        }
        binding.ivPicture.setOnClickListener {
            chooseImage.launch()
        }
        viewModel.itemListLiveData.observe(viewLifecycleOwner) {
            if (it != null){
                val adapter = NewItemAdapter { item ->
                    val updated = viewModel.itemListLiveData.value
                    updated?.remove(item)
                    viewModel.itemListLiveData.value = updated
                }
                binding.recyclerView.adapter = adapter
                adapter.submitList(it)
            }
        }
        binding.btnAddItem.setOnClickListener {
            if (hasEmptyField()) {
                checkForErrors()
                return@setOnClickListener
            }
            if (viewModel.isNewItemRepeated(NewItem(binding.etCode.text.toString(),binding.etMaterial.text.toString(),
                    getAmount()))
            ){
                Toast.makeText(requireContext(), R.string.`already_exist_ّItem`, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addItem()
            Toast.makeText(requireContext(), R.string.item_added, Toast.LENGTH_SHORT).show()
            binding.apply {
                etAmount.setText("")
                etMaterial.setText("")
                etCode.setText("")
            }
        }
        binding.btnCreate.setOnClickListener {
            if (binding.etFormulaName.text.isNullOrEmpty()) {
                    setError(binding.etFormulaName)
                    return@setOnClickListener
                }

            if (!viewModel.isFormulaNew(binding.etFormulaName.text.toString())) {
                Toast.makeText(requireContext(), R.string.`already_exist_ّFormula`, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.insertFormula(Formula(0,binding.etFormulaName.text.toString(),currentPhotoPath))
            viewModel.insertItems(binding.etFormulaName.text.toString())
            findNavController().navigate(R.id.action_addFormulaFragment_to_homeFragment)
            Toast.makeText(requireContext(),R.string.successfully_saved, Toast.LENGTH_SHORT).show()
            }
        }
    private fun hasEmptyField(): Boolean {
        return (binding.etCode.text.isNullOrEmpty() || binding.etMaterial.text.isNullOrEmpty() ||
                binding.etAmount.text.isNullOrEmpty())
    }

    private fun checkForErrors() {
        setError(binding.etCode)
        setError(binding.etMaterial)
        setError(binding.etAmount)
    }

    private fun setError(editText: EditText) {
        if (editText.text.isNullOrEmpty())
            editText.error = resources.getString(R.string.must_be_filled)
    }

    private fun addItem() {
        val item = NewItem(binding.etCode.text.toString(),
            binding.etMaterial.text.toString(),
            getAmount())
        if (!viewModel.isNewItemRepeated(item)){
            viewModel.addItemToList(item)
            return
        }
    }
    private fun isDouble(userInput: String): Boolean {
        return userInput.contains(".")
    }
    private fun getAmount():Double{
        return if (!isDouble( binding.etAmount.text.toString()))
            binding.etAmount.text.toString().toInt().toDouble()
        else
            binding.etAmount.text.toString().toDouble()
    }
    private fun savePhotoToInternalStorage(filename:String,bmp:Bitmap):Boolean{
        return try {
            context?.openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG,95,stream))
                    throw IOException("COULDN'T SAVE BITMAP")
            }
            true
        }catch (e:IOException){
            e.printStackTrace()
            false
        }
    }
}