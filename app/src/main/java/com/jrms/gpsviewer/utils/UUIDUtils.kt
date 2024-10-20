package com.jrms.gpsviewer.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

fun bytesToUUID(bytes : ByteArray) : UUID{
    val byteBuffer = ByteBuffer.wrap(bytes)

    val fistLong =  byteBuffer.getLong()
    val secondLong = byteBuffer.getLong()

    return if(ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)){
        UUID(fistLong, secondLong)

    }else{
        UUID(secondLong, fistLong)
    }
}

fun uUIDToBytes(uuid: UUID) : ByteArray{
    val byteBuffer = ByteBuffer.allocate(16)

    if(ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)){
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
    }else{
        byteBuffer.putLong(uuid.leastSignificantBits)
        byteBuffer.putLong(uuid.mostSignificantBits)
    }

    return  byteBuffer.array()
}

fun uUIDToBytes(uuid : String) : ByteArray{
    return uUIDToBytes(UUID.fromString(uuid))
}