package com.example.dictionaryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        webView = findViewById(R.id.webView)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)

        // 配置 WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // 默认加载 vortaro.cn
        webView.loadUrl("https://vortaro.cn")

        btnSearch.setOnClickListener {
            val word = etSearch.text.toString().trim()
            if (word.isNotEmpty()) {
                searchWord(word)
            } else {
                Toast.makeText(this, R.string.enter_word_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchWord(word: String) {
        try {
            // vortaro.cn 搜索 URL 格式
            val searchUrl = "https://vortaro.cn/#/search?word=${Uri.encode(word)}"
            webView.loadUrl(searchUrl)
        } catch (e: Exception) {
            // 如果 WebView 加载失败，尝试用浏览器打开
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vortaro.cn"))
                startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(this, R.string.open_dict_failed_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openInBrowser(view: android.view.View) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vortaro.cn"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.open_browser_failed_toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
