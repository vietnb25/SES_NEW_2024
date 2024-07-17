package vn.ses.s3m.plus.common;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmsConsumer {

    private static Log log = LogFactory.getLog(SmsConsumer.class);

    private static SmsConsumer smsConsumer;

    public static SmsConsumer getInstance() {
        if (smsConsumer == null) {
            synchronized (SmsConsumer.class) {
                if (null == smsConsumer) {
                    try {
                        smsConsumer = new SmsConsumer();
                    } catch (Exception e) {
                        log.error("Could not found properties file or reference attributes is not exist.");
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }
        return smsConsumer;
    }

    public void sendSms(String otp, String message, String phoneNumber, String url) {
        message = message.replace(Constants.PARAMETER_01, phoneNumber);
        message = message.replace(Constants.PARAMETER_02, otp);
        try {
            log.info("[SMS] Send: " + message);
            int result = SMSBrandCCApi.getInstance()
                .sendMT(message, url);
            log.info("[SMS] result: " + String.valueOf(result));
        } catch (Exception e) {
            log.error("[SMS] Error occured when sending sms");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
