package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.dto.WarningInfor;

@Mapper
public interface ReceiverWarningMapper {

    List<Receiver> getListReceiver(Map<String, Object> condition);

    void deleteInforByReceiverIdAndDeviceId(Map<String, Object> condition);

    void insertInforWarning(List<WarningInfor> datas);

    List<String> getWarningsInfor(Map<String, Object> condition);

    void addNewReceiver(Map<String, Object> condition);

    void updateReceiver(Receiver receiver);

    void deleteReceiver(Map<String, Object> condition);
}
