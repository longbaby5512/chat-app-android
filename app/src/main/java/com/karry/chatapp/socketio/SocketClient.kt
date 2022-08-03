package com.karry.chatapp.socketio

import io.socket.client.IO
import java.lang.reflect.Proxy

class SocketClient internal constructor(private val socket: SocketExecutor) {

    fun <T: Any> create(service: Class<T>): T {
        val proxyInstance = Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service)
        ) { _, method, arguments ->
            socket.execute(method, arguments ?: arrayOf())
        }
        return service.cast(proxyInstance) ?: throw TypeCastException()
    }

    class Builder {
        private var baseUrl: String = ""
        private var options: IO.Options = IO.Options()

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun options(options: IO.Options): Builder {
            this.options = options
            return this
        }

        fun build(): SocketClient {
            return SocketClient(SocketExecutor(IO.socket(baseUrl, options)))
        }
    }
}