package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.ReportQuantityPowerMapper;
import vn.ses.s3m.plus.dto.ReportQuantityPower;
import vn.ses.s3m.plus.dto.Setting;

import java.util.List;
import java.util.Map;

@Service
public class ReportQuantityPowerServiceImpl implements ReportQuantityPowerService{
    @Autowired
    private ReportQuantityPowerMapper mapper;

    @Override
    public List<ReportQuantityPower> getReportFrame1(Map<String, String> condition) {
        return this.mapper.getReportFrame1(condition);
    }

    @Override
    public List<ReportQuantityPower> getReportFrame2(Map<String, String> condition) {
        return this.mapper.getReportFrame2(condition);
    }

    @Override
    public List<Setting> iEEELimit(Map<String, String> condition) {
        return this.mapper.iEEELimit(condition);
    }
}
