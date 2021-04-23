package io.github.ppdzm.utils.universal.formats.avro

import java.io.{ByteArrayOutputStream, EOFException}

import com.sksamuel.avro4s.json.JsonToAvroConverter
import io.github.ppdzm.utils.universal.base.ResourceUtils
import io.github.ppdzm.utils.universal.feature.LoanPattern
import io.github.ppdzm.utils.universal.implicits.BasicConversions._
import org.apache.avro.Schema
import org.apache.avro.file.{DataFileReader, DataFileWriter, SeekableByteArrayInput}
import org.apache.avro.generic.{GenericData, GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.{BinaryEncoder, DecoderFactory, EncoderFactory}
import org.apache.avro.specific.SpecificDatumReader

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by Stuart Alex on 2021/1/27.
 */
object AvroUtils {
    private lazy val parser = new Schema.Parser()

    /**
     * Avro字节数组转回Avro实体（类型未知，字节数组中含有Schema信息）
     *
     * @param bytes Avro格式字节数组
     * @return
     */
    def bytes2Object(bytes: Array[Byte]): List[Object] = {
        bytes2Generic[Object](bytes)
    }

    /**
     * Avro字节数组转回Avro实体（GenericRecord/GenericData.Array）
     *
     * @param bytes Avro格式字节数组
     * @tparam T 实体类型
     * @return
     */
    def bytes2Generic[T](bytes: Array[Byte]): List[T] = {
        val seekableByteArrayInput = new SeekableByteArrayInput(bytes)
        val datumReader = new SpecificDatumReader[T]()
        LoanPattern.using(new DataFileReader[T](seekableByteArrayInput, datumReader)) {
            dataFileReader =>
                val listBuffer = ListBuffer[T]()
                while (dataFileReader.hasNext) {
                    val datum = dataFileReader.next()
                    listBuffer += datum
                }
                listBuffer.toList
        }
    }

    /**
     * Avro字节数组转回Avro实体（类型未知，字节数组中不含Schema信息）
     *
     * @param bytes        Avro格式字节数组
     * @param schemaString Avro Schema字符串
     * @return
     */
    def bytes2Object(bytes: Array[Byte], schemaString: String): List[Object] = {
        bytes2Generic[Object](bytes, schemaString)
    }

    /**
     * Avro字节数组转回Avro实体（GenericRecord/GenericData.Array，字节数组中不含Schema信息）
     *
     * @param bytes Avro格式字节数组
     * @tparam T 实体类型
     * @return
     */
    def bytes2Generic[T](bytes: Array[Byte], schemaString: String): List[T] = {
        bytes2Generic[T](bytes, parser.parse(schemaString))
    }

    /**
     * Avro字节数组转回Avro实体（GenericRecord/GenericData.Array，字节数组中不含Schema信息）
     *
     * @param bytes Avro格式字节数组
     * @tparam T 实体类型
     * @return
     */
    def bytes2Generic[T](bytes: Array[Byte], schema: Schema): List[T] = {
        val listBuffer = ListBuffer[T]()
        val reader = new GenericDatumReader[T](schema)
        val binaryDecoder = DecoderFactory.get.binaryDecoder(bytes, null)
        var finished = false
        while (!finished) {
            try {
                val datum = reader.read(null.asInstanceOf[T], binaryDecoder)
                listBuffer += datum
            } catch {
                case _: EOFException => finished = true
                case e: Exception => throw e
            }
        }
        listBuffer.toList
    }

    /**
     * Avro字节数组转回Avro实体（类型未知，字节数组中不含Schema信息）
     *
     * @param bytes  Avro格式字节数组
     * @param schema Avro Schema
     * @return
     */
    def bytes2Object(bytes: Array[Byte], schema: Schema): List[Object] = {
        bytes2Generic[Object](bytes, schema)
    }

    /**
     * 从文件获取Avro Schema
     *
     * @param schemaFileName Schema文件名
     * @return
     */
    def getSchemaFromFile(schemaFileName: String): Schema = {
        val schemaInputStream = ResourceUtils.locateAsInputStream(schemaFileName)
        parser.parse(schemaInputStream)
    }

    /**
     * 从字符串获取Avro Schema
     *
     * @param schemaString Schema文件名
     * @return
     */
    def getSchemaFromString(schemaString: String): Schema = {
        parser.parse(schemaString)
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中有Schema信息）
     *
     * @param jsonString   JsonString
     * @param schemaString Avro Schema String
     * @return
     */
    def json2AvroBytesWithSchema(jsonString: String, schemaString: String): Array[Byte] = {
        val schema = parser.parse(schemaString)
        json2AvroBytesWithSchema(jsonString, schema)
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中有Schema信息）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return
     */
    def json2AvroBytesWithSchema(jsonString: String, schema: Schema): Array[Byte] = {
        // 方法1
        //    val datumWriter = new GenericDatumWriter[Object]()
        //    LoanPattern.using(new DataFileWriter[Object](datumWriter)) {
        //      writer =>
        //        LoanPattern.using(new ByteArrayInputStream(jsonString.getBytes)) {
        //          input =>
        //            LoanPattern.using(new ByteArrayOutputStream()) {
        //              output =>
        //                writer.create(schema, output)
        //                val reader = new GenericDatumReader[Object](schema)
        //                val decoder = DecoderFactory.get.jsonDecoder(schema, input)
        //                var finished = false
        //                while (!finished) {
        //                  try {
        //                    val datum = reader.read(null, decoder)
        //                    writer.append(datum)
        //                  } catch {
        //                    case _: EOFException => finished = true
        //                  }
        //                }
        //                writer.flush()
        //                output.flush()
        //                output.toByteArray
        //            }
        //        }
        //    }
        // 方法2
        val objects = json2AvroObject(jsonString, schema)
        val datumWriter = new GenericDatumWriter[Object]()
        LoanPattern.using(new DataFileWriter[Object](datumWriter)) {
            writer =>
                LoanPattern.using(new ByteArrayOutputStream()) {
                    output =>
                        writer.create(schema, output)
                        objects.foreach(writer.append)
                        writer.flush()
                        output.flush()
                        output.toByteArray
                }
        }
    }

    /**
     * 将Json String转换为Avro格式（Schema类型未知）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return
     */
    def json2AvroObject(jsonString: String, schema: Schema): List[Object] = {
        json2Avro[Object](jsonString, schema)
    }

    /**
     * 将Json String转换为Avro格式
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return
     */
    def json2Avro[T](jsonString: String, schema: Schema): List[T] = {
        val buffer = ListBuffer[T]()
        //        LoanPattern.using(new ByteArrayInputStream(jsonString.getBytes)) {
        //            input =>
        //                val reader = new GenericDatumReader[T](schema)
        //                val decoder = DecoderFactory.get.jsonDecoder(schema, input)
        //                var finished = false
        //                while (!finished) {
        //                    try {
        //                        val datum = reader.read(null.asInstanceOf[T], decoder)
        //                        buffer += datum
        //                    } catch {
        //                        case _: EOFException => finished = true
        //                    }
        //                }
        //        }
        val reader = new GenericDatumReader[T](schema)
        val jsonDecoder = new ExtendedJsonDecoder(jsonString, schema)
        var datum: T = null.asInstanceOf[T]
        var finished = false
        while (!finished) {
            try {
                datum = reader.read(datum, jsonDecoder)
                buffer += datum
            } catch {
                case _: EOFException => finished = true
            }
        }
        buffer.toList
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中无Schema信息）
     *
     * @param jsonString   JsonString
     * @param schemaString Avro Schema String
     * @return
     */
    def json2AvroBytesWithoutSchema(jsonString: String, schemaString: String): Array[Byte] = {
        val schema = parser.parse(schemaString)
        json2AvroBytesWithoutSchema(jsonString, schema)
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中无Schema信息）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return
     */
    def json2AvroBytesWithoutSchema(jsonString: String, schema: Schema): Array[Byte] = {
        LoanPattern.using(new ByteArrayOutputStream()) {
            outputStream => json2AvroBytes(jsonString, schema, outputStream)
        }
    }

    /**
     * json string转为Avro字节数组，存储于传入的ByteArrayOutputStream中
     *
     * @param jsonString   JsonString
     * @param schema       Avro Schema
     * @param outputStream ByteArrayOutputStream—
     * @return
     */
    def json2AvroBytes(jsonString: String, schema: Schema, outputStream: ByteArrayOutputStream): Array[Byte] = {
        val objects = json2AvroObject(jsonString, schema)
        val datumWriter = new GenericDatumWriter[Object](schema)
        val encoder = EncoderFactory.get.directBinaryEncoder(outputStream, null.asInstanceOf[BinaryEncoder])
        objects.foreach { obj => datumWriter.write(obj, encoder) }
        encoder.flush()
        outputStream.flush()
        outputStream.toByteArray
    }

    /**
     * 将Json String转换为Avro格式（Schema类型为record）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return
     */
    def json2AvroGenericRecord(jsonString: String, schema: Schema): List[GenericRecord] = {
        assert(schema.getType.getName == "record", "not an Avro record Schema")
        json2Avro[GenericRecord](jsonString, schema)
    }

    /**
     * 将Json String转换为Avro格式（Schema类型为array）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return
     */
    def json2AvroGenericArray(jsonString: String, schema: Schema): List[GenericData.Array[_]] = {
        assert(schema.getType.getName == "array", "not an Avro array Schema")
        json2Avro[GenericData.Array[_]](jsonString, schema)
    }

    /**
     * 根据json自动生成Avro Schema（仅供参考）
     *
     * @param jsonString json字符串
     * @param namespace  Schema namespace
     * @param name       Schema name
     * @return
     */
    def parseSchemaFromJson(jsonString: String, namespace: String, name: String): Schema = {
        new JsonToAvroConverter(namespace).convert(name, jsonString)
    }

    /**
     * 展平带有子列表的GenericRecord
     *
     * @param record      GenericRecord
     * @param independent 独立字段名称
     * @param sub         含有子级列表字段名称
     * @return
     */
    def flatRecordWithSubRecordList(record: GenericRecord, independent: String, sub: String): List[Map[String, Any]] = {
        val parentRecord = record.get(independent).asInstanceOf[GenericRecord]
        val parentMap = parseRecord2Map(parentRecord, reserveParentName = false)
        record.get(sub)
            .asInstanceOf[GenericData.Array[GenericRecord]]
            .map {
                event =>
                    val eventMap = parseRecord2Map(event, reserveParentName = false)
                    eventMap.putAll(parentMap)
                    eventMap.toMap
            }
            .toList
    }

    /**
     * 将Avro Record转换为Map
     *
     * @param record            Avro Record
     * @param reserveParentName 是否保留父一级名称
     * @return
     */
    def parseRecord2Map(record: GenericRecord, reserveParentName: Boolean): mutable.Map[String, Any] = {
        parseRecord2Map(record, reserveParentName, null)
    }

    /**
     * 将Avro Record转换为Map
     *
     * @param record            Avro Record: [[GenericRecord]]
     * @param reserveParentName 是否保留父一级名称
     * @param parentName        父一级名称
     * @return
     */
    private def parseRecord2Map(record: GenericRecord, reserveParentName: Boolean, parentName: String): mutable.Map[String, Any] = {
        val map = mutable.Map[String, Any]()
        record.getSchema.getFields.foreach {
            f =>
                val name = f.name()
                val fType = f.schema().getType.getName.toLowerCase
                fType match {
                    case "record" =>
                        val subRecordMap = if (parentName.notNullAndEmpty)
                            parseRecord2Map(record.get(name).asInstanceOf[GenericRecord], reserveParentName, parentName + "." + name)
                        else
                            parseRecord2Map(record.get(name).asInstanceOf[GenericRecord], reserveParentName, name)
                        map.putAll(subRecordMap)
                    case "array" =>
                        record.get(name)
                            .asInstanceOf[GenericData.Array[GenericRecord]]
                            .zipWithIndex
                            .foreach {
                                case (arrayRecord, index) =>
                                    val arrayRecordMap = parseRecord2Map(arrayRecord, reserveParentName, name + "." + index)
                                    map.putAll(arrayRecordMap)
                            }
                    case _ =>
                        if (reserveParentName && parentName.notNullAndEmpty)
                            map.put(parentName.toLowerCase + "." + name.toLowerCase, record.get(name))
                        else
                            map.put(name.toLowerCase, record.get(name))
                }
        }
        map
    }

}
