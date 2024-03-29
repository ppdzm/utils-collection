package io.github.ppdzm.utils.flink.scala.serialization

import java.io.ByteArrayOutputStream
import io.github.ppdzm.utils.universal.formats.avro.AvroUtils
import org.apache.avro.Schema
import org.apache.flink.formats.avro.{RegistryAvroSerializationSchema, SchemaCoder}
import scalikejdbc.LoanPattern

/**
 * Created by Stuart Alex on 2021/1/29.
 */
case class Json2RegistryAvroSerializationSchema(schema: Schema, schemaCoderProvider: SchemaCoder.SchemaCoderProvider)
    extends RegistryAvroSerializationSchema[String](classOf[String], schema, schemaCoderProvider) {

    override def serialize(value: String): Array[Byte] = {
        LoanPattern.using(new ByteArrayOutputStream()) {
            outputStream =>
                schemaCoderProvider.get().writeSchema(schema, outputStream)
                AvroUtils.json2AvroBytes(value, schema, outputStream)
        }
    }

}