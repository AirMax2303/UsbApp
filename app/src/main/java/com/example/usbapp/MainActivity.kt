package com.example.usbapp

import android.content.Context
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun doFindInterface(usbDevice: UsbDevice, interfaceClass: Int): Int? {
        for (index in 0 until usbDevice.interfaceCount) {
            val usbInterface: UsbInterface = usbDevice.getInterface(index)
            if (usbInterface.interfaceClass == interfaceClass) {
                return index
            }
        }
        return null;
    }

    fun doFindEndpoint(usbInterface: UsbInterface, typeEndpoint: Int): Int? {
        for (index in 0 until usbInterface.endpointCount) {
            val usbEndpoint: UsbEndpoint = usbInterface.getEndpoint(index)
            if (usbEndpoint.type == typeEndpoint) {
                return index
            }
        }

        return null;
    }

    private val desiredUsbName = "";
    private val usbManager: UsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
    private val usbDevice: UsbDevice? = usbManager.deviceList.getValue(desiredUsbName)
    private val interfaceClass: Int = 0
    private val interfaceIndex: Int? = usbDevice?.let { doFindInterface(it, interfaceClass) }
    private val usbInterface: UsbInterface? = interfaceIndex?.let { usbDevice?.getInterface(it) }
    private val typeEndpoint: Int = 0
    private val endpointIndex = usbInterface?.let { doFindEndpoint(it, typeEndpoint) }
    private val usbEndpoint: UsbEndpoint? = endpointIndex?.let { usbInterface?.getEndpoint(it) }
    private val usbDeviceConnection: UsbDeviceConnection =
        usbManager.openDevice(usbManager.deviceList.getValue(desiredUsbName))


    private val target = Target(
        coordinates = "50.4513695760717, 30.524663230691157",
        type = "small enemy",
        name = "base",
    )
    var gson = Gson()
    var targetEntity = gson.toJson(target)
    private val buffer: ByteArray = targetEntity.toString().encodeToByteArray()
    private val hexCode = buffer.hashCode()

    // Отправка данных
    var res: Int? = usbEndpoint?.let { usbDeviceConnection.bulkTransfer(it, buffer, hexCode, 100) }


    // Получение цели из массива байт
    fun getDataFromUsb(buffer: ByteArray) {
        val targetString = buffer.decodeToString()
        val result = targetString.toKotlinObject<com.example.usbapp.Target>()
    }

    inline fun <reified T : Any> String.toKotlinObject(): T =
        Gson().fromJson(this, T::class.java)
}