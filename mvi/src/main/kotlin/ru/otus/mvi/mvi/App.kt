package ru.otus.mvi.mvi

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import ru.otus.mvi.common.session.SessionManager

class App : Application() {
    /**
     * Global session manager
     */
    val sessionManager: SessionManager = SessionManager.Instance

    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
    }
}

/**
 * Global session manager
 */
val Application.sessionManager get() = (this as App).sessionManager