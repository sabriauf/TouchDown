package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sabri on 1/13/2017. data downloader
 */

public class DownloadManager {

    public static final String POST_REQUEST = "POST";
    public static final String GET_REQUEST = "GET";
    private static final String CHARSET = "UTF-8";
    private static final String APPEND_PARAM = "&";
    private static final String EQUAL = "=";

    private static String getJsonResponse(String strUrl, String params, String requestMethod,
                                          int timeout, String type, HashMap<String, String> headers) {

        HttpURLConnection urlConnection = null;
        String jsonStr = null;

        try {

            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(timeout);
            urlConnection.setConnectTimeout(timeout);

            if (headers != null)
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }


            if (type != null)
                urlConnection.setRequestProperty("content-type", type);
            urlConnection.setRequestProperty("charset", CHARSET);


            urlConnection.setRequestMethod(requestMethod);

            if (params != null && !params.equals(""))
                setParamsToRequest(urlConnection, params);

            Log.d(DownloadManager.class.getSimpleName(), "Server Request : " + strUrl + " : " + params);

            int statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;

            try {
                statusCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED || statusCode == HttpURLConnection.HTTP_ACCEPTED) {
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                jsonStr = convertInputStreamToString(inputStream);
            } else {

                InputStream inputStream = urlConnection.getErrorStream();
                if (inputStream == null) {
                    return null;
                }
                jsonStr = convertInputStreamToString(inputStream);
            }
            Log.d(DownloadManager.class.getSimpleName(), "Server Response : " + jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonStr;
    }

    private static String convertInputStreamToString(InputStream inputStream) {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        if (buffer.length() == 0) {
            return null;
        }

        return buffer.toString();
    }

    private static String getParamDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append(APPEND_PARAM);

            result.append(URLEncoder.encode(entry.getKey(), CHARSET));
            result.append(EQUAL);
            result.append(URLEncoder.encode(entry.getValue(), CHARSET));
        }
        return result.toString();
    }

    private static void setParamsToRequest(HttpURLConnection urlConnection, String params) throws IOException {
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, CHARSET));
        writer.write(params);
        writer.flush();
        writer.close();
        os.close();
    }

    static class DownloadBuilder {

        Context context;

        HashMap<String, String> headers;

        String url;
        String params = "";
        String type;
        String requestMethod;
        int timeout = 10000;

        DownloadBuilder init(Context context, String url, String requestMethod) {
            this.context = context;
            this.url = url;
            this.requestMethod = requestMethod;
            return this;
        }

        public DownloadBuilder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public DownloadBuilder setContentType(String type) {
            this.type = type;
            return this;
        }

        public DownloadBuilder setParams(HashMap<String, String> params) {
            if (params != null) {
                if (params.containsValue(null)) {
                    return this;
                }
                try {
                    this.params = getParamDataString(params);
                    return this;
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }
            return this;
        }

        public DownloadBuilder setUrlParams(HashMap<String, String> params) {
            if (params != null) {
                if (params.containsValue(null)) {
                    return this;
                }
                try {
                    this.url = url + "?" + getParamDataString(params);
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }
            return this;
        }

        public DownloadBuilder setHeaders(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        String startDownload() {
            return DownloadManager.getJsonResponse(url, params, requestMethod, timeout, type, headers);
        }
    }
}
