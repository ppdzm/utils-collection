package io.github.ppdzm.utils.universal.formats.avro

import java.io.{EOFException, InputStream}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util

import com.fasterxml.jackson.core.JsonToken._
import com.fasterxml.jackson.core.{JsonFactory, JsonParser, JsonToken}
import com.fasterxml.jackson.databind.node.NullNode
import io.github.ppdzm.utils.universal.base.Logging
import org.apache.avro.io.ParsingDecoder
import org.apache.avro.io.parsing.{JsonGrammarGenerator, Parser, Symbol}
import org.apache.avro.util.Utf8
import org.apache.avro.{AvroTypeException, Schema}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by Stuart Alex on 2021/3/18.
 */
class ExtendedJsonDecoder(root: Symbol) extends ParsingDecoder(root) with Parser.ActionHandler with Logging {
    private val jsonFactory = new JsonFactory
    private val reorderBuffers = new util.Stack[ReorderBuffer]()
    private val NULL_JSON_ELEMENT = JsonElement(null)
    private var currentReorderBuffer: ReorderBuffer = _
    private var in: JsonParser = _

    def this(json: String, schema: Schema) = {
        this(new JsonGrammarGenerator().generate(schema))
        configure(json)
    }

    def configure(json: String): Unit = {
        if (null == json) {
            throw new NullPointerException("String to read from cannot be null!")
        }
        parser.reset()
        this.in = jsonFactory.createJsonParser(json)
        this.in.nextToken
    }

    override def arrayNext(): Long = {
        advance(Symbol.ITEM_END)
        doArrayNext()
    }

    def configure(inputStream: InputStream): Unit = {
        if (null == inputStream) {
            throw new NullPointerException("InputStream to read from cannot be null!")
        }
        parser.reset()
        this.in = jsonFactory.createJsonParser(inputStream)
        this.in.nextToken
    }

    override def doAction(input: Symbol, top: Symbol): Symbol = {
        top match {
            case fa: Symbol.FieldAdjustAction =>
                val name = fa.fname
                if (currentReorderBuffer != null) {
                    val node = currentReorderBuffer.savedFields.get(name)
                    if (node != null) {
                        currentReorderBuffer.savedFields.remove(name)
                        currentReorderBuffer.origParser = in
                        in = new ExtendedJsonParser(node)
                        return null
                    }
                }
                if (in.getCurrentToken eq JsonToken.FIELD_NAME) {
                    do {
                        val fn = in.getText
                        in.nextToken
                        if (name == fn) {
                            return null
                        } else {
                            if (currentReorderBuffer == null) {
                                currentReorderBuffer = new ReorderBuffer
                            }
                            currentReorderBuffer.savedFields.put(fn, getValueAsTree(in))
                        }
                    } while (in.getCurrentToken == JsonToken.FIELD_NAME)
                    //throw new AvroTypeException(s"Expected field name not found: ${fa.fname}");
                    logWarning(s"Expected field name not found: ${fa.fname}, try inject default value if available")
                    injectDefaultValueIfAvailable(in, fa.fname)
                } else {
                    injectDefaultValueIfAvailable(in, fa.fname)
                }
            case Symbol.FIELD_END =>
                if (currentReorderBuffer != null && currentReorderBuffer.origParser != null) {
                    in = currentReorderBuffer.origParser
                    currentReorderBuffer.origParser = null
                }
            case Symbol.RECORD_START =>
                if (in.getCurrentToken eq JsonToken.START_OBJECT) {
                    in.nextToken
                    reorderBuffers.push(currentReorderBuffer)
                    currentReorderBuffer = null
                } else {
                    throw error("record-start")
                }
            case Symbol.RECORD_END | Symbol.UNION_END =>
                if (in.getCurrentToken eq JsonToken.END_OBJECT) {
                    in.nextToken
                    if (top eq Symbol.RECORD_END) {
                        if (currentReorderBuffer != null && !currentReorderBuffer.savedFields.isEmpty) {
                            throw error(s"Unknown fields: ${currentReorderBuffer.savedFields.keySet}")
                        }
                        currentReorderBuffer = reorderBuffers.pop
                    }
                } else {
                    throw error(if (top eq Symbol.RECORD_END) "record-end" else "union-end")
                }
            case _ => throw new AvroTypeException(s"Unknown action symbol $top")
        }
        null
    }

    private def getValueAsTree(in: JsonParser): List[JsonElement] = {
        var level = 0
        val result = mutable.MutableList[JsonElement]()
        do {
            val t = in.getCurrentToken
            t match {
                case START_OBJECT | START_ARRAY =>
                    level += 1
                    result += JsonElement(t)
                case END_OBJECT | END_ARRAY =>
                    level -= 1
                    result += JsonElement(t)
                case FIELD_NAME | VALUE_STRING | VALUE_NUMBER_INT | VALUE_NUMBER_FLOAT | VALUE_TRUE | VALUE_FALSE | VALUE_NULL => result += JsonElement(t, in.getText)
                case _ =>
            }
            in.nextToken
        } while (level != 0)
        result += NULL_JSON_ELEMENT
        result.toList
    }

