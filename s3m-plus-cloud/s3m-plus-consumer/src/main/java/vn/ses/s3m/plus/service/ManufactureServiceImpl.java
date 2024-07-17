package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ManufactureMapper;
import vn.ses.s3m.plus.dto.DataPqs;
import vn.ses.s3m.plus.dto.Manufacture;

@Service
public class ManufactureServiceImpl implements ManufactureService {

    @Autowired
    private ManufactureMapper manufactureMapper;

    /**
     * Lấy ra danh sách theo dõi sản xuất.
     *
     * @return danh sách theo dõi sản xuất.
     */
    @Override
    public List<Manufacture> getManufactures(final Map<String, Object> condition) {
        return manufactureMapper.getManufactures(condition);
    }

    @Override
    public List<DataPqs> getDataPqsManufactures(Map<String, Object> condition) {
        return manufactureMapper.getDataPqsManufactures(condition);
    }

    /**
     * Thêm theo dõi sản xuất.
     *
     * @return theo dõi sản xuất đã được thêm vào db.
     */
    @Override
    public void addManufactures(String schema, Manufacture manufacture) {
        manufactureMapper.addManufactures(schema, manufacture);
    }

    @Override
    public void addViewTimeManufactures(String schema, Manufacture manufactures) {
        manufactureMapper.addViewTimeManufactures(schema, manufactures);
    }

    /**
     * Cập nhật theo dõi sản xuất.
     *
     * @return theo dõi sản xuất đã được cập nhật vào db.
     */
    @Override
    public void updateManufactures(String schema, Manufacture manufacture) {
        manufactureMapper.updateManufactures(schema, manufacture);
    }

    @Override
    public List<Manufacture> exportManufactures(Map<String, Object> condition) {
        return manufactureMapper.exportManufactures(condition);
    }
}
