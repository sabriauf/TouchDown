package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lk.rc07.ten_years.touchdown.models.DownloadMeta;

/**
 * Created by Sabri on 1/13/2017. api sync data
 */

public class SynchronizeData extends AsyncTask<DownloadMeta, Void, String> {

    //constants
    private static final String JSON_STATUS_OBJECT = "JsonStatus";

    //instances
    private DownloadMeta meta;
    private Context context;
    private HashMap<String, DownloadListener> listeners;

    public SynchronizeData(Context context) {
        this.context = context;
        listeners = new HashMap<>();
    }

    public void setOnDownloadListener(String name, DownloadListener listener) {
        this.listeners.put(name, listener);
    }

    @Override
    protected String doInBackground(DownloadMeta... downloadMetas) {
        meta = downloadMetas[0];
        String url = meta.getUrl();
        return new DownloadManager.DownloadBuilder()
                .init(context, url, meta.getRequestMethod())
                .setHeaders(AppHandler.getHeaders(context))
                .startDownload();
    }

    private Void parseJsonString(String jsonString) {

        try {
            JSONObject respond = new JSONObject(jsonString);

            if (!respond.has(JSON_STATUS_OBJECT)) {
                notifyFailure(jsonString, 0);
            } else {

                JsonStatus status = new Gson().fromJson(respond.getJSONObject(JSON_STATUS_OBJECT).toString(), JsonStatus.class);

                if(status.isSuccess())
                    notifySuccess(respond.toString(), status.getCode());
                else
                    notifyFailure(status.getDescription(), status.getCode());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        parseJsonString(cleanResponse(response));
    }

    private String cleanResponse(String rawString) {
        rawString = rawString.trim();
        rawString = rawString.replaceAll("\n", "");
        return rawString;
    }

    private void notifyFailure(String message, int code) {
        for(String key: listeners.keySet()) {
            listeners.get(key).onDownloadFailed(message, meta, code);
        }
    }

    private void notifySuccess(String message, int code) {
        for(String key: listeners.keySet()) {
            listeners.get(key).onDownloadSuccess(message, meta, code);
        }
    }

    public interface DownloadListener {

        void onDownloadSuccess(String response, DownloadMeta meta, int code);

        void onDownloadFailed(String errorMessage, DownloadMeta meta, int code);
    }

    private class JsonStatus {
        private boolean success;
        private int code;
        private String description;

        String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
