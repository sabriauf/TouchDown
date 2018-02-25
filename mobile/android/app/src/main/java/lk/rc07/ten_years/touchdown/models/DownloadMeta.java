package lk.rc07.ten_years.touchdown.models;

import java.util.HashMap;

/**
 * Created by Sabri on 1/13/2017. meta data object for downloading
 */

public class DownloadMeta {

    private int what;
    private HashMap<String, String> params;
    private String url;
    private HashMap<String, String> urlParams;
    private HashMap<String, String> headers;
    private String requestMethod;

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(HashMap<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }
}
