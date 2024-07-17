package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.MaterialValue;

import java.util.List;
import java.util.Map;

public interface MaterialValueService {
    public List<MaterialValue> getMaterialValueByProjectAndType(Map<String, Object> con);

    public void addMaterialValue(MaterialValue value);
    public void updateMaterialValue(MaterialValue value);
}
