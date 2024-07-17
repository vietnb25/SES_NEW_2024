package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataPqs;
import vn.ses.s3m.plus.dto.Manufacture;

public interface ManufactureService {

    List<Manufacture> getManufactures(Map<String, Object> condition);

    List<DataPqs> getDataPqsManufactures(Map<String, Object> condition);

    void addManufactures(String schema, Manufacture manufactures);

    void addViewTimeManufactures(String schema, Manufacture manufactures);

    void updateManufactures(String schema, Manufacture manufactures);

    List<Manufacture> exportManufactures(Map<String, Object> condition);
}
