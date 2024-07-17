package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.OverviewPowerMapper;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewGridPower;
import vn.ses.s3m.plus.dto.OverviewGridTotalPower;

@Service
public class OverviewPowerGridServiceImpl implements OverviewPowerGridService {

    @Autowired
    private OverviewPowerMapper overviewPowerMapper;

    @Override
    public OverviewGridPower getPowerRMU(Map<String, Object> condition) {
        return overviewPowerMapper.getOverviewPowerRMU(condition);
    }

    @Override
    public List<OverviewGridPower> getDeviceHasWarning(Map<String, Object> condition) {
        return overviewPowerMapper.getDevicesHasWarningGrid(condition);
    }

    @Override
    public List<OverviewGridPower> getLayer(Map<String, Object> condition) {
        return overviewPowerMapper.getLayerGrid(condition);
    }

    @Override
    public List<OverviewGridTotalPower> getListPowerRMUInDay(Map<String, Object> condition) {
        return overviewPowerMapper.getListPowerRMUInDay(condition);
    }

    @Override
    public Forecast getForecast(Map<String, Object> condition) {
        return overviewPowerMapper.getForecast(condition);
    }

    @Override
    public List<Forecast> getListForecast(Map<String, Object> condition) {
        return overviewPowerMapper.getListForecast(condition);
    }

    @Override
    public Map<String, String> getInformationProject(Map<String, Object> condition) {
        return overviewPowerMapper.getInformationProject(condition);
    }

}
