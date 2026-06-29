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
