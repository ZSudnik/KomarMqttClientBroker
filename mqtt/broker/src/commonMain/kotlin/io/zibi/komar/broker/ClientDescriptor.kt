package io.zibi.komar.broker

import java.util.Objects

class ClientDescriptor internal constructor(
    val clientID: String,
    val addressStr: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ClientDescriptor
        return  clientID == that.clientID && addressStr == that.addressStr
//        return port == that.port && clientID == that.clientID && address == that.address
    }

    override fun hashCode(): Int {
        return Objects.hash(clientID, addressStr)
    }

    override fun toString(): String {
        return "ClientDescriptor{" +
                "clientID='" + clientID + '\'' +
                ", address='" + addressStr + '\'' +
//                ", port=" + port +
                '}'
    }
}
