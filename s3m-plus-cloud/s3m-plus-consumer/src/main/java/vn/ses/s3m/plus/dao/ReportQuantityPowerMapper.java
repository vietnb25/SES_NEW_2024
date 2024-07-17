package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.ReportQuantityPower;
import vn.ses.s3m.plus.dto.Setting;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportQuantityPowerMapper {
    List<ReportQuantityPower> getReportFrame1(Map<String,String> condition);
    List<ReportQuantityPower> getReportFrame2(Map<String,String> condition);
    List<Setting> iEEELimit(Map<String,String> condition);
}
