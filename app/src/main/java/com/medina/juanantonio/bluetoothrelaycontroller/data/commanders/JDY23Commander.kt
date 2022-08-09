package com.medina.juanantonio.bluetoothrelaycontroller.data.commanders

object JDY23Commander {

    enum class Command(val value: ByteArray) {
        ON_1(value = byteArrayOf((0xA0).toByte(), 0x01, 0x01, (0xA2).toByte())),
        OFF_1(value = byteArrayOf((0xA0).toByte(), 0x01, 0x00, (0xA1).toByte())),
        ON_2(value = byteArrayOf((0xA0).toByte(), 0x02, 0x01, (0xA3).toByte())),
        OFF_2(value = byteArrayOf((0xA0).toByte(), 0x02, 0x00, (0xA2).toByte())),
        ON_3(value = byteArrayOf((0xA0).toByte(), 0x03, 0x01, (0xA4).toByte())),
        OFF_3(value = byteArrayOf((0xA0).toByte(), 0x03, 0x00, (0xA3).toByte())),
        ON_4(value = byteArrayOf((0xA0).toByte(), 0x04, 0x01, (0xA5).toByte())),
        OFF_4(value = byteArrayOf((0xA0).toByte(), 0x04, 0x00, (0xA4).toByte())),
        ON_5(value = byteArrayOf((0xA0).toByte(), 0x05, 0x01, (0xA6).toByte())),
        OFF_5(value = byteArrayOf((0xA0).toByte(), 0x05, 0x00, (0xA5).toByte())),
        ON_6(value = byteArrayOf((0xA0).toByte(), 0x06, 0x01, (0xA7).toByte())),
        OFF_6(value = byteArrayOf((0xA0).toByte(), 0x06, 0x00, (0xA6).toByte())),
        ON_7(value = byteArrayOf((0xA0).toByte(), 0x07, 0x01, (0xA8).toByte())),
        OFF_7(value = byteArrayOf((0xA0).toByte(), 0x07, 0x00, (0xA7).toByte())),
        ON_8(value = byteArrayOf((0xA0).toByte(), 0x08, 0x01, (0xA9).toByte())),
        OFF_8(value = byteArrayOf((0xA0).toByte(), 0x08, 0x00, (0xA8).toByte())),
    }
}