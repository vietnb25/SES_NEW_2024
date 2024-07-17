package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ReceiverWarningMapper;
import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.dto.WarningInfor;

/**
 * Xử lý lấy thông tin người nhận cảnh báo từ SES
 *
 * @since 2023-10-07
 */
@Service
public class ReceiverServiceImpl implements ReceiverService {
    @Autowired
    ReceiverWarningMapper receiverMapper;

    @Override
    public List<Receiver> getListReceiver(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverMapper.getListReceiver(condition);
    }

    @Override
    public void deleteInfor(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.deleteInforByReceiverIdAndDeviceId(condition);
    }

    @Override
    public void insertInforWarning(List<WarningInfor> datas) {
        // TODO Auto-generated method stub
        receiverMapper.insertInforWarning(datas);
    }

    @Override
    public List<String> getWarningsInfor(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverMapper.getWarningsInfor(condition);
    }

    @Override
    public void addNewReceiver(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.addNewReceiver(condition);
    }

    @Override
    public void updateReceiver(Receiver receiver) {
        // TODO Auto-generated method stub
        receiverMapper.updateReceiver(receiver);
    }

    @Override
    public void deleteReceiver(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.deleteReceiver(condition);
    }
}
