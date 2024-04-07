package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.data.events.ActivateCardResponseEvent
import asp.android.asppagos.databinding.FragmentActivePhysicCardInputBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.RxBus

class ActivePhysicCardInputFragment : BaseFragment() {

    private var _binding: FragmentActivePhysicCardInputBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivePhysicCardInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            binding.cardInput.doOnTextChanged { text, _, _, _ ->
                binding.bContinueSendMoney.isEnabled = text?.length == 19
            }

            binding.bContinueSendMoney.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        RxBus.publish(ActivateCardResponseEvent(true))
    }

}