    private def injectDefaultValueIfAvailable(in: JsonParser, fieldName: String): Unit = {
        //        Field field = findField(schema.getElementType(), fieldName);
        //
        //        if (field == null) {
        //throw new AvroTypeException(s"Expected field name not found: $fieldName");
        //        }
        //
        //        JsonNode defVal = field.defaultValue();
        //        if (defVal == null) {
        //throw new AvroTypeException(s"Expected field name not found: $fieldName");
        //        }
        val defVal = NullNode.instance
        val result = new util.ArrayList[JsonElement](2)
        val traverse = defVal.traverse
        var nextToken = traverse.nextToken
        while (nextToken != null) {
            if (nextToken.isScalarValue) {
                result.add(JsonElement(nextToken, traverse.getText))
            } else {
                result.add(JsonElement(nextToken))
            }
            nextToken = traverse.nextToken
        }
        result.add(NULL_JSON_ELEMENT)
        if (currentReorderBuffer == null) {
            currentReorderBuffer = new ReorderBuffer
        }
        currentReorderBuffer.origParser = in
        this.in = new ExtendedJsonParser(result.toList)
    }

    override def mapNext(): Long = {
        advance(Symbol.ITEM_END)
        doMapNext()
    }

    override def readArrayStart(): Long = {
        advance(Symbol.ARRAY_START)
        if (in.getCurrentToken eq JsonToken.START_ARRAY) {
            in.nextToken
            doArrayNext()
        } else {
            throw error("array-start")
        }
    }

    private def doArrayNext(): Int = {
        if (in.getCurrentToken == JsonToken.END_ARRAY) {
            parser.advance(Symbol.ARRAY_END)
            in.nextToken
            0
        }
        else 1
    }

    override def readBoolean(): Boolean = {
        advance(Symbol.BOOLEAN)
        val t = in.getCurrentToken
        if ((t eq JsonToken.VALUE_TRUE) || (t eq JsonToken.VALUE_FALSE)) {
            in.nextToken
            t eq JsonToken.VALUE_TRUE
        } else {
            throw error("boolean")
        }
    }

    override def readBytes(old: ByteBuffer): ByteBuffer = {
        advance(Symbol.BYTES)
        if (in.getCurrentToken eq JsonToken.VALUE_STRING) {
            val result = readByteArray
            in.nextToken
            ByteBuffer.wrap(result)
        } else {
            throw error("bytes")
        }
    }

    override def readDouble(): Double = {
        advance(Symbol.DOUBLE)
        if (in.getCurrentToken eq JsonToken.VALUE_NUMBER_FLOAT) {
            val result = in.getDoubleValue
            in.nextToken
            result
        } else {
            throw error("double")
        }
    }

    override def readEnum(): Int = {
        advance(Symbol.ENUM)
        val top = parser.popSymbol.asInstanceOf[Symbol.EnumLabelsAction]
        if (in.getCurrentToken eq JsonToken.VALUE_STRING) {
            in.getText
            val n = top.findLabel(in.getText)
            if (n >= 0) {
                in.nextToken
                return n
            }
            throw new AvroTypeException(s"Unknown symbol in enum ${in.getText}")
        } else {
            throw error("fixed")
        }
    }

    override def readFixed(bytes: Array[Byte], start: Int, length: Int): Unit = {
        checkFixed(length)
        if (in.getCurrentToken eq JsonToken.VALUE_STRING) {
            val result = readByteArray
            in.nextToken
            if (result.length != length) {
                throw new AvroTypeException(s"Expected fixed length $length, but got${result.length}")
            }
            System.arraycopy(result, 0, bytes, start, length)
        } else {
            throw error("fixed")
        }
    }

    override def readFloat(): Float = {
        advance(Symbol.FLOAT)
        if (in.getCurrentToken eq JsonToken.VALUE_NUMBER_FLOAT) {
            val result = in.getFloatValue
            in.nextToken
            result
        } else {
            throw error("float")
        }
    }

    override def readIndex(): Int = {
        advance(Symbol.UNION)
        val a = parser.popSymbol.asInstanceOf[Symbol.Alternative]
        var label: String = null
        val token = in.getCurrentToken
        if (token eq VALUE_NULL) {
            label = "null"
        } else if ((token eq START_OBJECT) && (in.nextToken eq FIELD_NAME)) {
            label = in.getText
            in.nextToken
            parser.pushSymbol(Symbol.UNION_END)
            //        } else if (a.size == 2) {
            //            if ("null" == a.getLabel(0) || "null" == a.getLabel(1)) {
            //                label = if ("null" == a.getLabel(0)) {
            //                    a.getLabel(1)
            //                } else {
            //                    a.getLabel(0)
            //                }
            //            }
        } else if (a.size > 1) {
            label = token match {
                case VALUE_NULL => "null"
                case VALUE_NUMBER_INT => "int"
                case VALUE_NUMBER_FLOAT => "float"
                case VALUE_FALSE | VALUE_TRUE => "boolean"
                case VALUE_STRING => "string"
                case _ => a.labels.filter(_ != "null").head
            }
        } else {
            throw error("start-union")
        }
        var n = a.findLabel(label)
        if (n < 0) {
            n = a.labels.zipWithIndex.filter(_._1 != "null").head._2
            //n = 0
            //throw new AvroTypeException(s"Unknown union branch $label, with token $token, labels ${a.labels.mkString(",")}")
        }
        parser.pushSymbol(a.getSymbol(n))
        n
    }

