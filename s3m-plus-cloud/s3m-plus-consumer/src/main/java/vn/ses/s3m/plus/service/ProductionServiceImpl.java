package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ProductionMapper;
import vn.ses.s3m.plus.dto.Production;

@Service
public class ProductionServiceImpl implements ProductionService {

    @Autowired
    private ProductionMapper productionMapper;

    /**
     * Lấy ra danh sách sản phẩm. Condition: schema khách hàng, projectId mã dự án
     *
     * @return danh sách sản phẩm.
     */
    @Override
    public List<Production> getListProduction(Map<String, Object> condition) {
        return productionMapper.getListProduction(condition);
    }

    @Override
    public List<Production> getListProductionStep(Map<String, Object> condition) {
        return productionMapper.getListProductionStep(condition);
    }

    @Override
    public Production getNewProductionStep(Map<String, Object> condition) {
        return this.productionMapper.getNewProductionStep(condition);
    }

    @Override
    public void addProduction(String schema, Production production) {
        productionMapper.addProduction(schema, production);
    }

    @Override
    public void addProductionStep(String schema, Production production) {
        productionMapper.addProductionStep(schema, production);
    }
}
