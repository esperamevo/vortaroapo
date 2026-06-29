# 词典悬浮窗APK构建指南

## 项目简介
这是一个带有悬浮窗功能的词典应用，支持点击悬浮球打开词典查询界面，查询vortaro.cn词典网站。

---

## 方法一：Android Studio构建（推荐）

### 步骤1：创建项目目录
在你的电脑上创建以下目录结构：

```
DictionaryApp/
├── build.gradle
├── settings.gradle
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── dictionaryapp/
│           │               ├── MainActivity.kt
│           │               ├── SearchActivity.kt
│           │               └── FloatService.kt
│           └── res/
│               ├── drawable/
│               │   ├── bg_float_circle.xml
│               │   ├── ic_launcher_foreground.xml
│               │   └── ic_search.xml
│               ├── layout/
│               │   ├── activity_main.xml
│               │   ├── activity_search.xml
│               │   └── view_float.xml
│               ├── mipmap-anydpi-v26/
│               │   ├── ic_launcher.xml
│               │   └── ic_launcher_round.xml
│               ├── values/
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── values-night/
│                   └── themes.xml
```

### 步骤2：下载/打开项目
1. 打开Android Studio
2. 选择 **File → Open**
3. 选择创建的DictionaryApp文件夹
4. 等待Gradle同步完成（右下角进度条完成）

### 步骤3：构建APK
1. 在菜单栏选择 **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. 等待构建完成（底部会显示"BUILD SUCCESSFUL"）
3. 构建完成后，右下角会有通知，点击 **locate** 查看APK位置
4. APK文件位置：`app/build/outputs/apk/debug/app-debug.apk`

### 步骤4：安装到手机
1. 将APK文件传输到手机
2. 在手机上打开APK文件进行安装
3. 如提示"未知来源"，请在设置中允许安装

---

## 方法二：Gradle命令行构建（需要JDK 17）

如果你已安装JDK 17和Android SDK，可以在项目目录运行：

```bash
# Windows
gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

构建完成后，APK位置：`app/build/outputs/apk/debug/app-debug.apk`

---

## 方法三：GitHub Actions自动构建

### 步骤1：上传到GitHub
1. 创建GitHub仓库
2. 将项目文件上传到仓库
3. 已包含.github/workflows/build.yml配置文件

### 步骤2：自动构建
1. 推送代码后，GitHub Actions会自动构建
2. 在Actions标签页查看构建状态
3. 构建完成后，在Artifacts区域下载APK

---

## 文件内容

### 根目录文件

**build.gradle** (根目录)
```gradle
// Top-level build file
plugins {
    id 'com.android.application' version '8.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.20' apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

**settings.gradle**
```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DictionaryApp"
include ':app'
```

### app目录文件

**app/build.gradle**
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.dictionaryapp'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.dictionaryapp"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation "androidx.core:core-ktx:1.12.0"
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "com.google.android.material:material:1.11.0"
    implementation "androidx.constraintlayout:constraintlayout:2.1.4"
}
```

**app/proguard-rules.pro**
```
# Add project specific ProGuard rules here.
```

### AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- 悬浮窗权限 -->
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <!-- 前台服务权限 -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <!-- Android 14+ 前台服务特殊类型权限 -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <!-- 网络权限（WebView 需要） -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.DictionaryApp"
        tools:targetApi="31">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".SearchActivity"
            android:exported="false" />

        <service
            android:name=".FloatService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="specialUse" />

    </application>

</manifest>
```

### Kotlin代码文件

**MainActivity.kt**
```kotlin
package com.example.dictionaryapp

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var floatService: FloatService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: android.os.IBinder?) {
            val binder = service as FloatService.FloatBinder
            floatService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            floatService = null
        }
    }

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.permission_granted_toast, Toast.LENGTH_SHORT).show()
            startFloatService()
        } else {
            Toast.makeText(this, R.string.permission_denied_toast, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopFloatService()
            Toast.makeText(this, R.string.float_stopped_toast, Toast.LENGTH_SHORT).show()
        }

        // 检查并请求悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionDialog()
        } else {
            startFloatService()
        }
    }

    override fun onResume() {
        super.onResume()
        // 如果权限被撤销，提示用户
        if (!Settings.canDrawOverlays(this) && isBound) {
            Toast.makeText(this, R.string.permission_revoked_toast, Toast.LENGTH_SHORT).show()
            stopFloatService()
        }
    }

    private fun showOverlayPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_dialog_title)
            .setMessage(R.string.permission_dialog_message)
            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    overlayPermissionLauncher.launch(intent)
                } catch (e: Exception) {
                    // 某些设备可能需要跳转到特定页面
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
            .setNegativeButton(R.string.exit) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun startFloatService() {
        val intent = Intent(this, FloatService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopFloatService() {
        val intent = Intent(this, FloatService::class.java)
        stopService(intent)
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
        }
    }
}
```

**SearchActivity.kt**
```kotlin
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
```

