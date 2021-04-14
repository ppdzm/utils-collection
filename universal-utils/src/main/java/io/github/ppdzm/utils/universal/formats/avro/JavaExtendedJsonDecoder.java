package io.github.ppdzm.utils.universal.formats.avro;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.io.ParsingDecoder;
import org.apache.avro.io.parsing.JsonGrammarGenerator;
import org.apache.avro.io.parsing.Parser;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.util.Utf8;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by Stuart Alex on 2021/3/24.
 */
public class JavaExtendedJsonDecoder extends ParsingDecoder implements Parser.ActionHandler {
    static final String CHARSET = "ISO-8859-1";
    private static final JsonElement NULL_JSON_ELEMENT = new JsonElement(null);
    private static JsonFactory jsonFactory = new JsonFactory();
    private final Schema schema;
    Stack<ReorderBuffer> reorderBuffers = new Stack<ReorderBuffer>();
    ReorderBuffer currentReorderBuffer;
    private JsonParser in;

    public JavaExtendedJsonDecoder(InputStream in, Schema schema) throws IOException {
        super(getSymbol(schema));
        configure(in);
        this.schema = schema;
    }

    public JavaExtendedJsonDecoder(String in, Schema schema) throws IOException {
        super(getSymbol(schema));
        configure(in);
        this.schema = schema;
    }

    private static Symbol getSymbol(Schema schema) {
        if (null == schema) {
            throw new NullPointerException("Schema cannot be null!");
        }
        return new JsonGrammarGenerator().generate(schema);
    }

    private static List<JsonElement> getVaueAsTree(JsonParser in) throws IOException {
        int level = 0;
        List<JsonElement> result = new ArrayList<JsonElement>();
        do {
            JsonToken t = in.getCurrentToken();
            switch (t) {
                case START_OBJECT:
                case START_ARRAY:
                    level++;
                    result.add(new JsonElement(t));
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    level--;
                    result.add(new JsonElement(t));
                    break;
                case FIELD_NAME:
                case VALUE_STRING:
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                    result.add(new JsonElement(t, in.getText()));
                    break;
            }
            in.nextToken();
        } while (level != 0);
        result.add(new JsonElement(null));
        return result;
    }

    private static Field findField(Schema schema, String name) {
        if (schema.getField(name) != null) {
            return schema.getField(name);
        }

        Field foundField = null;

        for (Field field : schema.getFields()) {
            Schema fieldSchema = field.schema();
            if (Type.RECORD.equals(fieldSchema.getType())) {
                foundField = findField(fieldSchema, name);
            } else if (Type.ARRAY.equals(fieldSchema.getType())) {
                foundField = findField(fieldSchema.getElementType(), name);
            } else if (Type.MAP.equals(fieldSchema.getType())) {
                foundField = findField(fieldSchema.getValueType(), name);
            }

            if (foundField != null) {
                return foundField;
            }
        }

        return foundField;
    }

    public JavaExtendedJsonDecoder configure(InputStream in) throws IOException {
        if (null == in) {
            throw new NullPointerException("InputStream to read from cannot be null!");
        }
        parser.reset();
        this.in = jsonFactory.createJsonParser(in);
        this.in.nextToken();
        return this;
    }

    public JavaExtendedJsonDecoder configure(String in) throws IOException {
        if (null == in) {
            throw new NullPointerException("String to read from cannot be null!");
        }
        parser.reset();
        this.in = new JsonFactory().createJsonParser(in);
        this.in.nextToken();
        return this;
    }

    private void advance(Symbol symbol) throws IOException {
        this.parser.processTrailingImplicitActions();
        if (in.getCurrentToken() == null && this.parser.depth() == 1)
            throw new EOFException();
        parser.advance(symbol);
    }

    @Override
    public void readNull() throws IOException {
        advance(Symbol.NULL);
        if (in.getCurrentToken() == JsonToken.VALUE_NULL) {
            in.nextToken();
        } else {
            throw error("null");
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        advance(Symbol.BOOLEAN);
        JsonToken t = in.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE || t == JsonToken.VALUE_FALSE) {
            in.nextToken();
            return t == JsonToken.VALUE_TRUE;
        } else {
            throw error("boolean");
        }
    }

