package io.github.ppdzm.utils.universal.formats.avro;

import io.github.ppdzm.utils.universal.base.ResourceUtils;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2021/5/21.
 */
public class AvroUtils {
    private static final Schema.Parser PARSER = new Schema.Parser();

    /**
     * Avro字节数组转回Avro实体（类型未知，字节数组中含有Schema信息）
     *
     * @param bytes Avro格式字节数组
     * @return Object列表
     * @throws IOException IOException
     */
    public static List<Object> bytes2Object(byte[] bytes) throws IOException {
        return bytes2Generic(bytes);
        //
    }

    /**
     * Avro字节数组转回Avro实体（GenericRecord/GenericData.Array）
     *
     * @param bytes Avro格式字节数组
     * @param <T>   类型
     * @return 泛型T列表
     * @throws IOException IOException
     */
    public static <T> List<T> bytes2Generic(byte[] bytes) throws IOException {
        SeekableByteArrayInput seekableByteArrayInput = new SeekableByteArrayInput(bytes);
        SpecificDatumReader<T> datumReader = new SpecificDatumReader<>();
        DataFileReader<T> dataFileReader = new DataFileReader<>(seekableByteArrayInput, datumReader);
        List<T> list = new ArrayList<>();
        while (dataFileReader.hasNext()) {
            T datum = dataFileReader.next();
            list.add(datum);
        }
        dataFileReader.close();
        return list;
    }

    /**
     * Avro字节数组转回Avro实体（类型未知，字节数组中不含Schema信息）
     *
     * @param bytes        Avro格式字节数组
     * @param schemaString Avro Schema字符串
     * @return Object列表
     * @throws IOException IOException
     */
    public static List<Object> bytes2Object(byte[] bytes, String schemaString) throws IOException {
        return bytes2Generic(bytes, schemaString);
    }

    /**
     * Avro字节数组转回Avro实体（GenericRecord/GenericData.Array，字节数组中不含Schema信息）
     *
     * @param bytes        Avro格式字节数组
     * @param schemaString Avro Schema字符串
     * @param <T>          类型
     * @return 泛型T列表
     * @throws IOException IOException
     */
    public static <T> List<T> bytes2Generic(byte[] bytes, String schemaString) throws IOException {
        return bytes2Generic(bytes, PARSER.parse(schemaString));
    }

    /**
     * Avro字节数组转回Avro实体（类型未知，字节数组中不含Schema信息）
     *
     * @param bytes  Avro格式字节数组
     * @param schema Avro Schema
     * @return Object列表
     * @throws IOException IOException
     */
    public static List<Object> bytes2Object(byte[] bytes, Schema schema) throws IOException {
        return bytes2Generic(bytes, schema);
    }

    /**
     * Avro字节数组转回Avro实体（GenericRecord/GenericData.Array，字节数组中不含Schema信息）
     *
     * @param bytes  Avro格式字节数组
     * @param schema Avro Schema
     * @param <T>    类型
     * @return 泛型T列表
     * @throws IOException IOException
     */
    public static <T> List<T> bytes2Generic(byte[] bytes, Schema schema) throws IOException {
        GenericDatumReader<T> reader = new GenericDatumReader<>(schema);
        List<T> list = new ArrayList<>();
        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(bytes, null);
        boolean finished = false;
        while (!finished) {
            try {
                T datum = reader.read(null, binaryDecoder);
                list.add(datum);
            } catch (EOFException e) {
                finished = true;
            }
        }
        return list;
    }

    /**
     * 展平带有子列表的GenericRecord
     *
     * @param record      GenericRecord
     * @param independent 独立字段名称
     * @param sub         含有子级列表字段名称
     * @return 泛型T列表
     */
    public static List<Map<String, Object>> flatRecordWithSubRecordList(GenericRecord record, String independent, String sub) {
        GenericRecord parentRecord = (GenericRecord) record.get(independent);
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> parentMap = parseRecord2Map(parentRecord, false);
        for (GenericRecord subRecord : (GenericData.Array<GenericRecord>) record.get(sub)) {
            Map<String, Object> subMap = parseRecord2Map(subRecord, false);
            subMap.putAll(parentMap);
            mapList.add(subMap);
        }
        return mapList;
    }

    /**
     * 从文件获取Avro Schema
     *
     * @param schemaFileName Schema文件名
     * @return Schema
     * @throws IOException IOException
     */
    public static Schema getSchemaFromFile(String schemaFileName) throws IOException {
        InputStream schemaInputStream = ResourceUtils.locateAsInputStream(schemaFileName);
        return PARSER.parse(schemaInputStream);
    }

