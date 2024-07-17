package vn.ses.s3m.plus.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SMSBrandCCApi {

    private static Log log = LogFactory.getLog(SMSBrandCCApi.class);

    private static SMSBrandCCApi smsApi;

    public static SMSBrandCCApi getInstance() {
        if (smsApi == null) {
            synchronized (SMSBrandCCApi.class) {
                if (null == smsApi) {
                    try {
                        smsApi = new SMSBrandCCApi();

                    } catch (Exception e) {
                        log.error("Could not found properties file or reference attributes is not exist.");
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }

        return smsApi;
    }

    public int sendMT(String message, String url) {
        int result = -1;
        long startTime = System.currentTimeMillis();

        PostMethod post = null;
        try {
            HttpClient httpclient = new HttpClient();
            HttpConnectionManager conMgr = httpclient.getHttpConnectionManager();
            HttpConnectionManagerParams conPars = conMgr.getParams();
            conPars.setConnectionTimeout(20000);
            conPars.setSoTimeout(60000);
            post = new PostMethod(url);

            RequestEntity entity = new StringRequestEntity(message, "text/xml", "UTF-8");
            post.setRequestEntity(entity);
            post.setRequestHeader("SOAPAction", "");
            httpclient.executeMethod(post);
            InputStream is = post.getResponseBodyAsStream();
            String response = null;
            if (is != null) {
                response = getStringFromInputStream(is);
            }
            log.info("Call sendMT response: " + response);

            if (response != null && !response.equals("")) {
                if (response.contains("<result>")) {
                    int start = response.indexOf("<result>") + "<result>".length();
                    int end = response.lastIndexOf("</result>");
                    String responseCode = response.substring(start, end);
                    if (responseCode.equalsIgnoreCase("1")) {
                        result = 0; // call success
                    }
                }
            }

            is.close();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }

        log.info("Finish sendMT in " + (System.currentTimeMillis() - startTime) + " ms");
        return result;
    }

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ( (line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }

        return sb.toString();
    }
}