**FloatService.kt**
```kotlin
package com.example.dictionaryapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat

class FloatService : Service() {

    private var windowManager: WindowManager? = null
    private var floatView: View? = null
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var isFirstTouch = true

    inner class FloatBinder : Binder() {
        fun getService(): FloatService = this@FloatService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        initFloatView()
    }

    private fun isValidPosition(x: Int, y: Int): Boolean {
        val displayMetrics = resources.displayMetrics
        val width = floatView?.width ?: 0
        val height = floatView?.height ?: 0
        return x >= 0 && y >= 0 && x + width <= displayMetrics.widthPixels && y + height <= displayMetrics.heightPixels
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_search)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun initFloatView() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 创建悬浮视图
        floatView = View.inflate(this, R.layout.view_float, null)

        // 设置布局参数
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
            x = 0
            y = 100
        }

        // 触摸事件处理：拖拽 + 点击
        floatView?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX
                    lastY = event.rawY
                    isFirstTouch = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 计算移动距离
                    val dx = event.rawX - lastX
                    val dy = event.rawY - lastY

                    // 如果移动距离超过阈值，认为是拖拽
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isFirstTouch = false
                        val newX = params.x + dx.toInt()
                        val newY = params.y + dy.toInt()

                        // 边界检查：防止拖出屏幕
                        if (isValidPosition(newX, newY)) {
                            params.x = newX
                            params.y = newY
                            windowManager?.updateViewLayout(floatView, params)
                        }
                    }

                    lastX = event.rawX
                    lastY = event.rawY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 如果没有明显移动，视为点击
                    if (isFirstTouch) {
                        openSearchActivity()
                    }
                    true
                }
                else -> false
            }
        }

        windowManager?.addView(floatView, params)
    }

    private fun openSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    fun removeFloatView() {
        floatView?.let {
            windowManager?.removeView(it)
            floatView = null
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return FloatBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeFloatView()
        windowManager = null
    }

    companion object {
        const val CHANNEL_ID = "dictionary_float_channel"
        const val NOTIFICATION_ID = 1
    }
}
```

### 布局文件

**activity_main.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/float_started_message"
        android:textSize="18sp"
        android:gravity="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.3" />

    <Button
        android:id="@+id/btnStop"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/stop_float_button"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.6" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**activity_search.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="8dp">

        <EditText
            android:id="@+id/etSearch"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="@string/search_hint"
            android:inputType="text"
            android:maxLines="1"
            android:imeOptions="actionSearch" />

        <Button
            android:id="@+id/btnSearch"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/search_button" />

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/open_in_browser_button"
            android:onClick="openInBrowser" />

    </LinearLayout>

    <WebView
        android:id="@+id/webView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</LinearLayout>
```

**view_float.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <ImageView
        android:id="@+id/iv_float"
        android:layout_width="56dp"
        android:layout_height="56dp"
        android:layout_gravity="center"
        android:background="@drawable/bg_float_circle"
        android:padding="12dp"
        android:scaleType="center"
        android:src="@drawable/ic_search"
        android:contentDescription="@string/float_ball_content_description" />

</FrameLayout>
```

### Drawable文件

**bg_float_circle.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="#FF6200EE" />
    <size
        android:width="56dp"
        android:height="56dp" />
</shape>
```

**ic_search.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorOnPrimary">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z"/>
</vector>
```

**ic_launcher_foreground.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group android:scaleX="2.61"
        android:scaleY="2.61"
        android:translateX="22.68"
        android:translateY="22.68">
        <path
            android:fillColor="#FFFFFF"
            android:pathData="M15.5,14h-0.79l-0.28,-0.27C15.41,12.59 16,11.11 16,9.5 16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16c1.61,0 3.09,-0.59 4.23,-1.57l0.27,0.28v0.79l5,4.99L20.49,19l-4.99,-5zM9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z"/>
    </group>
</vector>
```

### Mipmap文件

**mipmap-anydpi-v26/ic_launcher.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/purple_500" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

**mipmap-anydpi-v26/ic_launcher_round.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/purple_500" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

### Values文件

**values/colors.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="white">#FFFFFFFF</color>
    <color name="black">#FF000000</color>
</resources>
```

**values/strings.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">词典悬浮窗</string>
    <string name="float_started_message">词典悬浮窗已启动\n请在其他应用界面查看悬浮球</string>
    <string name="stop_float_button">停止悬浮窗</string>
    <string name="search_hint">输入单词查询</string>
    <string name="search_button">查询</string>
    <string name="open_in_browser_button">浏览器打开</string>
    <string name="float_ball_content_description">词典悬浮球</string>
    <string name="permission_dialog_title">需要悬浮窗权限</string>
    <string name="permission_dialog_message">词典悬浮窗需要"显示在其他应用上层"权限，请在设置中开启。</string>
    <string name="go_to_settings">去开启</string>
    <string name="exit">退出</string>
    <string name="permission_granted_toast">权限已获取，正在启动悬浮窗...</string>
    <string name="permission_denied_toast">未授予权限，应用无法使用悬浮窗功能</string>
    <string name="float_stopped_toast">悬浮窗已停止</string>
    <string name="permission_revoked_toast">悬浮窗权限已取消，请重新授权</string>
    <string name="enter_word_toast">请输入要查询的单词</string>
    <string name="open_dict_failed_toast">无法打开词典网站</string>
    <string name="open_browser_failed_toast">无法打开浏览器</string>
    <string name="notification_channel_name">词典悬浮窗</string>
    <string name="notification_channel_description">保持词典悬浮窗运行</string>
    <string name="notification_title">词典悬浮窗运行中</string>
    <string name="notification_text">点击可查询 vortaro.cn</string>
</resources>
```

**values/themes.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.DictionaryApp" parent="Theme.Material3.DayNight">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>
</resources>
```

**values-night/themes.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.DictionaryApp" parent="Theme.Material3.DayNight">
        <item name="colorPrimary">@color/purple_700</item>
        <item name="colorPrimaryVariant">@color/black</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_700</item>
        <item name="colorSecondaryVariant">@color/teal_200</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>
</resources>
```

---

## 使用说明

1. 启动应用后，会自动创建紫色悬浮球
2. 在任意界面点击悬浮球，打开词典查询界面
3. 输入单词后点击"查询"，查看vortaro.cn词典结果
4. 支持拖拽移动悬浮球位置
5. 可在主界面点击"停止悬浮窗"关闭功能

---

## 注意事项

- 首次使用需要授予"显示在其他应用上层"权限
- 支持Android 7.0 (API 24)及以上系统
- Android 14+需要特殊前台服务权限（已配置）
