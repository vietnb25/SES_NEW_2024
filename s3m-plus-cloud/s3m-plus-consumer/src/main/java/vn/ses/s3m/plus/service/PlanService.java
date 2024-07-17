package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.Manufacture;
import vn.ses.s3m.plus.dto.Plan;
import vn.ses.s3m.plus.dto.Warning;

import java.util.List;
import java.util.Map;

public interface PlanService {
    List<Plan> getAllPlan(Map<String, Object> condition);

    void addPlan(String schema, Plan plan);

    void updatePlan(String schema, Plan plan);

    Plan getPlanById(String schema, Integer id);

    void deletePlanById(String schema, Integer id);


}
