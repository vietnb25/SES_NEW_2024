package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Production;

public interface ProductionService {
    List<Production> getListProduction(Map<String, Object> condition);

    List<Production> getListProductionStep(Map<String, Object> condition);

    Production getNewProductionStep(Map<String, Object> condition);

    void addProduction(String schema, Production production);

    void addProductionStep(String schema, Production production);

}
