package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.dao.DataLoadFrame2Mapper;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataLoadFrame2;

@Service
public class OperationInformationServiceImpl implements OperationInformationService {

    @Autowired
    private DataLoadFrame1Mapper dataLoadFrame1Mapper;

    @Autowired
    private DataLoadFrame2Mapper dataLoadFrame2Mapper;

    /**
     * Lấy thông tin vận hành tức thời của thiết bị
     *
     * @param condition Điều kiện lấy thông tin vận hành
     * @return Thông tin vận hành tức thời
     */
    @Override
    public DataLoadFrame1 getInstantOperationInformation(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.getInstantOperationInformation(condition);
    }

    /**
     * Lấy thông tin vận hành của thiết bị
     *
     * @param condition Điều kiện lấy thông tin vận hành
     * @return Thông tin vận hành
     */
    @Override
    public List<DataLoadFrame1> getOperationInformation(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.getOperationInformation(condition);
    }

    /**
     * Lấy thông số chất lượng điện năng tức thời
     *
     * @param condition Điều kiện lấy chất lượng điện năng
     * @return Thông số chất lượng điện năng tức thời
     */
    @Override
    public DataLoadFrame2 getInstantPowerQuality(final Map<String, Object> condition) {
        return dataLoadFrame2Mapper.getInstantPowerQuality(condition);
    }

    /**
     * Lấy danh sách thông số chất lượng điện năng
     *
     * @param condition Điều kiện lấy chất lượng điện năng
     * @return Danh sách thông số chất lượng điện năng
     */
    @Override
    public List<DataLoadFrame2> getPowerQualities(final Map<String, Object> condition) {
        return dataLoadFrame2Mapper.getPowerQualities(condition);
    }

    /**
     * Lấy thông tin Harmonic
     *
     * @param condition điều kiện truy vấn theo ID thiết bị
     * @return thông tin Harmonic
     */
    @Override
    public DataLoadFrame2 getDataHarmonic(final Map<String, String> condition) {
        return dataLoadFrame2Mapper.getDataHarmonic(condition);
    }

    /**
     * Lấy dữ liệu điện năng theo năm
     *
     * @param condition Điều kiện lấy dữ liệu
     * @return Danh sách dữ liệu điện năng trong năm
     */
    @Override
    public List<DataLoadFrame1> getDataPQSByMonth(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.getDataPQSByMonth(condition);
    }

    /**
     * Lấy tổng record theo điều kiện.
     *
     * @param condition Điều kiện lấy dữ liệu.
     * @return Tổng record.
     */
    @Override
    public Integer countTotalData(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.countTotalData(condition);
    }

    /**
     * Lấy tổng record s3m_data_load_frame_2 theo điều kiện.
     *
     * @param condition Điều kiện lấy dữ liệu.
     * @return Tổng record.
     */
    @Override
    public Integer countDataFrame2(final Map<String, Object> condition) {
        return dataLoadFrame2Mapper.countDataFrame2(condition);
    }

    /**
     * Lấy thông tin sóng hài theo giai đoạn
     *
     * @param condition Điều kiện lấy thông tin
     * @return Danh sách dữ liệu sóng hài
     */
    @Override
    public List<DataLoadFrame1> getHarmonicPeriod(final Map<String, Object> condition) {
        return dataLoadFrame1Mapper.getHarmonicPeriod(condition);
    }

    /**
     * Lấy thông tin sóng hài theo thời điểm
     *
     * @param condition Điều kiện lấy thông tin
     * @return Dữ liệu sóng hài tại thời điểm
     */
    @Override
    public DataLoadFrame2 getDataHarmonicByDay(final Map<String, Object> condition) {
        return dataLoadFrame2Mapper.getDataHarmonicByDay(condition);
    }

}
