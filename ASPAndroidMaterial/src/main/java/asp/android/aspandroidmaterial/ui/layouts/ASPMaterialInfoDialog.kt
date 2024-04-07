package asp.android.aspandroidmaterial.ui.layouts

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import asp.android.aspandroidmaterial.R

class ASPMaterialInfoDialog : DialogFragment() {

    companion object {

        const val TAG = "SimpleDialog"
        const val phone = "8004627373"
        const val email = "contacto@aspintegraopciones.com"


        private var dismissListener: DialogDismissListener? = null

        fun newInstance(dismissListener: DialogDismissListener): ASPMaterialInfoDialog {
            val dialog = ASPMaterialInfoDialog()
            this.dismissListener = dismissListener
            return dialog
        }

        interface DialogDismissListener {
            fun onDialogDismissed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.asp_custom_dialog_info, container)

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
        view.let {
            view.findViewById<ConstraintLayout>(
                R.id.emailContact
            ).findViewById<TextView>(
                R.id.textTitleContact
            ).text = "Correo electrónico:"
            view.findViewById<ConstraintLayout>(
                R.id.emailContact
            ).findViewById<TextView>(
                R.id.textSubtitleContact
            ).text = email

            view.findViewById<ConstraintLayout>(
                R.id.emailContact
            ).findViewById<TextView>(
                R.id.textSubtitleContact
            ).let {
                it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }


            view.findViewById<ConstraintLayout>(
                R.id.phoneContact
            ).findViewById<TextView>(
                R.id.textTitleContact
            ).text = "Teléfono:"
            view.findViewById<ConstraintLayout>(
                R.id.phoneContact
            ).findViewById<TextView>(
                R.id.textSubtitleContact
            ).text = phone

            view.findViewById<ConstraintLayout>(
                R.id.phoneContact
            ).findViewById<TextView>(
                R.id.textSubtitleContact
            ).let {
                it.paintFlags = it.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
        }
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<TextView>(R.id.textViewDialogOptionTitle1).setOnClickListener {
            dismissListener?.onDialogDismissed()
            dismiss()
        }

        view.findViewById<ConstraintLayout>(R.id.phoneContact).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            startActivity(intent)
        }

        view.findViewById<ConstraintLayout>(R.id.emailContact).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            startActivity(intent)
        }
    }
}