package ru.n08i40k.polytechnic.next.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import ru.n08i40k.polytechnic.next.Application

fun Context.openLink(link: String) =
    this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)

val Context.app get() = this.applicationContext as Application