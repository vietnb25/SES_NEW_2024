package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.ReportQuantityPower;
import vn.ses.s3m.plus.dto.Setting;

import java.util.List;
import java.util.Map;

public interface ReportQuantityPowerService {
    List<ReportQuantityPower> getReportFrame1(Map<String,String> condition);
    List<ReportQuantityPower> getReportFrame2(Map<String,String> condition);
    List<Setting> iEEELimit(Map<String,String> condition);
}
