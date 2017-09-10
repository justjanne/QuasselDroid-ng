package de.kuschku.quasseldroid_ng.quassel

import de.kuschku.quasseldroid_ng.protocol.Protocol_Features

data class Protocol(val flags: Protocol_Features, val data: Short, val version: Byte)
