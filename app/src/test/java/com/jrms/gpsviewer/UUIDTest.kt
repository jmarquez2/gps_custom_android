package com.jrms.gpsviewer

import org.junit.Assert.assertTrue
import com.jrms.gpsviewer.utils.bytesToUUID
import com.jrms.gpsviewer.utils.uUIDToBytes
import org.junit.Test
import java.util.UUID

class UUIDTest {

    @Test
    fun testUUID(){
        val uuid = UUID.fromString("4fb437d5-c94f-46bc-bd2c-ca8f04092846")

        val bytes = uUIDToBytes(uuid)
        val generatedUUID = bytesToUUID(bytes)

        assertTrue(uuid.equals(generatedUUID))

    }
}