    override def readInt(): Int = {
        advance(Symbol.INT)
        if (in.getCurrentToken eq VALUE_NUMBER_INT) {
            val result = in.getIntValue
            in.nextToken
            result
        } else {
            throw error("int")
        }
    }

    override def readLong(): Long = {
        advance(Symbol.LONG)
        if (in.getCurrentToken eq VALUE_NUMBER_INT) {
            val result = in.getLongValue
            in.nextToken
            result
        } else {
            throw error("long")
        }
    }

    override def readMapStart(): Long = {
        advance(Symbol.MAP_START)
        if (in.getCurrentToken eq START_OBJECT) {
            in.nextToken
            doMapNext()
        } else {
            throw error("map-start")
        }
    }

    private def doMapNext(): Int = {
        if (in.getCurrentToken eq JsonToken.END_OBJECT) {
            in.nextToken
            advance(Symbol.MAP_END)
            0
        }
        else {
            1
        }
    }

    override def readNull(): Unit = {
        advance(Symbol.NULL)
        if (in.getCurrentToken eq VALUE_NULL) {
            in.nextToken
        } else {
            throw error("null")
        }
    }

    override def readString(old: Utf8): Utf8 = new Utf8(readString())

    override def readString(): String = {
        advance(Symbol.STRING)
        val symbol = parser.topSymbol
        if (symbol eq Symbol.MAP_KEY_MARKER) {
            parser.advance(Symbol.MAP_KEY_MARKER)
            if (in.getCurrentToken ne FIELD_NAME) {
                throw error("map-key")
            }
        } else if (in.getCurrentToken ne VALUE_STRING) {
            throw error("string")
        }
        val result = in.getText
        in.nextToken
        result
    }

    override def skipArray(): Long = {
        advance(Symbol.ARRAY_START)
        if (in.getCurrentToken eq START_ARRAY) {
            in.skipChildren
            in.nextToken
            advance(Symbol.ARRAY_END)
        } else {
            throw error("array-start")
        }
        0
    }

    private def advance(symbol: Symbol): Symbol = {
        this.parser.processTrailingImplicitActions()
        if (in.getCurrentToken == null && this.parser.depth == 1) {
            throw new EOFException
        }
        parser.advance(symbol)
    }

    private def error(`type`: String) = new AvroTypeException(s"Expected ${`type`}. Got ${in.getCurrentToken}")

    override def skipBytes(): Unit = {
        advance(Symbol.BYTES)
        if (in.getCurrentToken eq VALUE_STRING) {
            in.nextToken
        } else {
            throw error("bytes")
        }
    }

    override def skipFixed(): Unit = {
        advance(Symbol.FIXED)
        val top = parser.popSymbol.asInstanceOf[Symbol.IntCheckAction]
        doSkipFixed(top.size)
    }

    override def skipFixed(length: Int): Unit = {
        checkFixed(length)
        doSkipFixed(length)
    }

    private def checkFixed(size: Int): Unit = {
        advance(Symbol.FIXED)
        val top = parser.popSymbol.asInstanceOf[Symbol.IntCheckAction]
        if (size != top.size) {
            throw new AvroTypeException(s"Incorrect length for fixed binary: expected ${top.size} but received $size bytes.")
        }
    }

    private def doSkipFixed(length: Int): Unit = {
        if (in.getCurrentToken eq JsonToken.VALUE_STRING) {
            val result = readByteArray
            in.nextToken
            if (result.length != length) {
                throw new AvroTypeException(s"Expected fixed length $length, but got${result.length}")
            }
        } else {
            throw error("fixed")
        }
    }

    private def readByteArray: Array[Byte] = in.getText.getBytes(StandardCharsets.ISO_8859_1)

    override def skipMap(): Long = {
        advance(Symbol.MAP_START)
        if (in.getCurrentToken eq START_OBJECT) {
            in.skipChildren
            in.nextToken
            advance(Symbol.MAP_END)
        } else {
            throw error("map-start")
        }
        0
    }

    override def skipString(): Unit = {
        advance(Symbol.STRING)
        if (parser.topSymbol eq Symbol.MAP_KEY_MARKER) {
            parser.advance(Symbol.MAP_KEY_MARKER)
            if (in.getCurrentToken ne FIELD_NAME) {
                throw error("map-key")
            }
        } else if (in.getCurrentToken ne VALUE_STRING) {
            throw error("string")
        }
        in.nextToken
    }

    private class ReorderBuffer {
        var savedFields = new util.HashMap[String, List[JsonElement]]
        var origParser: JsonParser = _
    }
}
