package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Receiver;

public interface ReceiverService {

    List<Receiver> getListReceiver(Map<String, Object> condition);

    void deleteInfor(Map<String, Object> condition);

    void insertInforWarning(Map<String, Object> condition);

    List<String> getWarningsInfor(Map<String, Object> condition);

    void addNewReceiver(Map<String, Object> condition);

    void updateReceiver(Receiver receiver);

    void deleteReceiver(Map<String, Object> condition);
}
