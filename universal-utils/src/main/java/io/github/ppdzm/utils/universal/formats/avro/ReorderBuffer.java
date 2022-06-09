package io.github.ppdzm.utils.universal.formats.avro;

import com.fasterxml.jackson.core.JsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by Stuart Alex on 2021/5/22.
 */
public class ReorderBuffer {
    public Map<String, List<JsonElement>> savedFields = new HashMap<>();
    public JsonParser origParser = null;
}
