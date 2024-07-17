package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.Manufacture;
import vn.ses.s3m.plus.dto.Plan;

import java.util.List;
import java.util.Map;
@Mapper
public interface PlanMapper {
    List<Plan> getAllPlan(Map<String, Object> conditon);

    void addPlan(String schema, Plan plan);

    void updatePlan(String schema, Plan plan);

    void deletePlanById(String schema, Integer id);

    Plan getPlanById(String schema, Integer id);
}
