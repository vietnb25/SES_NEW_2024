package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.MaterialTypeMapper;
import vn.ses.s3m.plus.dto.MaterialType;

import java.util.List;

@Service
public class MaterialTypeServiceImpl implements MaterialTypeService{

    @Autowired
    private MaterialTypeMapper materialTypeMapper;
    @Override
    public List<MaterialType> getListMaterialType() {
        return this.materialTypeMapper.getListMaterialType();
    }

    @Override
    public void addMaterialType(MaterialType type) {
        this.materialTypeMapper.addMaterialType(type);
    }

    @Override
    public void updateMaterialType(MaterialType type) {
        this.materialTypeMapper.updateMaterialType(type);
    }
}
