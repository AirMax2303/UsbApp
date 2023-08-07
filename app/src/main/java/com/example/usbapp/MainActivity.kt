package com.example.usbapp

import android.content.Context
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    val target = mapOf<String, String>(
        "coordinates" to "50.4513695760717, 30.524663230691157",
        "type" to "small enemy",
        "name" to "H",
    )
    private val buffer: ByteArray = JSONObject(target).toString().encodeToByteArray()
    private val hexCode = buffer.hashCode()
    var res: Int? =
        usbEndpoint?.let { usbDeviceConnection.bulkTransfer(it, buffer, hexCode, 100) }


}