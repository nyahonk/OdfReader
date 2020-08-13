package com.nyahonk.odfreader.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient

import com.nyahonk.odfreader.domain.StreamUtils

class PageView constructor(context: Context, attributeSet: AttributeSet) : WebView(
    context,
    attributeSet
) {

    private lateinit var documentFragment: DocumentFragment

    init {
        settings.apply {
            builtInZoomControls = true
            displayZoomControls = true
            setSupportZoom(true)
            defaultTextEncodingName = StreamUtils.ENCODING
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        addJavascriptInterface(this, "paragraphListener")

        keepScreenOn = true
        try {
            val method = context.javaClass.getMethod(
                "setSystemUiVisibility", Int::class.java
            )
            method.invoke(context, 1)
        } catch (e: Exception) {

        }

        webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return try {
                    getContext().startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        // taken from: https://stackoverflow.com/a/10069265/198996
        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            try {
                getContext().startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            } catch (e: Exception) {
            }
        }
    }

    fun setDocumentFragment(documentFragment: DocumentFragment) {
        this.documentFragment = documentFragment
    }
}