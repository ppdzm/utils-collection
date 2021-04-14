package io.github.ppdzm.utils.universal.formats.avro

import java.util

import com.fasterxml.jackson.core.JsonParser

/**
 * Created by Stuart Alex on 2021/3/18.
 */
class ReorderBuffer {
    var savedFields = new util.HashMap[String, util.List[JsonElement]]
    var origParser: JsonParser = _
}
