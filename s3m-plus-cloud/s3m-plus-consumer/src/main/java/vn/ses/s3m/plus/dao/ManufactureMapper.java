package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataPqs;
import vn.ses.s3m.plus.dto.Manufacture;

@Mapper
public interface ManufactureMapper {
    List<Manufacture> getManufactures(Map<String, Object> conditon);

    List<DataPqs> getDataPqsManufactures(Map<String, Object> condition);

    void addManufactures(String schema, Manufacture manufactures);

    void addViewTimeManufactures(String schema, Manufacture manufactures);

    void updateManufactures(String schema, Manufacture manufactures);

    List<Manufacture> exportManufactures(Map<String, Object> condition);
}
