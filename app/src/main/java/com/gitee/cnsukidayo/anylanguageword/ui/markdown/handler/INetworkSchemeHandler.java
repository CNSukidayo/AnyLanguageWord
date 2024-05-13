package com.gitee.cnsukidayo.anylanguageword.ui.markdown.handler;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import io.github.cnsukidayo.wword.common.request.RequestHandler;
import io.github.cnsukidayo.wword.common.request.RequestRegister;
import io.noties.markwon.image.ImageItem;
import io.noties.markwon.image.SchemeHandler;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

/**
 * A simple network scheme handler that is not dependent on any external libraries.
 *
 * @see #create()
 * @since 3.0.0
 */
public class INetworkSchemeHandler extends SchemeHandler {

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    @NonNull
    public static INetworkSchemeHandler create() {
        return new INetworkSchemeHandler();
    }

    @SuppressWarnings("WeakerAccess")
    INetworkSchemeHandler() {
    }

    @NonNull
    @Override
    public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {

        final ImageItem imageItem;
        Response response = null;
        try {

            RequestHandler requestHandler = RequestRegister.getRequestHandler();
            Request request = new Request.Builder()
                    .url(raw)
                    .get()
                    .build();
            response = requestHandler.execute(request);

            if (response.isSuccessful()) {
                BufferedSource source = response.body().source();
                Buffer buffer = source.getBuffer();

                final String contentType = contentType(response.headers().get("Content-Type"));
                final InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(buffer.readByteArray()));
                imageItem = ImageItem.withDecodingNeeded(contentType, inputStream);
            } else {
                throw new IOException("Bad response code: " + response + ", url: " + raw);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Exception obtaining network resource: " + raw, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return imageItem;
    }

    @NonNull
    @Override
    public Collection<String> supportedSchemes() {
        return Arrays.asList(SCHEME_HTTP, SCHEME_HTTPS);
    }

    @Nullable
    static String contentType(@Nullable String contentType) {

        if (contentType == null) {
            return null;
        }

        final int index = contentType.indexOf(';');
        if (index > -1) {
            return contentType.substring(0, index);
        }

        return contentType;
    }
}
