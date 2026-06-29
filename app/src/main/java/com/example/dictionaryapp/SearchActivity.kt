package com.example.dictionaryapp

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnPaste: ImageButton

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        webView = findViewById(R.id.webView)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnPaste = findViewById(R.id.btnPaste)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.databaseEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                return false
            }
        }
        webView.webChromeClient = WebChromeClient()

        webView.loadUrl("https://vortaro.cn")

        btnPaste.setOnClickListener {
            pasteFromClipboard()
        }

        btnSearch.setOnClickListener {
            val word = etSearch.text.toString().trim()
            if (word.isNotEmpty()) {
                searchWord(word)
            } else {
                Toast.makeText(this, R.string.enter_word_toast, Toast.LENGTH_SHORT).show()
            }
        }

        autoPasteFromClipboard()
    }

    private fun autoPasteFromClipboard() {
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0).text?.toString()?.trim()
                    if (!text.isNullOrEmpty() && text.length <= 50) {
                        etSearch.setText(text)
                        etSearch.setSelection(text.length)
                    }
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun pasteFromClipboard() {
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0).text?.toString()?.trim()
                    if (!text.isNullOrEmpty()) {
                        etSearch.setText(text)
                        etSearch.setSelection(text.length)
                        Toast.makeText(this, "已粘贴: $text", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "剪切板为空", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "剪切板为空", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "剪切板为空", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "无法读取剪切板", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchWord(word: String) {
        try {
            val jsCode = """
                (function() {
                    var inputs = document.querySelectorAll('input[type="text"], input[type="search"], input:not([type])');
                    for (var i = 0; i < inputs.length; i++) {
                        var input = inputs[i];
                        if (input.offsetParent !== null) {
                            var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;
                            nativeInputValueSetter.call(input, '$word');
                            input.dispatchEvent(new Event('input', { bubbles: true }));
                            input.dispatchEvent(new Event('change', { bubbles: true }));
                            var enterEvent = new KeyboardEvent('keypress', {
                                key: 'Enter', code: 'Enter', keyCode: 13, which: 13, bubbles: true
                            });
                            input.dispatchEvent(enterEvent);
                            var buttons = document.querySelectorAll('button');
                            for (var j = 0; j < buttons.length; j++) {
                                var btn = buttons[j];
                                if (btn.textContent.includes('搜索') || btn.textContent.includes('Search') || btn.type === 'submit') {
                                    btn.click();
                                    break;
                                }
                            }
                            return true;
                        }
                    }
                    return false;
                })();
            """.trimIndent()

            webView.evaluateJavascript(jsCode) { result ->
                if (result == "true") {
                    Toast.makeText(this, "正在搜索: $word", Toast.LENGTH_SHORT).show()
                } else {
                    val searchUrl = "https://vortaro.cn/#/search?word=${Uri.encode(word)}"
                    webView.loadUrl(searchUrl)
                    Toast.makeText(this, "正在加载搜索结果...", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            val searchUrl = "https://vortaro.cn/#/search?word=${Uri.encode(word)}"
            webView.loadUrl(searchUrl)
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

    @Deprecated("Use OnBackPressedCallback instead")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