    /**
     * 从字符串获取Avro Schema
     *
     * @param schemaString Schema文件名
     * @return Schema
     */
    public static Schema getSchemaFromString(String schemaString) {
        return PARSER.parse(schemaString);
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中有Schema信息）
     *
     * @param jsonString   JsonString
     * @param schemaString Avro Schema String
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] json2AvroBytesWithSchema(String jsonString, String schemaString) throws IOException {
        return json2AvroBytesWithSchema(jsonString, PARSER.parse(schemaString));
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中有Schema信息）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] json2AvroBytesWithSchema(String jsonString, Schema schema) throws IOException {
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
        List<Object> objects = json2AvroObject(jsonString, schema);
        GenericDatumWriter<Object> datumWriter = new GenericDatumWriter<>();
        DataFileWriter<Object> dataFileWriter = new DataFileWriter<>(datumWriter);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        dataFileWriter.create(schema, output);
        for (Object object : objects) {
            dataFileWriter.append(object);
        }
        dataFileWriter.flush();
        output.flush();
        return output.toByteArray();
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中无Schema信息）
     *
     * @param jsonString   JsonString
     * @param schemaString Avro Schema String
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] json2AvroBytesWithoutSchema(String jsonString, String schemaString) throws IOException {
        Schema schema = PARSER.parse(schemaString);
        return json2AvroBytesWithoutSchema(jsonString, schema);
    }

    /**
     * 将Json String转换为Avro格式字节数组（字节数组中无Schema信息）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] json2AvroBytesWithoutSchema(String jsonString, Schema schema) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        return json2AvroBytes(jsonString, schema, outputStream);
    }

    /**
     * json string转为Avro字节数组，存储于传入的ByteArrayOutputStream中
     *
     * @param jsonString   JsonString
     * @param schema       Avro Schema
     * @param outputStream ByteArrayOutputStream—
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] json2AvroBytes(String jsonString, Schema schema, ByteArrayOutputStream outputStream) throws IOException {
        List<Object> objects = json2AvroObject(jsonString, schema);
        GenericDatumWriter<Object> datumWriter = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        for (Object object : objects) {
            datumWriter.write(object, encoder);
        }
        encoder.flush();
        outputStream.flush();
        return outputStream.toByteArray();
    }

    /**
     * 将Json String转换为Avro格式（Schema类型未知）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return 泛型T列表
     * @throws IOException IOException
     */
    public static List<Object> json2AvroObject(String jsonString, Schema schema) throws IOException {
        return json2Avro(jsonString, schema);
    }

    /**
     * 将Json String转换为Avro格式
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @param <T>        类型
     * @return 泛型T列表
     * @throws IOException IOException
     */
    public static <T> List<T> json2Avro(String jsonString, Schema schema) throws IOException {
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
        GenericDatumReader<T> reader = new GenericDatumReader<>(schema);
        ExtendedJsonDecoder jsonDecoder = new ExtendedJsonDecoder(jsonString, schema);
        List<T> list = new ArrayList<>();
        boolean finished = false;
        T datum = null;
        while (!finished) {
            try {
                datum = reader.read(datum, jsonDecoder);
                list.add(datum);
            } catch (EOFException e) {
                finished = true;
            }
        }
        return list;
    }

    /**
     * 将Json String转换为Avro格式（Schema类型为record）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @return GenericRecord列表
     * @throws IOException IOException
     */
    public static List<GenericRecord> json2AvroGenericRecord(String jsonString, Schema schema) throws IOException {
        return json2Avro(jsonString, schema);
    }

    /**
     * 将Json String转换为Avro格式（Schema类型为array）
     *
     * @param jsonString JsonString
     * @param schema     Avro Schema
     * @param <T>        类型
     * @return 泛型T列表
     * @throws IOException IOException
     */
    public static <T> List<GenericData.Array<T>> json2AvroGenericArray(String jsonString, Schema schema) throws IOException {
        return json2Avro(jsonString, schema);
    }

    /**
     * 将Avro Record转换为Map
     *
     * @param record            Avro Record
     * @param reserveParentName 是否保留父一级名称
     * @return 泛型T列表
     */
    public static Map<String, Object> parseRecord2Map(GenericRecord record, boolean reserveParentName) {
        return parseRecord2Map(record, reserveParentName, null);
    }

