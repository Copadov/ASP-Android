package asp.android.aspandroidmaterial.ui.layouts

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import asp.android.aspandroidmaterial.R
import com.airbnb.lottie.LottieAnimationView

class ASPMaterialDialogCustom : DialogFragment() {

    private var aSPMaterialDialogCustomListener:
            ASPMaterialDialogCustomListener? = null

    companion object {

        const val TAG = "SimpleDialog"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private const val KEY_BUTTON_OPTION_1 = "KEY_BUTTON_OPTION_1"
        private const val KEY_ICON_TYPE = "ICON_TYPE"
        private const val VISIBLE_ACCEPT_BUTTON = "VISIBLE_ACCEPT_BUTTON"
        private const val KEY_OPTION_TYPE = "OPTION_TYPE"
        private const val KEY_TEXT_BUTTON = "KEY_TEXT_BUTTON"

        fun newInstance(
            title: String,
            subTitle: String,
            textOption1: String,
            dialogType: Int,
            visibleAcceptButton: Boolean,
            optionType: Int = 0,
            buttonTxt:String? = null
        ): ASPMaterialDialogCustom {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            args.putString(KEY_BUTTON_OPTION_1, textOption1)
            args.putInt(KEY_ICON_TYPE, dialogType)
            args.putBoolean(VISIBLE_ACCEPT_BUTTON, visibleAcceptButton)
            args.putInt(KEY_OPTION_TYPE, optionType) // Agregar el valor de optionType
               buttonTxt.let {
                   if(it!=null) args.putString(KEY_TEXT_BUTTON,buttonTxt)
               }
            val fragment = ASPMaterialDialogCustom()
            fragment.arguments = args
            return fragment
        }

    }

    fun setASPMaterialDialogCustomListener(
        aspMaterialDialogCustomListener: ASPMaterialDialogCustomListener
    ) {
        this.aSPMaterialDialogCustomListener = aspMaterialDialogCustomListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.asp_custom_dialog, container)

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

    private fun setupView(view: View) {
        view.findViewById<TextView>(R.id.textViewDialogTitle).text =
            arguments?.getString(KEY_TITLE)
        view.findViewById<TextView>(R.id.textViewDialogDescription).text =
            arguments?.getString(KEY_SUBTITLE)
        view.findViewById<TextView>(R.id.textViewDialogOptionTitle1).text =
            arguments?.getString(KEY_BUTTON_OPTION_1)

        val lottieAnimation = when (arguments?.getInt(KEY_ICON_TYPE)) {
            DialogIconType.ERROR.ordinal -> {
                R.raw.dialog_error_icon
            }
            DialogIconType.INFO.ordinal -> {
                R.raw.dialog_info_icon
            }
            DialogIconType.SUCCESS.ordinal -> {
                R.raw.dialog_sucess_icon
            }
            else -> {
                R.raw.dialog_info_icon
            }
        }

        view.findViewById<Button>(R.id.buttonAccept).isVisible = arguments?.getBoolean(
            VISIBLE_ACCEPT_BUTTON, false
        )!!

        view.findViewById<Button>(R.id.buttonAccept).text = "Aceptar"

        view.findViewById<Button>(R.id.buttonAccept).setOnClickListener {
            val optionType = arguments?.getInt(KEY_OPTION_TYPE) // Obtener el valor de optionType
            aSPMaterialDialogCustomListener?.onClickAcceptButton(optionType!!) // Pasar optionType como argumento
        }

        view.findViewById<LottieAnimationView>(R.id.imageViewIconDialog)
            .setAnimation(lottieAnimation)

        if(arguments?.containsKey(KEY_TEXT_BUTTON)!!){
            view.findViewById<Button>(R.id.buttonAccept).text = arguments?.getString(KEY_TEXT_BUTTON)
        }
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<TextView>(
            R.id.textViewDialogOptionTitle1
        ).setOnClickListener {
            aSPMaterialDialogCustomListener?.onClickClose()
            dismiss()
        }
        view.findViewById<TextView>(
            R.id.textViewDialogOptionTitle2
        ).setOnClickListener {
            aSPMaterialDialogCustomListener?.onClickClose()
            dismiss()
        }
    }

    enum class DialogIconType(val id: Int) {
        undefined(0),
        ERROR(1),
        INFO(2),
        SUCCESS(3)
    }

    interface ASPMaterialDialogCustomListener {
        fun onClickAcceptButton(optionType: Int) // Agregar el parámetro optionType
        fun onClickClose()
    }
}