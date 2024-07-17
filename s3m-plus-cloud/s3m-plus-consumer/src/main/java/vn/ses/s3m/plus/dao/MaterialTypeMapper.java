package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.MaterialType;

import java.util.List;

@Mapper
public interface MaterialTypeMapper {

    public List<MaterialType> getListMaterialType();
    public void addMaterialType(MaterialType type);
    public void updateMaterialType(MaterialType type);
}
