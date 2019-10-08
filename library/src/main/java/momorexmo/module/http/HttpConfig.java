package momorexmo.module.http;

import org.apache.http.client.methods.HttpRequestBase;

import java.util.Hashtable;
import java.util.Iterator;

import momorexmo.module.AppSettings;
import momorexmo.module.DataTable;
import momorexmo.module.Interfaces.getGenericItem;

public class HttpConfig
{

    public static String getToken() {
        try
        {
            return AppSettings.get("TOKEN","");
        }
        catch (Exception ex) {
            return "";
        }
    }
    public static void setToken(String token)
    {
        AppSettings.set("TOKEN",token);
    }

    private static String tokenType = "bearer";
    public static String getTokenType() {
        return tokenType + (tokenType.length() > 0 ? " " : tokenType );
    }
    public static void setTokenType(String tokenType) {
        HttpConfig.tokenType = tokenType;
    }

    private static boolean addHttpReqestWithDeviceInfo = false;
    public static boolean isAddHttpReqestWithDeviceInfo() {
        return addHttpReqestWithDeviceInfo;
    }
    public static void setAddHttpReqestWithDeviceInfo(boolean addHttpReqestWithDeviceInfo) {
        HttpConfig.addHttpReqestWithDeviceInfo = addHttpReqestWithDeviceInfo;
    }

    private static Object constHttpHeader = null;
    public static Object getConstHttpHeader() {
        return constHttpHeader;
    }
    public static void setConstHttpHeader(Object constHttpHeader) {
        HttpConfig.constHttpHeader = constHttpHeader;
    }

    private static getGenericItem<Hashtable> constHtppHashHeader = null;
    public static getGenericItem<Hashtable> getConstHtppHashHeader() {
        return constHtppHashHeader;
    }
    public static void setConstHtppHashHeader(getGenericItem<Hashtable> constHtppHashHeader) {
        HttpConfig.constHtppHashHeader = constHtppHashHeader;
    }

    private static Object tempHttpHeader = null;

    public static Object getTempHttpHeader() {
        return tempHttpHeader;
    }

    public static void setTempHttpHeader(Object tempHttpHeader) {
        HttpConfig.tempHttpHeader = tempHttpHeader;
    }


    public interface OnAuthenticationFailed {
        void onFailed(HttpResponse response,String url);
    }
    public interface OnHttpResponse {
        void onResponse(HttpResponse response);
    }
    public interface OnHttpResponseTable {
        void onResponse(DataTable response);
    }

    private static OnAuthenticationFailed onAuthenticationFailed;
    public static OnAuthenticationFailed getOnAuthenticationFailed() {
        return onAuthenticationFailed;
    }
    public static void setOnAuthenticationFailed(OnAuthenticationFailed AuthenticationFailed) {
        onAuthenticationFailed = AuthenticationFailed;
    }

    private static String[] httpResponseMessageTitle = new String[]{"Message","error_description","error"};
    public static String[] getHttpResponseMessageTitle() {
        return httpResponseMessageTitle;
    }

    public static void setHttpResponseMessageTitle(String[] httpResponseMessageTitle) {
        HttpConfig.httpResponseMessageTitle = httpResponseMessageTitle;
    }

}
