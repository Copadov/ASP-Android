package asp.android.asppagos.ui.fragments.onboarding

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.AddressResponseData
import asp.android.asppagos.databinding.FragmentFormAddressBinding
import asp.android.asppagos.ui.activities.PinInputActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.PROPERTY_PIN_VALUE
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogInfo
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormAddressFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private var _binding: FragmentFormAddressBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name
    private lateinit var pinInputLauncher: ActivityResultLauncher<Intent>
    val viewModel: MainDashboardViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {

            it.textAddressInformation.setTextColor(Color.GRAY)
            it.constraintLayoutOtherAddress.visibility = View.GONE

            it.continueButton.isEnabled = false

            it.CheckAddress.setOnCheckedChangeListener { _, isChecked ->
                it.constraintLayoutOtherAddress.visibility = View.GONE
                if (!isChecked) {
                    it.textAddressInformation.setTextColor(Color.GRAY)
                } else {
                    it.textAddressInformation.setTextColor(Color.BLACK)
                    it.CheckOtherAddress.isChecked = false
                }
                updateContinueButtonState(
                    it.CheckAddress.isChecked
                            || it.CheckOtherAddress.isChecked
                )
            }

            it.CheckOtherAddress.setOnCheckedChangeListener { _, isChecked ->
                val color = if (isChecked) Color.GRAY else Color.GRAY
                it.textAddressInformation.setTextColor(color)
                if (isChecked) {
                    it.CheckAddress.isChecked = false
                    it.constraintLayoutOtherAddress.visibility = View.VISIBLE
                } else {
                    it.constraintLayoutOtherAddress.visibility = View.GONE
                }
                updateContinueButtonState(
                    it.CheckAddress.isChecked
                            || it.CheckOtherAddress.isChecked
                )
            }

            it.continueButton.setOnClickListener {
                if (!binding.CheckAddress.isChecked) {
                    val address = binding.InputAddress.text.toString()
                    val numInt = binding.NumInt.text.toString()
                    val numExt = binding.NumExt.text.toString()
                    val cpCode = binding.inputCP.text.toString()
                    val colony = binding.inputColony.text.toString()
                    val city = binding.inputCity.text.toString()

                    val missingFields = mutableListOf<String>()

                    if (address.isEmpty()) {
                        missingFields.add("Calle")
                    }
                    if (numInt.isEmpty()) {
                        missingFields.add("Número interior")
                    }
                    if (numExt.isEmpty()) {
                        missingFields.add("Número exterior")
                    }
                    if (cpCode.isEmpty()) {
                        missingFields.add("Código postal")
                    }
                    if (colony.isEmpty()) {
                        missingFields.add("Colonia")
                    }
                    if (city.isEmpty()) {
                        missingFields.add("Ciudad")
                    }

                    if (missingFields.isNotEmpty()) {
                        val errorMessage =
                            "Faltan los siguientes campos: ${missingFields.joinToString(", ")}"
                        showCustomDialogInfo("Información", errorMessage)
                        return@setOnClickListener
                    }
                }

                if (binding.CheckAddress.isChecked) {
                    assignMockAddress()
                } else {
                    assignAddress()
                }

                intent = Intent(requireContext(), PinInputActivity::class.java)
                pinInputLauncher.launch(intent)
            }
        }

        initViewModel()

        pinInputLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val pinValue = result.data?.getStringExtra(PROPERTY_PIN_VALUE)
                    if (pinValue != null) {
                        dialog.show(
                            requireActivity().supportFragmentManager,
                            TAG
                        )
                        viewModel.setPincode(pinValue)
                        viewModel.cardAssign()
                    }
                }
            }
    }

    private fun assignMockAddress() {
        viewModel.setStreet("Calle 1")
        viewModel.setNumInt("203")
        viewModel.setNumExt("1A")
        viewModel.setCpCode("97260")
        viewModel.setColony("Obrera")
        viewModel.setCity("Merida")
    }

    private fun assignAddress() {
        viewModel.setStreet(binding.InputAddress.text.toString())
        viewModel.setNumInt(binding.NumInt.text.toString())
        viewModel.setNumExt(binding.NumExt.text.toString())
        viewModel.setCpCode(binding.inputCP.text.toString())
        viewModel.setColony(binding.inputColony.text.toString())
        viewModel.setCity(binding.inputCity.text.toString())
    }

    private fun initViewModel() {
        viewModel.let {
            it.getAddressResponseData.observe(viewLifecycleOwner) { responseData ->
                when (responseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        dialog.dismiss()

                        var address = Gson()
                            .fromJson<AddressResponseData>(
                                responseData.data
                            ).domicilio

                        binding.textAddressInformation.text = address
                    }

                    else -> {
                        dialog.dismiss()
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            responseData.mensaje,
                        )
                    }
                }
            }

            it.cardAssignResponseData.observe(viewLifecycleOwner) { responseData ->
                when (responseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        dialog.dismiss()

                        var response = Gson()
                            .fromJson<AddressResponseData>(
                                decryptData(
                                    responseData.data
                                )
                            )
                    }

                    else -> {
                        dialog.dismiss()
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            responseData.mensaje,
                        )
                    }
                }
            }
        }

        dialog.show(
            requireActivity().supportFragmentManager,
            TAG
        )

        viewModel.getAddress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //RxBus.publish(DataFormResponseEvent(true))
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }

    private fun updateContinueButtonState(enabled: Boolean) {
        binding.continueButton.isEnabled = enabled
    }
}