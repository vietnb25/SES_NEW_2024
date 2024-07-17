package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataLoadFrame1;
import vn.ses.s3m.plus.batch.dto.DataPqs;

@Mapper
public interface DataLoadFrame1Mapper {

	List<DataLoadFrame1> selectNewRecord(@Param("schema") String schema, @Param("table") String table, @Param("id") String id);

	List<DataLoadFrame1> selectDataBySentDate(@Param("schema") String schema, @Param("table") String table, @Param("deviceId") Long deviceId, 
			@Param("fromDate") String fromDate, @Param("toDate") String toDate);

	List<DataLoadFrame1> selectDataByFromDate(@Param("schema") String schema, @Param("table") String table, @Param("deviceId") Long deviceId, 
			@Param("fromDate") String fromDate, @Param("toDate") String toDate);
	
	DataLoadFrame1 selectNewestRecord(@Param("schema") String schema, @Param("table") String table);
	
}
