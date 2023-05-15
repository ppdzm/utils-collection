package io.github.ppdzm.utils.universal.formats.avro;


import com.fasterxml.jackson.core.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2021/5/22.
 */
public class ExtendedJsonParser extends JsonParser {
    private final List<JsonElement> elements;
    private int pos = 0;

    public ExtendedJsonParser(List<JsonElement> elements) {
        this.elements = elements;
    }

    @Override
    public ObjectCodec getCodec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCodec(ObjectCodec c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonToken nextToken() {
        pos++;
        return elements.get(pos).token;
    }

    @Override
    public JsonToken nextValue() {
        return null;
    }

    @Override
    public JsonParser skipChildren() {
        int level = 0;
        do {
            switch (elements.get(pos++).token) {
                case START_ARRAY:
                case START_OBJECT:
                    level++;
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    level--;
                    break;
            }
        } while (level > 0);
        return this;
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonStreamContext getParsingContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonLocation getTokenLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonLocation getCurrentLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText() {
        return elements.get(pos).value;
    }

    @Override
    public char[] getTextCharacters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTextLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTextOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public Number getNumberValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NumberType getNumberType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIntValue() {
        return Integer.parseInt(getText());
    }

    @Override
    public long getLongValue() {
        return Long.parseLong(getText());
    }

    @Override
    public BigInteger getBigIntegerValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getFloatValue() {
        return Float.parseFloat(getText());
    }

    @Override
    public double getDoubleValue() {
        return Double.parseDouble(getText());
    }

    @Override
    public BigDecimal getDecimalValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValueAsString(String s) {
        return null;
    }

    @Override
    public JsonToken getCurrentToken() {
        return elements.get(pos).token;
    }

    @Override
    public int getCurrentTokenId() {
        return 0;
    }

    @Override
    public boolean hasCurrentToken() {
        return false;
    }

    @Override
    public boolean hasTokenId(int i) {
        return false;
    }

    @Override
    public boolean hasToken(JsonToken jsonToken) {
        return false;
    }

    @Override
    public void clearCurrentToken() {

    }

    @Override
    public JsonToken getLastClearedToken() {
        return null;
    }

    @Override
    public void overrideCurrentName(String s) {

    }
}