    @Override
    public int readInt() throws IOException {
        advance(Symbol.INT);
        if (in.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            int result = in.getIntValue();
            in.nextToken();
            return result;
        } else {
            throw error("int");
        }
    }

    @Override
    public long readLong() throws IOException {
        advance(Symbol.LONG);
        if (in.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            long result = in.getLongValue();
            in.nextToken();
            return result;
        } else {
            throw error("long");
        }
    }

    @Override
    public float readFloat() throws IOException {
        advance(Symbol.FLOAT);
        if (in.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            float result = in.getFloatValue();
            in.nextToken();
            return result;
        } else {
            throw error("float");
        }
    }

    @Override
    public double readDouble() throws IOException {
        advance(Symbol.DOUBLE);
        if (in.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            double result = in.getDoubleValue();
            in.nextToken();
            return result;
        } else {
            throw error("double");
        }
    }

    @Override
    public Utf8 readString(Utf8 old) throws IOException {
        return new Utf8(readString());
    }

    @Override
    public String readString() throws IOException {
        advance(Symbol.STRING);
        if (parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            parser.advance(Symbol.MAP_KEY_MARKER);
            if (in.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw error("map-key");
            }
        } else {
            if (in.getCurrentToken() != JsonToken.VALUE_STRING) {
                throw error("string");
            }
        }
        String result = in.getText();
        in.nextToken();
        return result;
    }

    @Override
    public void skipString() throws IOException {
        advance(Symbol.STRING);
        if (parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            parser.advance(Symbol.MAP_KEY_MARKER);
            if (in.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw error("map-key");
            }
        } else {
            if (in.getCurrentToken() != JsonToken.VALUE_STRING) {
                throw error("string");
            }
        }
        in.nextToken();
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer old) throws IOException {
        advance(Symbol.BYTES);
        if (in.getCurrentToken() == JsonToken.VALUE_STRING) {
            byte[] result = readByteArray();
            in.nextToken();
            return ByteBuffer.wrap(result);
        } else {
            throw error("bytes");
        }
    }

    private byte[] readByteArray() throws IOException {
        byte[] result = in.getText().getBytes(CHARSET);
        return result;
    }

    @Override
    public void skipBytes() throws IOException {
        advance(Symbol.BYTES);
        if (in.getCurrentToken() == JsonToken.VALUE_STRING) {
            in.nextToken();
        } else {
            throw error("bytes");
        }
    }

