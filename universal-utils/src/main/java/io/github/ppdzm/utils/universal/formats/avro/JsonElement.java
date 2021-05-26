package io.github.ppdzm.utils.universal.formats.avro;

import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Created by Stuart Alex on 2021/5/22.
 */
public class JsonElement {
    public final JsonToken token;
    public final String value;

    public JsonElement(JsonToken t, String value) {
        this.token = t;
        this.value = value;
    }

    public JsonElement(JsonToken t) {
        this(t, null);
    }
}
