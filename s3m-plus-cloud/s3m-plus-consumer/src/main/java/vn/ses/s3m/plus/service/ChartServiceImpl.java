package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ChartMapper;
import vn.ses.s3m.plus.dao.DataCombinerMapper;
import vn.ses.s3m.plus.dao.DataInverterMapper;
import vn.ses.s3m.plus.dao.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.dao.DataRmuDrawer1Mapper;
import vn.ses.s3m.plus.dto.Chart;
import vn.ses.s3m.plus.dto.DataCombiner1;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;

@Service
public class ChartServiceImpl implements ChartService {
    @Autowired
    private DataLoadFrame1Mapper dataLoadFrame1Mapper;

    @Autowired
    private DataInverterMapper dataInverterMapper;

    @Autowired
    private DataCombinerMapper dataCombinerMapper;

    @Autowired
    private DataRmuDrawer1Mapper dataRmuDrawer1Mapper;

    @Autowired
    private ChartMapper chartMapper;

    /**
     * Đếm số cảnh báo theo project id và thời gian
     *
     * @param condition Điều kiện truy vấn theo projectId và thời gian.
     * @return Số cảnh báo theo điều kiện truy vấn.
     */
    @Override
    public List<DataLoadFrame1> getChartLoadByCustomerId(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.getChartLoadByCustomerId(condition);
    }

    @Override
    public List<DataInverter1> getChartSolarByCustomerId(final Map<String, Object> condition) {
        return dataInverterMapper.getChartSolarByCustomerId(condition);
    }

    @Override
    public List<DataCombiner1> getChartCombinerByCustomerId(final Map<String, Object> condition) {
        return dataCombinerMapper.getChartCombinerByCustomerId(condition);
    }

    @Override
    public List<DataRmuDrawer1> getChartRmuByCustomerId(final Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getChartRmuByCustomerId(condition);
    }

    @Override
    public List<Chart> getChartLoadCostByCustomerId(final Map<String, Object> condition) {
        return chartMapper.getChartLoadCostByCustomerId(condition);
    }

    @Override
    public List<Chart> getChartSolarCostByCustomerId(final Map<String, Object> condition) {
        return chartMapper.getChartSolarCostByCustomerId(condition);
    }

    @Override
    public List<Chart> getChartGridCostByCustomerId(final Map<String, Object> condition) {
        return chartMapper.getChartGridCostByCustomerId(condition);
    }

    @Override
    public List<Chart> getChartLoadSumCostByDay(final Map<String, Object> condition) {
        return chartMapper.getChartLoadSumCostByDay(condition);
    }

    @Override
    public List<Chart> getChartSolarSumCostByDay(final Map<String, Object> condition) {
        return chartMapper.getChartSolarSumCostByDay(condition);
    }

    @Override
    public List<Chart> getChartGridSumCostByDay(final Map<String, Object> condition) {
        return chartMapper.getChartGridSumCostByDay(condition);
    }

    @Override
    public List<Chart> getChartLoadByHour(final Map<String, Object> condition) {
        return chartMapper.getChartLoadByHour(condition);
    }

    @Override
    public List<Chart> getChartInverterByHour(final Map<String, Object> condition) {
        return chartMapper.getChartInverterByHour(condition);
    }

    @Override
    public List<Chart> getChartRmuByHour(final Map<String, Object> condition) {
        return chartMapper.getChartRmuByHour(condition);
    }

    @Override
    public List<Chart> getChartLoadPower(final Map<String, Object> condition) {
        return chartMapper.getChartLoadPower(condition);
    }

    @Override
    public List<Chart> getChartInverterPower(final Map<String, Object> condition) {
        return chartMapper.getChartInverterPower(condition);
    }

    @Override
    public List<Chart> getChartRmuPower(final Map<String, Object> condition) {
        return chartMapper.getChartRmuPower(condition);
    }

	@Override
	public List<Chart> getChartLoadSumCostByYear(Map<String, Object> condition) {
		return chartMapper.getChartLoadSumCostByYear(condition);
	}

	@Override
	public List<Chart> getChartSolarSumCostByYear(Map<String, Object> condition) {
		return chartMapper.getChartSolarSumCostByYear(condition);
	}

	@Override
	public List<Chart> getChartGridSumCostByYear(Map<String, Object> condition) {
		return chartMapper.getChartGridSumCostByYear(condition);
	}

	@Override
	public List<Chart> getChartLoadCostHomePage(Map<String, Object> condition) {
		return chartMapper.getChartLoadCostHomePage(condition);
	}

	@Override
	public List<Chart> getChartSolarCostHomePage(Map<String, Object> condition) {
        return chartMapper.getChartSolarCostHomePage(condition);
	}

	@Override
	public List<Chart> getChartGridCostHomePage(Map<String, Object> condition) {
        return chartMapper.getChartGridCostHomePage(condition);
	}

	@Override
	public List<Chart> getChartLoadSumCostHomePage(Map<String, Object> condition) {
		return chartMapper.getChartLoadSumCostHomePage(condition);
	}

	@Override
	public List<Chart> getChartSolarSumCostHomePage(Map<String, Object> condition) {
        return chartMapper.getChartSolarSumCostHomePage(condition);
	}

	@Override
	public List<Chart> getChartGridSumCostHomePage(Map<String, Object> condition) {
        return chartMapper.getChartGridSumCostHomePage(condition);
	}

	@Override
	public List<Chart> getChartTemperature(Map<String, Object> condition) {
		return chartMapper.getChartTemperature(condition);
	}

	@Override
	public List<Chart> getChartSankey(Map<String, Object> condition) {
		return chartMapper.getChartSankey(condition);
	}

	@Override
	public List<Chart> getChartDischargeIndicatorHtr02(Map<String, Object> condition) {		
		return chartMapper.getChartDischargeIndicatorHtr02(condition);
	}

	@Override
	public List<Chart> getChartDischargeIndicatorAms01(Map<String, Object> condition) {
		return chartMapper.getChartDischargeIndicatorAms01(condition);
	}

	@Override
	public List<DataLoadFrame1> getChartLoadCompare(Map<String, Object> condition) {
		 return dataLoadFrame1Mapper.getChartLoadCompare(condition);
	}

}
