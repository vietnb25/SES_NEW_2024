package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ReceiverMapper;
import vn.ses.s3m.plus.dto.Receiver;

@Service
public class ReceiverServiceImpl implements ReceiverService {

    @Autowired
    ReceiverMapper receiverMapper;

    @Override
    public List<Receiver> getListReceiver(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverMapper.getListReceiverLoad(condition);
    }

    @Override
    public void deleteInfor(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.deleteInforByReceiverIdAndDeviceIdLoad(condition);
    }

    @Override
    public void insertInforWarning(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.insertInforWarningLoad(condition);
    }

    @Override
    public List<String> getWarningsInfor(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverMapper.getWarningsInforLoad(condition);
    }

    @Override
    public void addNewReceiver(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.addNewReceiverLoad(condition);
    }

    @Override
    public void updateReceiver(Receiver receiver) {
        // TODO Auto-generated method stub
        receiverMapper.updateReceiverLoad(receiver);
    }

    @Override
    public void deleteReceiver(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.deleteReceiverLoad(condition);
    }

}
