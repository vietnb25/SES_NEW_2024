package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataCombiner1;

@Mapper
public interface DataCombiner1Mapper {
    List<DataCombiner1> selectNewRecord(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("id") String id);

    List<DataCombiner1> selectDataBySentDate(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("deviceId") Long deviceId, @Param ("fromDate") String fromDate, @Param ("toDate") String toDate);

    List<DataCombiner1> selectDataByFromDate(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("deviceId") Long deviceId, @Param ("fromDate") String fromDate, @Param ("toDate") String toDate);
}
