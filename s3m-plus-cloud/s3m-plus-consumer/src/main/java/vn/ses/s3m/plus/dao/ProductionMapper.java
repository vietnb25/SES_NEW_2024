package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Production;

@Mapper
public interface ProductionMapper {
    List<Production> getListProduction(Map<String, Object> condition);

    List<Production> getListProductionStep(Map<String, Object> condition);
    Production getNewProductionStep(Map<String, Object> condition);

    void addProduction(String schema, Production production);

    void addProductionStep(String schema, Production production);
}
