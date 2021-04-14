package io.github.ppdzm.utils.universal.encryption

object RC4 {

    def decrypt(input: Array[Byte], key: String): Array[Byte] = base(input, key)

    def encrypt(input: Array[Byte], key: String): Array[Byte] = base(input, key)

    private def base(input: Array[Byte], key: String): Array[Byte] = {
        var x = 0
        var y = 0
        val keyBytes = initKey(key)
        var xorIndex = 0
        val result = new Array[Byte](input.length)
        input.indices.foreach(i => {
            x = (x + 1) & 0xff
            y = ((keyBytes(x) & 0xff) + y) & 0xff
            val tmp = keyBytes(x)
            keyBytes(x) = keyBytes(y)
            keyBytes(y) = tmp
            xorIndex = ((keyBytes(x) & 0xff) + (keyBytes(y) & 0xff)) & 0xff
            result(i) = (input(i) ^ keyBytes(xorIndex)).toByte
        })
        result
    }

    private def initKey(key: String): Array[Byte] = {
        val keyBytes = key.getBytes()
        val state = new Array[Byte](256)
        state.indices.foreach(i => state(i) = i.toByte)
        var index1 = 0
        var index2 = 0
        if (keyBytes == null || keyBytes.isEmpty) {
            return null
        }
        for (i <- 0 until 256) {
            index2 = ((keyBytes(index1) & 0xff) + (state(i) & 0xff) + index2) & 0xff
            val tmp = state(i)
            state(i) = state(index2)
            state(index2) = tmp
            index1 = (index1 + 1) % keyBytes.length
        }
        state
    }

}