package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataLoadFrame2;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.Warning;

public interface ReportService {

    List<Map<String, Object>> getDevice(Map<String, String> condition);

    List<Map<String, Object>> getDeviceHasWaring(Map<String, Object> condition);

    List<Report> getReport(Map<String, Object> condition);

    List<DataLoadFrame1> getAllDataLoadFrame1(Map<String, String> condition);

    List<DataLoadFrame1> getDataLoadFrame1ById(Map<String, String> condition);

    List<DataLoadFrame1> getDataLoadFrame1ByIdLimit(Map<String, String> condition);

    List<DataLoadFrame1> getDataLoadFrame1Limit(Map<String, String> condition);

    List<DataLoadFrame2> getDataLoadFrame2ById(Map<String, String> condition);

    List<DataLoadFrame2> getAllDataLoadFrame2(Map<String, String> condition);

    void addReport(Report report);

    void deleteReport(Integer id);

    void updatePercent(Map<String, String> condition);

    Integer getUserId(String userName);

    void deleteReportByUrl(String url);

    List<Report> getListByLimit(Map<String, Integer> map);

    void updateStatus(Integer id);

    void updateTimeFinish(Integer id);

    List<Warning> getDetailWarningByWarningType(Map<String, Object> condition);

    List<Warning> getTotalWarningByLimit(Map<String, Object> condition);

    List<Warning> getDetailWarningByWarningTypeByLimit(Map<String, Object> condition);

    List<DataLoadFrame1> getElectricalPower(Map<String, String> condition);

    Long getElectricalPowerInHour(Map<String, String> condition);

    String getProjectName(String projectId);

    List<Map<String, String>> getWarningLoad(Map<String, String> condition);

    String getTotalWarningLoad(Map<String, String> condition);

    List<DataLoadFrame1> getElectricalPowerInMonth(Map<String, String> condition);

    List<DataLoadFrame1> getElectricalPowerInDay(Map<String, String> condition);

    List<DataLoadFrame1> getElectricalPowerInYear(Map<String, String> condition);

}
