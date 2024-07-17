package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.LandmarksPlansEnergyMapper;
import vn.ses.s3m.plus.dto.DataPower;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;
import vn.ses.s3m.plus.form.LandmarksPlansEnergyForm;

@Service
public class LandmarksPlanssEnergyServiceImpl implements LandmarksPlanssEnergyService {

    @Autowired
    private LandmarksPlansEnergyMapper mapper;

    @Override
    public List<LandmarksPlansEnergy> getListDataLandmarks(Map<String, String> condition) {
        return this.mapper.getListDataLandmarks(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getListDataPlans(Map<String, String> condition) {
        return this.mapper.getListDataPlans(condition);
    }

    @Override
    public void insertPlans(Map<String, String> condition) {
        this.mapper.insertPlans(condition);
    }

    @Override
    public void insertLandmarks(Map<String, String> condition) {
        this.mapper.insertLandmarks(condition);
    }

    @Override
    public void updatePlans(LandmarksPlansEnergyForm form) {
        this.mapper.updatePlans(form);
    }

    @Override
    public void updateLandmarks(LandmarksPlansEnergyForm form) {
        this.mapper.updateLandmarks(form);
    }

    @Override
    public void deleteStatusPlans(Map<String, String> condition) {

    }

    @Override
    public void deleteStatusLandmarks(Map<String, String> condition) {

    }

    @Override
    public void deletePlans(Map<String, String> condition) {

    }

    @Override
    public void deleteLandmarks(Map<String, String> condition) {

    }

    @Override
    public Double getEnergyByDayAndMonth(Map<String, String> condition) {
        return mapper.getEnergyByDayAndMonth(condition);
    }

    @Override
    public Double getLandmarksEnergyByDayAndMonth(Map<String, String> condition) {
        return mapper.getLandmarksEnergyByDayAndMonth(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getEnergyMonth(Map<String, String> condition) {
        return mapper.getEnergyMonth(condition);
    }

    @Override
    public List<DataPower> getEnergyMonthByDataPower(Map<String, String> condition) {
        return mapper.getEnergyMonthByDataPower(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getEnergyMonthPlan(Map<String, String> condition) {
        return mapper.getEnergyMonthPlan(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getEnergyYear(Map<String, String> condition) {
        return mapper.getEnergyYear(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getEnergyYearPlan(Map<String, String> condition) {
        return mapper.getEnergyYearPlan(condition);
    }
}
