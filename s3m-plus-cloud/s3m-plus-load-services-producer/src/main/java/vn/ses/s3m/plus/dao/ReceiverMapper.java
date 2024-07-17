package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Receiver;

@Mapper
public interface ReceiverMapper {

    List<Receiver> getListReceiverLoad(Map<String, Object> condition);

    void deleteInforByReceiverIdAndDeviceIdLoad(Map<String, Object> condition);

    void insertInforWarningLoad(Map<String, Object> condition);

    List<String> getWarningsInforLoad(Map<String, Object> condition);

    void addNewReceiverLoad(Map<String, Object> condition);

    void updateReceiverLoad(Receiver receiver);

    void deleteReceiverLoad(Map<String, Object> condition);
}
