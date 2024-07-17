package vn.ses.s3m.plus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UnitMapper;
import vn.ses.s3m.plus.dto.Unit;

@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitMapper unitMapper;

    /**
     * Lấy ra danh sách cài đặt.
     *
     * @return danh sách cài đặt.
     */
    @Override
    public List<Unit> getListUnit() {
        return unitMapper.getListUnit();
    }
}
