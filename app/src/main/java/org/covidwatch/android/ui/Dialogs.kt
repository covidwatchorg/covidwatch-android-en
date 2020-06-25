package org.covidwatch.android.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import org.covidwatch.android.R
import org.covidwatch.android.databinding.DialogTestVerificationCodeInfoBinding

object Dialogs {
    fun testVerificationCodeInfo(context: Context) {
        val dialogView =
            DialogTestVerificationCodeInfoBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView.root)
            .create()
        dialogView.closeButton.setOnClickListener { dialog.dismiss() }
        dialogView.btnLearnMore.setOnClickListener {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.faq))
                )
            )
            dialog.dismiss()
        }
        dialog.show()
    }
}