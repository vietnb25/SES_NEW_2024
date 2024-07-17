package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.grid.dao.ReceiverGridMapper;

@Service
public class ReceiverGridServiceImpl implements ReceiverGridService {

    @Autowired
    ReceiverGridMapper receiverGridMapper;

    @Override
    public List<Receiver> getListReceiverGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverGridMapper.getListReceiverGrid(condition);
    }

    @Override
    public void deleteInforGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverGridMapper.deleteInforByReceiverIdAndDeviceIdGrid(condition);
    }

    @Override
    public void insertInforWarningGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverGridMapper.insertInforWarningGrid(condition);
    }

    @Override
    public List<String> getWarningsInforGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return receiverGridMapper.getWarningsInforGrid(condition);
    }

    @Override
    public void addNewReceiverGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverGridMapper.addNewReceiverGrid(condition);
    }

    @Override
    public void updateReceiverGrid(Receiver receiver) {
        // TODO Auto-generated method stub
        receiverGridMapper.updateReceiverGrid(receiver);
    }

    @Override
    public void deleteReceiverGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        receiverGridMapper.deleteReceiverGrid(condition);
    }

}
