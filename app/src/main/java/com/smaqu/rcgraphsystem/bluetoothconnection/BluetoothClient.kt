package com.smaqu.rcgraphsystem.bluetoothconnection

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.util.*

class BluetoothClient {
    private class ClientHandler(looper: Looper, client: BluetoothClient) : Handler(looper) {
        private val weakReference: WeakReference<BluetoothClient> = WeakReference(client)

        override fun handleMessage(msg: Message) {
            val client = weakReference.get()
            when (msg.what) {
                BluetoothApi.MESSAGE_FILE -> client?.listener?.messageFile(client, msg.obj as String)
                BluetoothApi.MESSAGE_LIST -> client?.listener?.messageList(client, msg.obj as String)
                BluetoothApi.MESSAGE_WRITE -> client?.listener?.messageSend(client, msg.obj as String)
                BluetoothApi.MESSAGE_SHORT_READ -> client?.listener?.messageShortReceived(client, msg.obj as String)
                BluetoothApi.MESSAGE_FULL_READ -> client?.listener?.messageFullReceived(client, msg.obj as String)
                BluetoothApi.MESSAGE_INFO -> client?.listener?.messageInfo(client, msg.obj as String)
                BluetoothApi.MESSAGE_CLIENT_CONNECTED -> client?.listener?.stateConnected(msg.obj as BluetoothDevice)
                BluetoothApi.MESSAGE_CLIENT_CONNECTING -> client?.listener?.stateConnecting()
                BluetoothApi.MESSAGE_CLIENT_DISCONNECTED -> client?.listener?.stateDisconnected(msg.obj as BluetoothDevice)
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothClient"
        const val STATE_DISCONNECTED: Byte = 0
        const val STATE_CONNECTING: Byte = 1
        const val STATE_CONNECTED: Byte = 2
    }

    private val device: BluetoothDevice
    private val handler: Handler
    private val listener: ClientListener

    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    private var state: Byte = 0

    constructor (device: BluetoothDevice, listener: ClientListener) {
        this.device = device
        this.handler = ClientHandler(Looper.getMainLooper(), this)
        this.listener = listener
        setState(STATE_DISCONNECTED)
    }

    @Synchronized
    fun connect(pinConnection: Boolean) {
        setState(STATE_CONNECTING)
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }

        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }

        connectThread = ConnectThread(pinConnection)
        connectThread!!.start()
    }

