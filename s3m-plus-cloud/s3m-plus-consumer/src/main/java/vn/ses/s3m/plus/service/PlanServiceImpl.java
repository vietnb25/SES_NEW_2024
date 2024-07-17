package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.PlanMapper;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.Manufacture;
import vn.ses.s3m.plus.dto.Plan;
import vn.ses.s3m.plus.response.PlanResponse;

import java.util.List;
import java.util.Map;

@Service
public class PlanServiceImpl implements PlanService{
    @Autowired
    private PlanMapper planMapper;
    @Override
    public List<Plan> getAllPlan(Map<String, Object> condition) {
        return planMapper.getAllPlan(condition);
    }

    @Override
    public void addPlan(String schema, Plan plan) {
        planMapper.addPlan(schema, plan);
    }

    @Override
    public void updatePlan(String schema, Plan plan) {
        planMapper.updatePlan(schema, plan);
    }

    @Override
    public Plan getPlanById(String schema, Integer id) {
        return planMapper.getPlanById(schema, id);
    }

    @Override
    public void deletePlanById(String schema, Integer id) {
        planMapper.deletePlanById(schema, id);
    }
}
