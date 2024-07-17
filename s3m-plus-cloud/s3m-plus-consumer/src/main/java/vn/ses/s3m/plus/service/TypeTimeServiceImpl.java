package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.TypeTimeMapper;
import vn.ses.s3m.plus.dto.TypeTime;

import java.util.List;

@Service
public class TypeTimeServiceImpl implements TypeTimeService {
    @Autowired
    private TypeTimeMapper typeTimeMapper;
    @Override
    public List<TypeTime> getTypeTime() {
        return this.typeTimeMapper.getTypeTime();
    }
}
