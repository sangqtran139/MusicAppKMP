package com.sangtq.musicappkmp.core.database

import app.cash.sqldelight.db.SqlDriver

/** Driver SQLite in-memory cho test (schema được tạo sẵn). Actual theo nền tảng host. */
expect fun inMemorySqlDriver(): SqlDriver
