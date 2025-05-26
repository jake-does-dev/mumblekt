package dev.jakedoes.logging

import io.github.oshai.kotlinlogging.KLogger

enum class LogLevel {
    Debug,
    Info,
    Warn,
    Error
}

fun KLogger.log(logLevel: LogLevel, message: String) {
    when (logLevel) {
        LogLevel.Debug -> debug { message }
        LogLevel.Info -> info { message }
        LogLevel.Warn -> warn { message }
        LogLevel.Error -> error { message }
    }
}