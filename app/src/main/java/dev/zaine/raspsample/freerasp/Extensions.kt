package dev.zaine.raspsample.freerasp

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

fun AlertDialog.Builder.positiveButton(
    @StringRes resId: Int,
    handleClick: (which: Int, DialogInterface) -> Unit = { _, _ -> }
) {
    this.setPositiveButton(resId) { dialogInterface, which -> handleClick(which, dialogInterface) }
}

fun Context.showAlertDialog(theme: Int = 0, dialogBuilder: AlertDialog.Builder.() -> Unit) {
    val builder = if (theme != 0) AlertDialog.Builder(this, theme) else AlertDialog.Builder(this)
    builder.dialogBuilder()
    val dialog = builder.create()
    dialog.show()
}