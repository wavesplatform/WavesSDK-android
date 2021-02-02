package com.mysite.mywavesapplication.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun Context.copyToClipboard(text: String) {
    val clipboard =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("seed", text)
    clipboard?.primaryClip = clip

    Toast.makeText(this, "Text is copied to the clipboard", Toast.LENGTH_SHORT)
        .show()
}
