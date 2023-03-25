package core

import java.util.UUID

data class Config(
    val cookie: String = UUID.randomUUID().toString(),
    val port: String = "sys.agent",
)
