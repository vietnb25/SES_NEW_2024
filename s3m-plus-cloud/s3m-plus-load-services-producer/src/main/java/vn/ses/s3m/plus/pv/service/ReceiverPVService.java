package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Receiver;

public interface ReceiverPVService {

    List<Receiver> getListReceiverPV(Map<String, Object> condition);

    void deleteInforPV(Map<String, Object> condition);

    void insertInforWarningPV(Map<String, Object> condition);

    List<String> getWarningsInforPV(Map<String, Object> condition);

    void addNewReceiverPV(Map<String, Object> condition);

    void updateReceiverPV(Receiver receiver);

    void deleteReceiverPV(Map<String, Object> condition);
}
