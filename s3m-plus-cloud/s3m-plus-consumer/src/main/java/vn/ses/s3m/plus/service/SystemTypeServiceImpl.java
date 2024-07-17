package vn.ses.s3m.plus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.SystemTypeMapper;
import vn.ses.s3m.plus.dto.SystemType;

@Service
public class SystemTypeServiceImpl implements SystemTypeService {

    @Autowired
    private SystemTypeMapper systemTypeMapper;

    /**
     * Danh sách các loại hệ thống
     */
    @Override
    public List<SystemType> getSystemTypes() {
        return systemTypeMapper.getSystemTypes();
    }

    /**
     * Lấy thông tin loại hệ thống theo Id
     */
    @Override
    public SystemType getSystemTypeById(final Integer systemTypeId) {
        return systemTypeMapper.getSystemTypeById(systemTypeId);
    }

}
