package com.hot.sentefinder.utilities;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jamie on 4/20/2017.
 */

public class InternetInterceptor implements Interceptor {
    private static final String TAG = InternetInterceptor.class.getSimpleName();
    private Context context;

    public InternetInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(context)) {
            try {
                throw new NoInternetConnectionException();
            } catch (NoInternetConnectionException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "connection established");
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    private class NoInternetConnectionException extends Throwable {
        @Override
        public String getMessage() {
            super.getMessage();
            return "No connectivity exception";
        }
    }
}
