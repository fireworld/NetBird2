package cc.colorcat.demo;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cc.colorcat.netbird2.NetworkData;
import cc.colorcat.netbird2.Parser;
import cc.colorcat.netbird2.Response;
import cc.colorcat.netbird2.StateIOException;


/**
 * Created by cxx on 16-11-1.
 * xx.ch@outlook.com
 */

public class GsonParser<T> implements Parser<T> {
    private static final Gson GSON;
    private TypeToken<T> token;

    static {
        GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").serializeNulls().create();
    }

    public GsonParser(TypeToken<T> token) {
        if (token == null) {
            throw new NullPointerException("token == null");
        }
        this.token = token;
    }

    @NonNull
    @Override
    public NetworkData<? extends T> parse(@NonNull Response data) throws IOException {
        try {
            T t = GSON.fromJson(data.body().string(), token.getType());
            return NetworkData.newSuccess(t);
        } catch (JsonParseException e) {
            throw new StateIOException(data.msg(), e, data.code());
        }
    }
}
