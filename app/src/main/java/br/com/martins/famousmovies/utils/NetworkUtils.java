/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.martins.famousmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
  André Martins dos Santos
 */
public final class NetworkUtils {

    private static OkHttpClient client = new OkHttpClient();

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static boolean isConnectOnNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().getDetailedState().equals(NetworkInfo.DetailedState.CONNECTED)) {
                return true;
            }
        }
        return false;
    }
}