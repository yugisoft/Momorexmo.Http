package momorexmo.module.http;

import android.os.AsyncTask;

import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import momorexmo.module.AppRichActivity;
import momorexmo.module.AppSettings;
import momorexmo.module.DataTable;
import momorexmo.module.Interfaces.ASerializable;
import momorexmo.module.Interfaces.ISerializable;
import momorexmo.module.Interfaces.OnResponse;
import momorexmo.module.Utils.JsonConverter;
import momorexmo.module.Utils.parse;

public class HttpRequest extends AsyncTask<String,Void,HttpResponse>
{
    //region static
    public static class Headers {
        public static String DeviceInfoKey = "ziraDeviceInfo",ConstInfoKey="Config";


        public static void urlencoded(HttpRequestBase req) {
            req.setHeader("Content-Type", "application/x-www-form-urlencoded");
            req.setHeader("Accept", "application/json");
            req.setHeader("Authorization", parse.Join("{0}{1}",HttpConfig.getTokenType(),HttpConfig.getToken()));
        }

        public static void json(HttpRequestBase req) {
            req.setHeader("Content-Type", "application/json");
            req.setHeader("Accept", "application/json");
            req.setHeader("Authorization", parse.Join("{0}{1}",HttpConfig.getTokenType(),HttpConfig.getToken()));
        }

        private static void DeviceInfo(HttpRequestBase req) {
            try
            {
                if (HttpConfig.isAddHttpReqestWithDeviceInfo() && AppRichActivity.getDeviceInfo()!= null)
                  req.setHeader(DeviceInfoKey, JsonConverter.convertToJson(AppRichActivity.getDeviceInfo()));

            }
            catch (Exception ex)
            {}
        }

        private static void ConstHttpHeader(HttpRequestBase req) {
            try
            {
                if (HttpConfig.getConstHttpHeader() != null)
                  req.setHeader(ConstInfoKey, JsonConverter.convertToJson(HttpConfig.getConstHttpHeader()));

            }
            catch (Exception ex)
            {

            }
        }

        private static void TempHttpHeader(HttpRequestBase req) {
            try
            {
                if (HttpConfig.getTempHttpHeader() != null)
                    req.setHeader("TempHttpHeader", JsonConverter.convertToJson(HttpConfig.getTempHttpHeader()));
                HttpConfig.setTempHttpHeader(null);
            }
            catch (Exception ex)
            {}
        }

