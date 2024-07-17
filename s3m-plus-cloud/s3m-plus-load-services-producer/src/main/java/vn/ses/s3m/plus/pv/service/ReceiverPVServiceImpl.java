package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.pv.dao.ReceiverPVMapper;

@Service
public class ReceiverPVServiceImpl implements ReceiverPVService {

    @Autowired
    ReceiverPVMapper receiverMapper;

    @Override
    public List<Receiver> getListReceiverPV(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverMapper.getListReceiverPV(condition);
    }

    @Override
    public void deleteInforPV(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.deleteInforByReceiverIdAndDeviceIdPV(condition);
    }

    @Override
    public void insertInforWarningPV(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.insertInforWarningPV(condition);
    }

    @Override
    public List<String> getWarningsInforPV(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverMapper.getWarningsInforPV(condition);
    }

    @Override
    public void addNewReceiverPV(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.addNewReceiverPV(condition);
    }

    @Override
    public void updateReceiverPV(Receiver receiver) {
        // TODO Auto-generated method stub
        receiverMapper.updateReceiverPV(receiver);
    }

    @Override
    public void deleteReceiverPV(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverMapper.deleteReceiverPV(condition);
    }

}
