package com.sangtq.musicappkmp.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.inMemoryDriver

actual fun inMemorySqlDriver(): SqlDriver = inMemoryDriver(MusicDatabase.Schema)
