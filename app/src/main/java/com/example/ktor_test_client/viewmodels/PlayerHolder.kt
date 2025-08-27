package com.example.ktor_test_client.viewmodels

import com.google.common.util.concurrent.Service
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class PlayerHolder : Service {
    override fun startAsync(): Service {
        TODO("Not yet implemented")
    }

    override fun isRunning(): Boolean {
        TODO("Not yet implemented")
    }

    override fun state(): Service.State {
        TODO("Not yet implemented")
    }

    override fun stopAsync(): Service {
        TODO("Not yet implemented")
    }

    override fun awaitRunning() {
        TODO("Not yet implemented")
    }

    override fun awaitRunning(timeout: Long, unit: TimeUnit) {
        TODO("Not yet implemented")
    }

    override fun awaitTerminated() {
        TODO("Not yet implemented")
    }

    override fun awaitTerminated(timeout: Long, unit: TimeUnit) {
        TODO("Not yet implemented")
    }

    override fun failureCause(): Throwable {
        TODO("Not yet implemented")
    }

    override fun addListener(listener: Service.Listener, executor: Executor) {
        TODO("Not yet implemented")
    }

}