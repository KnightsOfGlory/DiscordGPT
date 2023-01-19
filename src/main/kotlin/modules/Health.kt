package modules

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import utilities.Logger
import java.net.InetSocketAddress

object Health {

    fun start() {
        val server = HttpServer.create(InetSocketAddress(8080), 0)
        server.createContext("/", HealthHandler())
        server.executor = null
        server.start()
    }

    class HealthHandler : HttpHandler {
        override fun handle(http: HttpExchange) {
            Logger.info ("Received health check, responding now...")
            val body = "OK"
            val stream = http.responseBody

            http.sendResponseHeaders(200, body.length.toLong())
            stream.write(body.toByteArray())
            stream.close()
        }
    }
}
