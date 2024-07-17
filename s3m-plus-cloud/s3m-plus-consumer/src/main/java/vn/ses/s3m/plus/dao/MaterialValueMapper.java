package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.MaterialValue;

import java.util.List;
import java.util.Map;

@Mapper
public interface MaterialValueMapper {
    public List<MaterialValue> getMaterialValueByProjectAndType(Map<String, Object> con);

    public void addMaterialValue(MaterialValue value);
    public void addMaterialValueHistory(MaterialValue value);
    public void updateMaterialValue(MaterialValue value);
}
