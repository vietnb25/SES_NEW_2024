package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Receiver;

public interface ReceiverGridService {

    List<Receiver> getListReceiverGrid(Map<String, Object> condition);

    void deleteInforGrid(Map<String, Object> condition);

    void insertInforWarningGrid(Map<String, Object> condition);

    List<String> getWarningsInforGrid(Map<String, Object> condition);

    void addNewReceiverGrid(Map<String, Object> condition);

    void updateReceiverGrid(Receiver receiver);

    void deleteReceiverGrid(Map<String, Object> condition);
}
