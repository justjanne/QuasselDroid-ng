package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Protocol_Features

data class Protocol(val flags: Protocol_Features, val data: Short, val version: Byte)
