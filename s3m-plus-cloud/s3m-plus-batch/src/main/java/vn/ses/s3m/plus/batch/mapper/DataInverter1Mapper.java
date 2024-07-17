package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataInverter1;

@Mapper
public interface DataInverter1Mapper {

    List<DataInverter1> selectNewRecordInverter(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("id") String id);

    List<DataInverter1> selectDataInverterBySentDate(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("deviceId") Long deviceId, @Param ("fromDate") String fromDate, @Param ("toDate") String toDate);

    List<DataInverter1> selectDataInverterByFromDate(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("deviceId") Long deviceId, @Param ("fromDate") String fromDate, @Param ("toDate") String toDate);
    
    DataInverter1 selectNewestRecordInverter(@Param ("schema") String schema, @Param ("table") String table);
}
