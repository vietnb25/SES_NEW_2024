package vn.ses.s3m.plus.grid.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Receiver;

@Mapper
public interface ReceiverGridMapper {
    List<Receiver> getListReceiverGrid(Map<String, Object> condition);

    void deleteInforByReceiverIdAndDeviceIdGrid(Map<String, Object> condition);

    void insertInforWarningGrid(Map<String, Object> condition);

    List<String> getWarningsInforGrid(Map<String, Object> condition);

    void addNewReceiverGrid(Map<String, Object> condition);

    void updateReceiverGrid(Receiver receiver);

    void deleteReceiverGrid(Map<String, Object> condition);
}