    private void checkFixed(int size) throws IOException {
        advance(Symbol.FIXED);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction) parser.popSymbol();
        if (size != top.size) {
            throw new AvroTypeException(
                "Incorrect length for fixed binary: expected " +
                    top.size + " but received " + size + " bytes.");
        }
    }

    @Override
    public void readFixed(byte[] bytes, int start, int len) throws IOException {
        checkFixed(len);
        if (in.getCurrentToken() == JsonToken.VALUE_STRING) {
            byte[] result = readByteArray();
            in.nextToken();
            if (result.length != len) {
                throw new AvroTypeException("Expected fixed length " + len
                    + ", but got" + result.length);
            }
            System.arraycopy(result, 0, bytes, start, len);
        } else {
            throw error("fixed");
        }
    }

    @Override
    public void skipFixed(int length) throws IOException {
        checkFixed(length);
        doSkipFixed(length);
    }

    private void doSkipFixed(int length) throws IOException {
        if (in.getCurrentToken() == JsonToken.VALUE_STRING) {
            byte[] result = readByteArray();
            in.nextToken();
            if (result.length != length) {
                throw new AvroTypeException("Expected fixed length " + length
                    + ", but got" + result.length);
            }
        } else {
            throw error("fixed");
        }
    }

    @Override
    protected void skipFixed() throws IOException {
        advance(Symbol.FIXED);
        Symbol.IntCheckAction top = (Symbol.IntCheckAction) parser.popSymbol();
        doSkipFixed(top.size);
    }

    @Override
    public int readEnum() throws IOException {
        advance(Symbol.ENUM);
        Symbol.EnumLabelsAction top = (Symbol.EnumLabelsAction) parser.popSymbol();
        if (in.getCurrentToken() == JsonToken.VALUE_STRING) {
            in.getText();
            int n = top.findLabel(in.getText());
            if (n >= 0) {
                in.nextToken();
                return n;
            }
            throw new AvroTypeException("Unknown symbol in enum " + in.getText());
        } else {
            throw error("fixed");
        }
    }

    @Override
    public long readArrayStart() throws IOException {
        advance(Symbol.ARRAY_START);
        if (in.getCurrentToken() == JsonToken.START_ARRAY) {
            in.nextToken();
            return doArrayNext();
        } else {
            throw error("array-start");
        }
    }

    @Override
    public long arrayNext() throws IOException {
        advance(Symbol.ITEM_END);
        return doArrayNext();
    }

    private long doArrayNext() throws IOException {
        if (in.getCurrentToken() == JsonToken.END_ARRAY) {
            parser.advance(Symbol.ARRAY_END);
            in.nextToken();
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long skipArray() throws IOException {
        advance(Symbol.ARRAY_START);
        if (in.getCurrentToken() == JsonToken.START_ARRAY) {
            in.skipChildren();
            in.nextToken();
            advance(Symbol.ARRAY_END);
        } else {
            throw error("array-start");
        }
        return 0;
    }

    @Override
    public long readMapStart() throws IOException {
        advance(Symbol.MAP_START);
        if (in.getCurrentToken() == JsonToken.START_OBJECT) {
            in.nextToken();
            return doMapNext();
        } else {
            throw error("map-start");
        }
    }

    @Override
    public long mapNext() throws IOException {
        advance(Symbol.ITEM_END);
        return doMapNext();
    }

    private long doMapNext() throws IOException {
        if (in.getCurrentToken() == JsonToken.END_OBJECT) {
            in.nextToken();
            advance(Symbol.MAP_END);
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long skipMap() throws IOException {
        advance(Symbol.MAP_START);
        if (in.getCurrentToken() == JsonToken.START_OBJECT) {
            in.skipChildren();
            in.nextToken();
            advance(Symbol.MAP_END);
        } else {
            throw error("map-start");
        }
        return 0;
    }

    @Override
    public int readIndex() throws IOException {
        advance(Symbol.UNION);
        Symbol.Alternative a = (Symbol.Alternative) parser.popSymbol();

        String label;
        if (in.getCurrentToken() == JsonToken.VALUE_NULL) {
            label = "null";
        } else if (in.getCurrentToken() == JsonToken.START_OBJECT &&
            in.nextToken() == JsonToken.FIELD_NAME) {
            label = in.getText();
            in.nextToken();
            parser.pushSymbol(Symbol.UNION_END);
        } else if (a.size() == 2 &&
            ("null".equals(a.getLabel(0)) || "null".equals(a.getLabel(1)))) {
            label = ("null".equals(a.getLabel(0)) ? a.getLabel(1) : a.getLabel(0));
        } else {
            throw error("start-union");
        }
        for (String ech_label : a.labels) {
            System.out.println("each:" + ech_label);
        }
        System.out.println("label:" + label);
        int n = a.findLabel(label);
        if (n < 0) {
            // throw new AvroTypeException("Unknown union branch " + label);
            n = 0;
        }
        parser.pushSymbol(a.getSymbol(n));
        return n;
    }

    @Override
    public Symbol doAction(Symbol input, Symbol top) throws IOException {
        if (top instanceof Symbol.FieldAdjustAction) {
            Symbol.FieldAdjustAction fa = (Symbol.FieldAdjustAction) top;
            String name = fa.fname;
            if (currentReorderBuffer != null) {
                List<JsonElement> node = currentReorderBuffer.savedFields.get(name);
                if (node != null) {
                    currentReorderBuffer.savedFields.remove(name);
                    currentReorderBuffer.origParser = in;
                    in = makeParser(node);
                    return null;
                }
            }
            if (in.getCurrentToken() == JsonToken.FIELD_NAME) {
                do {
                    String fn = in.getText();
                    in.nextToken();
                    if (name.equals(fn)) {
                        return null;
                    } else {
                        if (currentReorderBuffer == null) {
                            currentReorderBuffer = new ReorderBuffer();
                        }
                        currentReorderBuffer.savedFields.put(fn, getVaueAsTree(in));
                    }
                } while (in.getCurrentToken() == JsonToken.FIELD_NAME);
                // throw new AvroTypeException("Expected field name not found: " + fa.fname);
                System.out.println("Expected field name not found: " + fa.fname);
                injectDefaultValueIfAvailable(in, fa.fname);
            } else {
                injectDefaultValueIfAvailable(in, fa.fname);
            }
        } else if (top == Symbol.FIELD_END) {
            if (currentReorderBuffer != null && currentReorderBuffer.origParser != null) {
                in = currentReorderBuffer.origParser;
                currentReorderBuffer.origParser = null;
            }
        } else if (top == Symbol.RECORD_START) {
            if (in.getCurrentToken() == JsonToken.START_OBJECT) {
                in.nextToken();
                reorderBuffers.push(currentReorderBuffer);
                currentReorderBuffer = null;
            } else {
                throw error("record-start");
            }
        } else if (top == Symbol.RECORD_END || top == Symbol.UNION_END) {
            if (in.getCurrentToken() == JsonToken.END_OBJECT) {
                in.nextToken();
                if (top == Symbol.RECORD_END) {
                    if (currentReorderBuffer != null && !currentReorderBuffer.savedFields.isEmpty()) {
                        throw error("Unknown fields: " + currentReorderBuffer.savedFields.keySet());
                    }
                    currentReorderBuffer = reorderBuffers.pop();
                }
            } else {
                throw error(top == Symbol.RECORD_END ? "record-end" : "union-end");
            }
        } else {
            throw new AvroTypeException("Unknown action symbol " + top);
        }
        return null;
    }

    private JsonParser makeParser(final List<JsonElement> elements) throws IOException {
        return new JsonParser() {
            int pos = 0;

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
            public void close() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public JsonToken nextToken() throws IOException {
                pos++;
                return elements.get(pos).token;
            }

            @Override
            public JsonToken nextValue() throws IOException {
                return null;
            }

            @Override
            public JsonParser skipChildren() throws IOException {
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
            public String getCurrentName() throws IOException {
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
            public String getText() throws IOException {
                return elements.get(pos).value;
            }

            @Override
            public char[] getTextCharacters() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getTextLength() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getTextOffset() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasTextCharacters() {
                return false;
            }

            @Override
            public Number getNumberValue() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public NumberType getNumberType() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getIntValue() throws IOException {
                return Integer.parseInt(getText());
            }

            @Override
            public long getLongValue() throws IOException {
                return Long.parseLong(getText());
            }

            @Override
            public BigInteger getBigIntegerValue() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public float getFloatValue() throws IOException {
                return Float.parseFloat(getText());
            }

            @Override
            public double getDoubleValue() throws IOException {
                return Double.parseDouble(getText());
            }

            @Override
            public BigDecimal getDecimalValue() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public byte[] getBinaryValue(Base64Variant b64variant)
                throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getValueAsString(String s) throws IOException {
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
        };
    }

    private AvroTypeException error(String type) {
        return new AvroTypeException("Expected " + type +
            ". Got " + in.getCurrentToken());
    }

    private void injectDefaultValueIfAvailable(final JsonParser in, String fieldName) throws IOException {
        /*
        Field field = findField(schema.getElementType(), fieldName);

        if (field == null) {
            throw new AvroTypeException("Expected field name not found: " + fieldName);
        }

        JsonNode defVal = field.defaultValue();
        if (defVal == null) {
            throw new AvroTypeException("Expected field name not found: " + fieldName);
        }
        */

        JsonNode defVal = NullNode.instance;
        List<JsonElement> result = new ArrayList<JsonElement>(2);

        JsonParser traverse = defVal.traverse();
        JsonToken nextToken;
        while ((nextToken = traverse.nextToken()) != null) {
            if (nextToken.isScalarValue()) {
                result.add(new JsonElement(nextToken, traverse.getText()));
            } else {
                result.add(new JsonElement(nextToken));
            }
        }
        result.add(NULL_JSON_ELEMENT);
        if (currentReorderBuffer == null) {
            currentReorderBuffer = new ReorderBuffer();
        }
        currentReorderBuffer.origParser = in;
        this.in = makeParser(result);
    }

    private static class ReorderBuffer {
        public Map<String, List<JsonElement>> savedFields = new HashMap<String, List<JsonElement>>();
        public JsonParser origParser = null;
    }

    private static class JsonElement {
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
}
