package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.Report;

public interface ReportService {

    List<DataInverter1> getDataInverter(String date);

    void addReport(Report report);

    Integer getUserId(String userName);

    List<Report> getReport(Integer userId);

    DataInverter1 getDataInverterInDay(Map<String, String> condition);

    DataInverter1 getDataInverterInMonth(Map<String, String> condition);

    DataInverter1 getDataInverterInYear(Map<String, String> condition);

    List<Report> getListByLimit(Map<String, Integer> map);

    String getProjectName(String projectId);

    void updatePercent(Map<String, String> condition);

    void updateStatus(Integer id);

    void updateTimeFinish(Integer id);

    void deleteReport(Integer id);
}
