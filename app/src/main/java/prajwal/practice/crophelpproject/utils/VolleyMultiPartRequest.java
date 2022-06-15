package prajwal.practice.crophelpproject.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VolleyMultiPartRequest extends Request<NetworkResponse> {

    private static final String TAG = "CustomLogging";
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-"+System.currentTimeMillis();

    private final Response.Listener<NetworkResponse> listener;
    private final Response.ErrorListener errorListener;
    private Map<String, String> headers;




    public VolleyMultiPartRequest(
            int method,
            String url,
            Response.Listener<NetworkResponse> listener,
            Response.ErrorListener errorListener){
        super(method, url, errorListener);
        this.listener = listener;
        this.errorListener = errorListener;

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (headers != null) ? headers: super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary="+boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try{
//            Populate text payload
            Map<String, String> params = getParams();
            if(params != null && params.size() > 0){
                textParse(dos,params, getParamsEncoding());
            }
//            Populate data byte payload
            Map<String, DataPart> data = getByteData();
            if(data != null && data.size() > 0){
                dataParse(dos,data);
            }

//            close multpipart form data after the text and file data
            dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);
            return bos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "getBody: "+e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Custom method handle data payload
     *
     * @return Map data part label with data byte
     * @throws AuthFailureError
     */
    protected Map<String, DataPart> getByteData() throws AuthFailureError{
        return null;
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try{
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response)
            );

        }catch (Exception e){
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(error);
    }

    /**
     * Parse String map into data output stream by key and value
     * @param dataOutputStream
     * @param params
     * @param encoding
     * @throws IOException
     */
    private void textParse(
            DataOutputStream dataOutputStream,
            Map<String, String> params,
            String encoding)
            throws IOException{
        try{
            for(Map.Entry<String, String> entry : params.entrySet()){
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        }catch (UnsupportedEncodingException e){
            throw new RuntimeException("Encoding not supported: "+encoding, e);
        }

    }



    /**
     *
     * @param dataOutputStream
     * @param data
     * @throws IOException
     */
    private void dataParse(
            DataOutputStream dataOutputStream,
            Map<String, DataPart> data
    ) throws IOException{
        for(Map.Entry<String, DataPart> entry: data.entrySet()){
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }



    /**
     * Write String data into header and data output stream
     * @param dataOutputStream
     * @param key
     * @param value
     * @throws IOException
     */
    private void buildTextPart(
            DataOutputStream dataOutputStream,
            String key,
            String value
    ) throws IOException {
        dataOutputStream.writeBytes(twoHyphens+boundary+lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+key+"\""+lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(value+lineEnd);
    }

    /**
     *
     * @param dataOutputStream
     * @param value
     * @param key
     * @throws IOException
     */
    private void buildDataPart(
            DataOutputStream dataOutputStream,
            DataPart value,
            String key
    ) throws IOException {
        dataOutputStream.writeBytes(twoHyphens+boundary+lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                "key"+"\"; filename=\""+value.getFileName()+"\""+lineEnd);
        if (value.getType() != null && !value.getType().trim().isEmpty()){
            dataOutputStream.writeBytes("Content-Type: "+value.getType()+lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(value.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0 , bufferSize);

        while(bytesRead > 0){
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0 , bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    protected class DataPart{
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart() {
        }

        public DataPart(String fileName, byte[] content) {
            this.fileName = fileName;
            this.content = content;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }


}

