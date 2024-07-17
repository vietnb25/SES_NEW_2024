package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.MaterialValueMapper;
import vn.ses.s3m.plus.dto.MaterialType;
import vn.ses.s3m.plus.dto.MaterialValue;

import java.util.List;
import java.util.Map;

@Service
public class MaterialValueServiceImpl implements  MaterialValueService{

    @Autowired
    private MaterialValueMapper mapper;

    @Override
    public List<MaterialValue> getMaterialValueByProjectAndType(Map<String, Object> con) {
        return this.mapper.getMaterialValueByProjectAndType(con);
    }

    @Override
    public void addMaterialValue(MaterialValue value) {
        this.mapper.addMaterialValue(value);
        this.mapper.addMaterialValueHistory(value);
    }


    @Override
    public void updateMaterialValue(MaterialValue value) {
        this.mapper.updateMaterialValue(value);
        this.mapper.addMaterialValueHistory(value);
    }
}
