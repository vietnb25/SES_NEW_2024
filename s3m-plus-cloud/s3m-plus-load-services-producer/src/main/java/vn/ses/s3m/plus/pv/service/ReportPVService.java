package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Report;

public interface ReportPVService {

    List<DataInverter1> getDataInverterPV(Map<String, String> condition);

    void addReportPV(Report report);

    Integer getUserIdPV(String userName);

    List<Report> getReportPV(Integer userId);

    DataInverter1 getDataInverterInDayPV(Map<String, String> condition);

    DataInverter1 getDataInverterInMonthPV(Map<String, String> condition);

    DataInverter1 getDataInverterInYearPV(Map<String, String> condition);

    List<Report> getListByLimitPV(Map<String, Integer> map);

    String getProjectNamePV(String projectId);

    void updatePercentPV(Map<String, String> condition);

    void updateStatusPV(Integer id);

    void updateTimeFinishPV(Integer id);

    void deleteReportPV(Integer id);

    List<Map<String, String>> getTotalWarningPv(Map<String, String> conditions);

    String getTotalWarning(Map<String, String> conditions);

    List<Map<String, Object>> getDevicePV(Map<String, String> condition);

}
