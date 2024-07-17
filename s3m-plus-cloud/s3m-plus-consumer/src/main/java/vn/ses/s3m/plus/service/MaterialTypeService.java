package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.MaterialType;

import java.util.List;

public interface MaterialTypeService {
    public List<MaterialType> getListMaterialType();
    public void addMaterialType(MaterialType type);
    public void updateMaterialType(MaterialType type);
}
