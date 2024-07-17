package vn.ses.s3m.plus.pv.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Receiver;

@Mapper
public interface ReceiverPVMapper {

    List<Receiver> getListReceiverPV(Map<String, Object> condition);

    void deleteInforByReceiverIdAndDeviceIdPV(Map<String, Object> condition);

    void insertInforWarningPV(Map<String, Object> condition);

    List<String> getWarningsInforPV(Map<String, Object> condition);

    void addNewReceiverPV(Map<String, Object> condition);

    void updateReceiverPV(Receiver receiver);

    void deleteReceiverPV(Map<String, Object> condition);
}
