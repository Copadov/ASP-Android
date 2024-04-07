package asp.android.aspandroidmaterial.ui.layouts

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import asp.android.aspandroidmaterial.R

class ASPMaterialMovementsFilterDialog : DialogFragment() {

    private lateinit var optionChecks: List<CheckBox>
    private var listener: FilterDialogListener? = null
    private var dialogType: String = DIALOG_TYPE_FILTER // default value

    fun setListener(listener: FilterDialogListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "FilterDialog"
        const val KEY_DIALOG_TYPE = "KEY_DIALOG_TYPE"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_BUTTON_CLOSE = "KEY_BUTTON_CLOSE"
        private const val KEY_BUTTON_ACCEPT = "KEY_BUTTON_ACCEPT"
        private const val KEY_BUTTON_FILTER_1 = "KEY_BUTTON_FILTER_1"
        private const val KEY_BUTTON_FILTER_2 = "KEY_BUTTON_FILTER_2"
        private const val KEY_BUTTON_FILTER_3 = "KEY_BUTTON_FILTER_3"

        const val DIALOG_TYPE_FILTER = "FILTER"
        const val DIALOG_TYPE_MOVEMENTS = "MOVEMENTS"
    }

    fun newInstance(
        title: String,
        buttonClose: String,
        buttonAccept: String,
        button1: String,
        button2: String,
        button3: String,
        dialogType: String // added parameter

    ): ASPMaterialMovementsFilterDialog {
        val args = Bundle()
        args.putString(KEY_TITLE, title)
        args.putString(KEY_BUTTON_CLOSE, buttonClose)
        args.putString(KEY_BUTTON_ACCEPT, buttonAccept)
        args.putString(KEY_BUTTON_FILTER_1, button1)
        args.putString(KEY_BUTTON_FILTER_2, button2)
        args.putString(KEY_BUTTON_FILTER_3, button3)
        args.putString(KEY_DIALOG_TYPE, dialogType)
        val fragment = ASPMaterialMovementsFilterDialog()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.asp_filter_movements_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }


    private fun setupClickListeners(view: View) {

        view.findViewById<TextView>(R.id.textViewDialogOptionTitle1).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.buttonAccept).setOnClickListener {
            val selectedCheckboxes = getSelectedCheckboxes()
            listener?.onFilterSelected(selectedCheckboxes, dialogType)
            dismiss()
        }
    }

    private fun getSelectedCheckboxes(): List<Int> {
        val selectedCheckboxes = mutableListOf<Int>()
        for ((index, checkbox) in optionChecks.withIndex()) {
            if (checkbox.isChecked) {
                selectedCheckboxes.add(index + 1)
            }
        }
        return selectedCheckboxes
    }

    private fun setupView(view: View) {

        view.findViewById<TextView>(R.id.textViewDialogTitle).text =
            arguments?.getString(KEY_TITLE)

        dialogType = arguments?.getString(KEY_DIALOG_TYPE, DIALOG_TYPE_FILTER)!!


        val optionIds = listOf(
            R.id.option_one,
            R.id.option_two,
            R.id.option_three
        )

        optionChecks = optionIds.map {
            view.findViewById<ConstraintLayout>(it)
                .findViewById(R.id.checkBoxOption)
        }

        optionChecks.forEachIndexed { index, optionCheck ->
            optionCheck.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    optionChecks.forEachIndexed { otherIndex, otherOptionCheck ->
                        if (index != otherIndex) {
                            otherOptionCheck.isChecked = false
                        }
                    }
                }
            }
        }

        view.findViewById<ConstraintLayout>(R.id.option_one)
            .findViewById<TextView>(R.id.textViewDescription).text =
            arguments?.getString(KEY_BUTTON_FILTER_1)

        view.findViewById<ConstraintLayout>(R.id.option_two)
            .findViewById<TextView>(R.id.textViewDescription).text =
            arguments?.getString(KEY_BUTTON_FILTER_2)

        view.findViewById<ConstraintLayout>(R.id.option_three)
            .findViewById<TextView>(R.id.textViewDescription).text =
            arguments?.getString(KEY_BUTTON_FILTER_3)

        view.findViewById<Button>(R.id.buttonAccept).text =
            arguments?.getString(KEY_BUTTON_ACCEPT)

        view.findViewById<TextView>(R.id.textViewDialogOptionTitle1).text =
            arguments?.getString(KEY_BUTTON_CLOSE)
    }

    interface FilterDialogListener {
        fun onFilterSelected(selectedCheckboxes: List<Int>, dialogType: String)
    }
}