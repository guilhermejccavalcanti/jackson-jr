package com.fasterxml.jackson.jr.ob.impl;

import java.io.IOException;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

/**
 * Reader for typed {@link java.util.Collection} values.
 */
public class ArrayReader extends ValueReader {

    protected final Class<?> _elementType;

    protected final ValueReader _valueReader;

    public ArrayReader(Class<?> t, ValueReader vr) {
        _elementType = t;
        _valueReader = vr;
    }

    @Override
    public Object readNext(JSONReader r, JsonParser p) throws IOException {
        if (p.nextToken() != JsonToken.START_ARRAY) {
            if (p.hasToken(JsonToken.VALUE_NULL)) {
                return null;
            }
            return JSONObjectException.from(p, "Unexpected token " + p.getCurrentToken() + "; should get START_ARRAY");
        }
        CollectionBuilder b = r._collectionBuilder(null);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return b.emptyArray(_elementType);
        }
        Object value = _valueReader.read(r, p);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return b.singletonArray(_elementType, value);
        }
        b = b.start().add(value);
        do {
            b = b.add(_valueReader.read(r, p));
        } while (p.nextToken() != JsonToken.END_ARRAY);
        return b.buildArray(_elementType);
    }

    @Override
    public Object read(JSONReader r, JsonParser p) throws IOException {
        CollectionBuilder b = r._collectionBuilder(null);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return b.emptyArray(_elementType);
        }
        Object value = _valueReader.read(r, p);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return b.singletonArray(_elementType, value);
        }
        b = b.start().add(value);
        do {
            b = b.add(_valueReader.read(r, p));
        } while (p.nextToken() != JsonToken.END_ARRAY);
        return b.buildArray(_elementType);
    }
}