    /**
     * 将Avro Record转换为Map
     *
     * @param record            Avro Record: [[GenericRecord]]
     * @param reserveParentName 是否保留父一级名称
     * @param parentName        父一级名称
     * @return Map<String, Object>
     */
    private static Map<String, Object> parseRecord2Map(GenericRecord record, boolean reserveParentName, String parentName) {
        Map<String, Object> resultMap = new HashMap<>(4);
        if (record == null) {
            return resultMap;
        }
        for (Schema.Field field : record.getSchema().getFields()) {
            String fieldName = field.name();
            Object fieldValue = record.get(fieldName);
            String fieldType = field.schema().getType().getName().toLowerCase();
            switch (fieldType) {
                case "array":
                    GenericData.Array<GenericRecord> records = (GenericData.Array<GenericRecord>) record.get(fieldName);
                    for (int i = 0; i < records.size(); i++) {
                        GenericRecord rec = records.get(i);
                        Map<String, Object> subMap = parseRecord2Map(rec, reserveParentName, fieldName + "." + i);
                        resultMap.putAll(subMap);
                    }
                    break;
                case "int":
                    if (fieldValue == null) {
                        resultMap.put(fieldName, null);
                    } else {
                        resultMap.put(fieldName, Integer.parseInt(String.valueOf(fieldValue)));
                    }
                    break;
                case "long":
                    if (fieldValue == null) {
                        resultMap.put(fieldName, null);
                    } else {
                        resultMap.put(fieldName, Long.parseLong(String.valueOf(fieldValue)));
                    }
                    break;
                case "map":
                    resultMap.put(fieldName, parseMapField2Map(fieldValue));
                    break;
                case "record":
                    Map<String, Object> subRecordMap;
                    if (parentName == null || "".equals(parentName)) {
                        subRecordMap = parseRecord2Map((GenericRecord) record.get(fieldName), reserveParentName, fieldName);
                    } else {
                        subRecordMap = parseRecord2Map((GenericRecord) record.get(fieldName), reserveParentName, parentName + "." + fieldName);
                    }
                    resultMap.putAll(subRecordMap);
                    break;
                case "string":
                    if (fieldValue == null) {
                        resultMap.put(fieldName, null);
                    } else {
                        resultMap.put(fieldName, String.valueOf(fieldValue));
                    }
                    break;
                case "union":
                    if (fieldValue == null) {
                        break;
                    }
                    boolean finished = false;
                    for (Schema typeInUnion : field.schema().getTypes()) {
                        switch (typeInUnion.getType().getName()) {
                            case "int":
                                resultMap.put(fieldName, Integer.parseInt(String.valueOf(fieldValue)));
                                break;
                            case "long":
                                resultMap.put(fieldName, Long.parseLong(String.valueOf(fieldValue)));
                                break;
                            case "map":
                                resultMap.put(fieldName, parseMapField2Map(fieldValue));
                                finished = true;
                                break;
                            case "null":
                                break;
                            case "record":
                                Map<String, Object> recordMapInUnion;
                                if (parentName == null || "".equals(parentName)) {
                                    recordMapInUnion = parseRecord2Map((GenericRecord) record.get(fieldName), reserveParentName, fieldName);
                                } else {
                                    recordMapInUnion = parseRecord2Map((GenericRecord) record.get(fieldName), reserveParentName, parentName + "." + fieldName);
                                }
                                resultMap.putAll(recordMapInUnion);
                                finished = true;
                                break;
                            default:
                                resultMap.put(fieldName, String.valueOf(record.get(fieldName)));
                                finished = true;
                                break;
                        }
                        if (finished) {
                            break;
                        }
                    }
                    break;
                default:
                    if (reserveParentName && parentName != null && !"".equals(parentName)) {
                        resultMap.put(parentName + "." + fieldName, record.get(fieldName));
                    } else {
                        resultMap.put(fieldName, record.get(fieldName));
                    }
                    break;
            }
        }
        return resultMap;
    }

    /**
     * 将MapField转为Java Map
     *
     * @param mapField Avro Map Field
     * @return Map<String, String>
     */
    private static Map<String, String> parseMapField2Map(Object mapField) {
        Map<String, String> resultMap = new HashMap<>(4);
        if (mapField == null) {
            return resultMap;
        }
        Map<?, ?> fieldMap = (Map<?, ?>) mapField;
        for (Object key : fieldMap.keySet()) {
            Object value = fieldMap.get(key);
            if (value == null) {
                resultMap.put(String.valueOf(key), null);
            } else {
                resultMap.put(String.valueOf(key), String.valueOf(value));
            }
        }
        return resultMap;
    }
}
