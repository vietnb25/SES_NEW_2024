package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataPower;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;
import vn.ses.s3m.plus.form.LandmarksPlansEnergyForm;

@Mapper
public interface LandmarksPlansEnergyMapper {
    List<LandmarksPlansEnergy> getListDataLandmarks(Map<String, String> condition);

    List<LandmarksPlansEnergy> getListDataPlans(Map<String, String> condition);

    void insertPlans(Map<String, String> condition);

    void insertLandmarks(Map<String, String> condition);

    void updatePlans(LandmarksPlansEnergyForm form);

    void updateLandmarks(LandmarksPlansEnergyForm form);

    void deleteStatusPlans(Map<String, String> condition);

    void deleteStatusLandmarks(Map<String, String> condition);

    void deletePlans(Map<String, String> condition);

    void deleteLandmarks(Map<String, String> condition);

    Double getEnergyByDayAndMonth(Map<String, String> condition);

    Double getLandmarksEnergyByDayAndMonth(Map<String, String> condition);

    List<LandmarksPlansEnergy> getEnergyMonth(Map<String, String> condition);

    List<DataPower> getEnergyMonthByDataPower(Map<String, String> condition);

    List<LandmarksPlansEnergy> getEnergyMonthPlan(Map<String, String> condition);

    List<LandmarksPlansEnergy> getEnergyYear(Map<String, String> condition);

    List<LandmarksPlansEnergy> getEnergyYearPlan(Map<String, String> condition);
}