        public static void Add(HttpRequestBase req, Hashtable headers) {
            if (HttpConfig.getConstHtppHashHeader() != null)
            {
                Hashtable hashtable = HttpConfig.getConstHtppHashHeader().getItem();
                Iterator<String> keys = hashtable.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    req.setHeader(key, hashtable.get(key).toString());
                }
            }
            if (headers==null)return;
            try
            {
                Iterator<String> keys = headers.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();

                    req.setHeader(key, headers.get(key).toString());
                }
            }
            catch (Exception ex)
            {}
        }


    }
    public static class Bodys {

        public static String Add(HttpEntityEnclosingRequestBase req, Hashtable bodys) {
            String log = "";
            if (bodys==null)return log;
            try
            {
                Iterator<String> keys = bodys.keySet().iterator();

                String body = "";
                int i = 0;
                while (keys.hasNext())
                {
                    String key = keys.next();
                    if (i > 0) body += "&";
                    body += key + "=" + bodys.get(key).toString();
                    i++;
                }
                log = body;
                StringEntity se = null;
                try {
                    se = new StringEntity(body, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                req.setEntity(se);
            }
            catch (Exception Ex)
            {
                log += Ex.getMessage();
            }
            return log;
        }
        public static void Add(HttpEntityEnclosingRequestBase req, String bodys) {

            try
            {
                StringEntity se = new StringEntity(bodys.toString(), HTTP.UTF_8);
                se.setContentType("application/json");
                req.setEntity(se);
            }
            catch (Exception ex)
            {

            }
        }
        public static void Add(HttpEntityEnclosingRequestBase req, Object object) {

            try
            {
                if ( object instanceof ISerializable)
                    Add(req,((ISerializable)object).Serialize());
                else
                    Add(req,JsonConverter.convertToJson(object));
            }
            catch (Exception ex)
            {

            }
        }
    }
    public static String UrlFormatter(String url) {
        try
        {
            URL urll= new URL(url);
            URI uri = new URI(urll.getProtocol(), urll.getUserInfo(), urll.getHost(), urll.getPort(), urll.getPath(), urll.getQuery(), urll.getRef());
            return uri.toString();
        }
        catch (Exception ex)
        {
            return  url;
        }

    }
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    public static HttpResponse isException(int statusCode) {

        HttpResponse response = new HttpResponse();
        response.HataKodu = statusCode;
        response.isException = !(statusCode >= 200 && statusCode < 300);


        switch (statusCode)
        {
            //     case 400:
            //         Hata.isException = true;
            //         Hata.HataAciklama = "Sunucuya Yapılan İstek Hatalıdır";
            //         break;
            //     case 401:
            //         Hata.isException = true;
            //         Hata.HataAciklama = "Oturumunuzun Süresi Dolmuş! Lütfen Tekrar Giriş Yapınız.";
            //         break;
            case 404:
                response.isException = true;
                response.HataAciklama = "İstek Yapılan Kaynak Veya Sayfa Bulunamadı! Lütfen Server Adresini Doğru Girdiğinizden Emin Olun!";
                break;
            //     case 408:
            //         Hata.isException = true;
            //         Hata.HataAciklama = "İstek Zaman Aşımına Uğradı! Lütfen İnternet Bağlantınızı Kontrol Edin.";
            //         break;
            //     case 410:
            //         Hata.isException = true;
            //         Hata.HataAciklama = "Ulaşmaya Çalıştığınız Sayfa Veya Kaynak Artık Mevcut Değil!";
            //         break;
            //     case 413:
            //         Hata.isException = true;
            //         Hata.HataAciklama = "İsteğin boyutu çok büyük olduğu için işlenemedi!";
            //         break;
            //     case 414:
            //         Hata.isException = true;
            //         Hata.HataAciklama = "İstek Adresi Fazla Uzun!";
            //         break;
            //     default:
            //         Hata.isException = false;
            //         Hata.HataAciklama = "Success";
            //         Hata.HataKodu = 0;
            //         break;
            //
        }
        return response;
    }
    public static HttpResponse httpExecute(HttpRequestBase httpGet,String log) {
        String LOG ="httpExecuteResponse \n";
        HttpResponse response =null;
        try
        {
            InputStream inputStream = null;

            HttpClient httpclient = new DefaultHttpClient();
            org.apache.http.HttpResponse httpResponse = httpclient.execute(httpGet);

            StatusLine st = httpResponse.getStatusLine();

            response = isException(st.getStatusCode());
            if (HttpConfig.getOnAuthenticationFailed()!=null && response.HataKodu == 401)
                HttpConfig.getOnAuthenticationFailed().onFailed(response,httpGet.getURI().toURL().toString());
            LOG+= "HttpURL : "+httpGet.getURI().toURL()+"\n";
            LOG+=log+"\n";
            LOG+= "HttpReponseStatusCode : "+st.getStatusCode()+"\n";

            //if (!response.isException || st.getStatusCode() == 400) { try { } catch (Exception Ex) { } }
            if (st.getStatusCode() != 204)
            {
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                {
                    response.Data = convertInputStreamToString(inputStream);
                    //if (st.getStatusCode() == 400) {}
                    DataTable dt = new DataTable(response.Data);

                    String error  ="";
                    for (int i = 0 ; i < HttpConfig.getHttpResponseMessageTitle().length;i++)
                    {
                        error = dt.get(0,HttpConfig.getHttpResponseMessageTitle()[i]);
                        if (error.length()>0)
                            break;
                    }
                    response.HataAciklama = error.length()>0 ?error : response.HataAciklama;
                }
                else
                {
                    response.Data = "Beklenmeyen Bir Hata Oluştu!";
                }
            }
        }
        catch (Exception ex)
        {
            response = new HttpResponse();
            response.HataKodu = -999;
            response.isException = true;
            response.HataAciklama=ex.getMessage();

        }
        LOG += "Response : "+(response.Data.length() != 0 ? response.Data : response.getMessage());
        AppSettings.Print("I", "httpExecuteResponse", LOG);
        return  response;
    }
    //endregion

    private OnHttpResponse onHttpResponse = null;
    private Hashtable headers=null,bodys=null;
    private String sbody="";
    private boolean Json = false;
    private String Log = "";
    private int timeOut = 0;
    public static int defaultTimeOut = 0;
    HttpRequestBase httpRequest;
    protected boolean isFilePost = false;




    //region Constr
    public HttpRequest() {
        onHttpResponse=null;
        timeOut = defaultTimeOut;
    }
    public HttpRequest(OnHttpResponse pOnHttpResponse) {
        onHttpResponse=pOnHttpResponse;
        timeOut = defaultTimeOut;
    }
    public HttpRequest(OnHttpResponse pOnHttpResponse,Hashtable pBodys) {
        onHttpResponse=pOnHttpResponse;
        bodys=pBodys;
        timeOut = defaultTimeOut;
    }
    public HttpRequest(OnHttpResponse pOnHttpResponseTable,String pBodys,Hashtable pheaders) {
        onHttpResponse=pOnHttpResponseTable;
        sbody=pBodys;
        headers=pheaders;
        Json=true;
        timeOut = defaultTimeOut;
    }
    public HttpRequest(OnHttpResponse pOnHttpResponse,String pBodys) {
        onHttpResponse=pOnHttpResponse;
        sbody=pBodys;
        Json=true;
        timeOut = defaultTimeOut;
    }
    public HttpRequest(OnHttpResponse pOnHttpResponseTable,Hashtable pBodys,Hashtable pheaders) {
        onHttpResponse=pOnHttpResponseTable;
        bodys=pBodys;
        headers=pheaders;
        timeOut = defaultTimeOut;
    }
    //endregion
    //region GETTER
    public int getTimeOut() {
        return timeOut;
    }
    public Hashtable getBodys() {
        return bodys;
    }
    public Hashtable getHeaders() {
        return headers;
    }
    public HttpRequestBase getHttpRequest() {
        return httpRequest;
    }
    public static int getDefaultTimeOut() {
        return defaultTimeOut;
    }
    public OnHttpResponse getOnHttpResponse() {
        return onHttpResponse;
    }
    public String getLog() {
        return Log;
    }
    public String getSbody() {
        return sbody;
    }
    //endregion
    //region SETTER
    public HttpRequest setOnHttpResponse(OnHttpResponse onHttpResponse) {
        this.onHttpResponse = onHttpResponse;
        return  this;
    }
    public HttpRequest setHeaders(Hashtable headers) {
        this.headers = headers;
        return  this;
    }
    public HttpRequest setBodys(Hashtable bodys) {
        this.bodys = bodys;
        if (bodys.size()>0)
            Json = false;
        return  this;
    }
    public HttpRequest setBodys(String sbody) {
        this.sbody = sbody;
        if (sbody.length()>0)
            Json = true;
        return  this;
    }
    public HttpRequest setLog(String log) {
        Log = log;
        return  this;
    }
    public HttpRequest setJson(boolean json) {
        Json = json;
        return this;
    }
    public HttpRequest setHttpRequest(HttpRequestBase httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }
    public HttpRequest setSbody(String sbody) {
        this.sbody = sbody;
        return this;
    }
    public HttpRequest setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return  this;
    }
    public HttpRequest setTimeOutSecond(int timeOut) {
        this.timeOut = timeOut * 1000;
        return  this;
    }

    public <T> HttpRequest JsonTo(final OnResponse<T> listener, final Class cl) {


        setOnHttpResponse(new OnHttpResponse() {
            @Override
            public void onResponse(HttpResponse response) {
                if (response.isException)
                {
                    if (onHttpResponse!= null)
                        onHttpResponse.onResponse(response);
                }
                else
                {
                    if (listener!=null)
                    {
                        listener.onResponse((T) JsonConverter.convertJsonToModel(response.Data,cl));
                    }
                }
            }
        });
        return this;
    }
    public <T> HttpRequest JsonToList(final OnResponse<List<T>> listener, final Class<T> cl) {


        setOnHttpResponse(new OnHttpResponse() {
            @Override
            public void onResponse(HttpResponse response) {
                if (response.isException)
                {
                    if (onHttpResponse!= null)
                        onHttpResponse.onResponse(response);
                }
                else
                {
                    if (listener!=null)
                    {
                        listener.onResponse((List<T>) JsonConverter.convertJsonToList(response.Data,cl));
                    }
                }
            }
        });
        return  this;
    }
    //endregion

    //region Http Request Async
    public void GET(String url)
    {
        Execute(new HttpGet(UrlFormatter(url)));
    }
    public void DELETE(String url) {
        Execute(new HttpDelete(UrlFormatter(url)));
    }
    public void HEAD(String url) {
        Execute(new HttpHead(UrlFormatter(url)));
    }
    public void OPTIONS(String url) {
        Execute(new HttpOptions(UrlFormatter(url)));
    }
    public void POST(String url) {
        Execute(new HttpPost(UrlFormatter(url)));
    }
    public void PUT(String url) {
        Execute(new HttpPut(UrlFormatter(url)));
    }
    public void POST_FILE(String url, File[] files) {
        if (files.length>0)
        {
            String postReceiverUrl = url;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverUrl);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (File file : files)
                reqEntity.addPart(file.getName(), new FileBody(file));
            httpPost.setEntity(reqEntity);
            httpPost.addHeader("Authorization",parse.Join("{0}{1}",HttpConfig.getTokenType(),HttpConfig.getToken()));
            setHttpRequest(httpPost);
            super.execute();
        }
    }


    public void Execute(HttpEntityEnclosingRequestBase http) {

        //Content Type Belirlendi!
        if (!Json)
        {
            Headers.urlencoded(http);
            Log += "\n"+Bodys.Add(http,bodys);
        }
        else
        {
            Headers.json(http);
            Log += "\n"+sbody.replace("{","\n{").replace("}","\n}");
            Bodys.Add(http,sbody);
        }

        Execute(((HttpRequestBase)(http)));
    }
    public void Execute(HttpRequestBase http) {

        if (Log.equals(""))
        {
            Log = "http-"+http.getMethod();
            if (!Json)
                Headers.urlencoded(http);
        }
        Headers.ConstHttpHeader(http);
        Headers.TempHttpHeader(http);
        Headers.DeviceInfo(http);
        Headers.Add(http, headers);
        httpRequest = http;

        if (timeOut > 0)
            HttpConnectionParams.setConnectionTimeout(http.getParams(),timeOut);
        this.execute();
    }
    //endregion
    //region Http Request Sync
    public HttpResponse GET_Sync(String url) {
        return Execute_Sync(new HttpGet(UrlFormatter(url)));
    }
    public HttpResponse DELETE_Sync(String url) {
        return  Execute_Sync(new HttpDelete(UrlFormatter(url)));
    }
    public HttpResponse HEAD_Sync(String url) {
        return  Execute_Sync(new HttpHead(UrlFormatter(url)));
    }
    public HttpResponse OPTIONS_Sync(String url) { return Execute_Sync(new HttpOptions(UrlFormatter(url)));
    }
    public HttpResponse POST_Sync(String url) {
        return Execute_Sync(new HttpPost(UrlFormatter(url)));
    }
    public HttpResponse PUT_Sync(String url) {
        return  Execute_Sync(new HttpPut(UrlFormatter(url)));
    }
    public HttpResponse POST_FILE_Sync(String url, File[] files) {
        HttpPost httpPost = new HttpPost(UrlFormatter(url));

        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (File file : files)
            reqEntity.addPart(file.getName(), new FileBody(file));
        httpPost.setEntity(reqEntity);

        return httpExecute(((HttpRequestBase)(httpPost)),"Http File Post");
    }

    public HttpResponse Execute_Sync(HttpEntityEnclosingRequestBase http) {

        //Content Type Belirlendi!
        if (!Json)
        {
            Headers.urlencoded(http);
            Log += "\n"+Bodys.Add(http,bodys);
        }
        else
        {
            Headers.json(http);
            Log += "\n"+sbody.replace("{","\n{").replace("}","\n}");
            Bodys.Add(http,sbody);
        }

        return Execute_Sync(((HttpRequestBase)(http)));
    }
    public HttpResponse Execute_Sync(HttpRequestBase http) {

        if (Log.equals(""))
        {
            Log = "http-"+http.getMethod();
            if (!Json)
                Headers.urlencoded(http);
        }
        Headers.ConstHttpHeader(http);
        Headers.TempHttpHeader(http);
        Headers.DeviceInfo(http);
        Headers.Add(http, headers);
        httpRequest = http;

        if (timeOut > 0)
            HttpConnectionParams.setConnectionTimeout(http.getParams(),timeOut);
        return httpExecute(httpRequest,Log);
    }
    //endregion

    @Override
    protected HttpResponse doInBackground(String... strings) {
        return httpExecute(httpRequest,Log);
    }

    @Override protected void   onPostExecute(HttpResponse response) {
        super.onPostExecute(response);
        if (onHttpResponse!=null)
            onHttpResponse.onResponse(response);
    }
}