    @Synchronized
    fun stop() {
        setState(STATE_DISCONNECTED)
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }

        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }
    }

    @Synchronized
    fun send(message: String): Boolean {
        return message.isNotEmpty() && state == STATE_CONNECTED && connectedThread!!.write(message)
    }

    @Synchronized
    fun getDevice(): BluetoothDevice = device

    @Synchronized
    fun getState(): Byte = state

    @Synchronized
    private fun connected(socket: BluetoothSocket) {
        setState(STATE_CONNECTED)
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }

        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }

        connectedThread = ConnectedThread(socket)
        connectedThread!!.start()
    }

    @Synchronized
    private fun setState(state: Byte) {
        if (this.state == state)
            return
        this.state = state
        when (state) {
            STATE_CONNECTED -> {
                handler.obtainMessage(BluetoothApi.MESSAGE_CLIENT_CONNECTED, device).sendToTarget()
            }
            STATE_CONNECTING -> {
                handler.obtainMessage(BluetoothApi.MESSAGE_CLIENT_CONNECTING).sendToTarget()
            }
            STATE_DISCONNECTED -> {
                handler.obtainMessage(BluetoothApi.MESSAGE_CLIENT_DISCONNECTED, device).sendToTarget()
            }
        }
    }

    private fun connectFailed() {
        Log.d(TAG, "Connection failed while creating socket")
        stop()
        handler.obtainMessage(BluetoothApi.MESSAGE_INFO, "Connect failed while creating socket").sendToTarget()
    }

    private fun connectionLost() {
        Log.d(TAG, "Connection lost.")
        handler.obtainMessage(BluetoothApi.MESSAGE_INFO, "Connection lost").sendToTarget()
        setState(STATE_DISCONNECTED)
    }

    private fun getLogString(msg: String, device: BluetoothDevice): String {
        return (msg + "\n"
                + " name: " + device.name
                + " address: " + device.address)
    }

    private inner class ConnectThread(pinConnection: Boolean) : Thread() {
        @Volatile
        private var connecting: Boolean = false
        private var socket: BluetoothSocket?

        init {
            Log.d(TAG, getLogString("Connect thread created.", device))

            var tmp: BluetoothSocket? = null
            val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

            try {
                Log.d(TAG, getLogString("Trying to connect.", device))
                tmp = if (pinConnection) {
                    device.createRfcommSocketToServiceRecord(uuid)
                } else {
                    device.createInsecureRfcommSocketToServiceRecord(uuid)
                }

            } catch (e: IOException) {
                Log.e(TAG, getLogString("Create socket failed.", device), e)
            }
            socket = tmp
        }

        override fun run() {
            Log.d(TAG, getLogString("BEGIN connect thread.", device))
            name = device.name + device.address
            connecting = true
            var connectionAttempt = 2

            while (connecting) {
                try {
                    socket!!.connect()
                } catch (e: IOException) {
                    Log.e(TAG, getLogString("Connect socket failed.", device), e)
                    if (connectionAttempt == 0) {
                        connectFailed()
                    }
                    try {
                        Thread.sleep(2000)
                    } catch (e2: InterruptedException) {
                        handler.obtainMessage(BluetoothApi.MESSAGE_INFO, "Reconnect bluetooth!").sendToTarget()
                        Log.e(TAG, getLogString("Connect thread interrupted while sleeping.",
                                device), e2)
                    }
                    connectionAttempt--
                    continue
                }

                synchronized(this@BluetoothClient) {
                    connectThread = null
                }

                connected(socket!!)
                break
            }
        }

        fun cancel() {
            connecting = false
            try {
                socket!!.close()
            } catch (e: IOException) {
                Log.e(TAG, getLogString("Close socket failed.", device), e)
            }
        }
    }

    private inner class ConnectedThread(private val socket: BluetoothSocket) : Thread() {
        @Volatile
        private var connected: Boolean = false
        private val inputStream: InputStream?
        private val outputStream: OutputStream?

        init {
            Log.d(TAG, getLogString("Connected thread created.", device))

            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                Log.e(TAG, getLogString("Get stream failed.", device), e)
            }

            inputStream = tmpIn
            outputStream = tmpOut
        }

        override fun run() {
            Log.d(TAG, getLogString("BEGIN connected thread.", device))
            name = device.name + device.address
            connected = true

            val DEFAULT_DOWNLOAD = 0
            val LIST_DOWNLOAD = 1
            val FILE_DOWNLOAD = 2

            var transferType = DEFAULT_DOWNLOAD
            val buffer = ByteArray(4096)
            var bytes: Int

            while (connected) {
                try {
                    bytes = inputStream!!.read(buffer)
                    val message = String(buffer, 0, bytes)

                    if (message.startsWith("LIST: ")) {
                        transferType = LIST_DOWNLOAD
                        handler.obtainMessage(BluetoothApi.MESSAGE_SHORT_READ, "File list[...]\n").sendToTarget()
                    }
                    if (message.startsWith("DATA: ")) {
                        transferType = FILE_DOWNLOAD
                        handler.obtainMessage(BluetoothApi.MESSAGE_SHORT_READ, "Data values[...]\n").sendToTarget()
                    }

                    when (transferType) {
                        DEFAULT_DOWNLOAD -> {
                            handler.obtainMessage(BluetoothApi.MESSAGE_SHORT_READ, message).sendToTarget()
                        }
                        LIST_DOWNLOAD -> {
                            handler.obtainMessage(BluetoothApi.MESSAGE_LIST, message).sendToTarget()
                        }
                        FILE_DOWNLOAD -> {
                            handler.obtainMessage(BluetoothApi.MESSAGE_FILE, message).sendToTarget()
                        }
                    }

                    if (message.endsWith("*")) {
                        transferType = DEFAULT_DOWNLOAD
                    }

                    handler.obtainMessage(BluetoothApi.MESSAGE_FULL_READ, message).sendToTarget()

                    // For debug purposes.
                    Log.d(TAG, getLogString("Received message: $message", device))

                } catch (e: IOException) {
                    Log.e(TAG, getLogString("Disconnected.", device), e)
                    connectionLost()
                    break
                }
            }
        }

        fun write(message: String): Boolean {
            return try {
                val buffer = message.toByteArray(Charsets.UTF_8)
                outputStream!!.write(buffer)

                // Share the sent buffer back to UI
                handler.obtainMessage(BluetoothApi.MESSAGE_WRITE, message).sendToTarget()
                true
            } catch (e: IOException) {
                Log.e(TAG, getLogString("Write failed.", device), e)
                false
            }
        }

        fun cancel() {
            shutdownConnection()
        }

        private fun shutdownConnection() {
            Log.d(TAG, getLogString("Shutdown connection", device))
            connected = false
            try {
                inputStream!!.close()
            } catch (e: IOException) {
                Log.e(TAG, getLogString("Close input stream failed.", device), e)
            }

            try {
                outputStream!!.close()
            } catch (e: IOException) {
                Log.e(TAG, getLogString("Close output stream failed.", device), e)
            }

            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, getLogString("Close connected socket", device))
            }
        }
    }
}