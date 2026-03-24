package com.bluetooth.music

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 蓝牙连接广播接收器
 * 监听蓝牙设备的连接和断开事件
 */
class BluetoothConnectionReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BluetoothConnectionReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

        when (action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                Log.d(TAG, "蓝牙设备已连接：${device?.name ?: device?.address}")
                // 通知服务检查是否为目标设备
                notifyService(context, "connected", device?.name)
            }
            
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                Log.d(TAG, "蓝牙设备已断开：${device?.name ?: device?.address}")
                notifyService(context, "disconnected", device?.name)
            }
        }
    }

    private fun notifyService(context: Context, event: String, deviceName: String?) {
        val prefs = context.getSharedPreferences("bluetooth_music_config", Context.MODE_PRIVATE)
        val targetDevice = prefs.getString("bluetooth_device", null)

        // 只有当连接的设备是目标设备时才触发
        if (deviceName == targetDevice && event == "connected") {
            Log.d(TAG, "目标设备已连接，触发音乐播放")
            
            // 启动服务（如果尚未运行）
            val serviceIntent = Intent(context, BluetoothMonitorService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
