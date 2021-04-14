package io.github.ppdzm.utils.universal.formats.avro

import java.math.BigInteger

import com.fasterxml.jackson.core.JsonParser.NumberType
import com.fasterxml.jackson.core.JsonToken._
import com.fasterxml.jackson.core._

/**
 * Created by Stuart Alex on 2021/3/18.
 */
class ExtendedJsonParser(elements: List[JsonElement]) extends JsonParser {
    private var pos = 0

    override def close(): Unit = throw new UnsupportedOperationException()

    override def getBigIntegerValue: BigInteger = throw new UnsupportedOperationException()

    override def getBinaryValue(base64Variant: Base64Variant): Array[Byte] = throw new UnsupportedOperationException()

    override def getCodec: ObjectCodec = throw new UnsupportedOperationException()

    override def getCurrentLocation: JsonLocation = throw new UnsupportedOperationException()

    override def getCurrentName: String = throw new UnsupportedOperationException()

    override def getCurrentToken: JsonToken = elements(pos).token

    override def getDecimalValue: java.math.BigDecimal = throw new UnsupportedOperationException()

    override def getDoubleValue: Double = getText.toDouble

    override def getFloatValue: Float = getText.toFloat

    override def getNumberType: NumberType = throw new UnsupportedOperationException()

    override def getNumberValue: Number = throw new UnsupportedOperationException()

    override def getIntValue: Int = getText.toInt

    override def getLongValue: Long = getText.toLong

    override def getParsingContext: JsonStreamContext = throw new UnsupportedOperationException()

    override def getText: String = elements(pos).value

    override def getTextCharacters: Array[Char] = throw new UnsupportedOperationException()

    override def getTextLength: Int = throw new UnsupportedOperationException()

    override def getTextOffset: Int = throw new UnsupportedOperationException()

    override def getTokenLocation: JsonLocation = throw new UnsupportedOperationException()

    override def isClosed: Boolean = throw new UnsupportedOperationException()

    override def nextToken(): JsonToken = {
        pos += 1
        elements(pos).token
    }

    override def setCodec(objectCodec: ObjectCodec): Unit = throw new UnsupportedOperationException()

    override def skipChildren(): JsonParser = {
        var level = 0
        pos += 1
        do {
            elements(pos - 1).token match {
                case START_ARRAY =>
                case START_OBJECT => level += 1
                case END_ARRAY =>
                case END_OBJECT => level -= 1
                case _ =>
            }
            pos += 1
        } while (level > 0)
        this
    }

    override def version(): Version = throw new UnsupportedOperationException()

    override def nextValue(): JsonToken = throw new UnsupportedOperationException()

    override def getCurrentTokenId: Int = throw new UnsupportedOperationException()

    override def hasCurrentToken: Boolean = throw new UnsupportedOperationException()

    override def hasTokenId(id: Int): Boolean = throw new UnsupportedOperationException()

    override def hasToken(t: JsonToken): Boolean = throw new UnsupportedOperationException()

    override def clearCurrentToken(): Unit = throw new UnsupportedOperationException()

    override def getLastClearedToken: JsonToken = throw new UnsupportedOperationException()

    override def overrideCurrentName(name: String): Unit = throw new UnsupportedOperationException()

    override def hasTextCharacters: Boolean = throw new UnsupportedOperationException()

    override def getValueAsString(`def`: String): String = throw new UnsupportedOperationException()
}
