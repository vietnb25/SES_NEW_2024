package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataFlow;
import vn.ses.s3m.plus.batch.dto.DataInverter1;

@Mapper
public interface DataFlowMapper {

	List<DataFlow> selectNewRecordFlow(@Param("schema") String schema, @Param("table") String table,
			@Param("id") String id);

	DataFlow selectNewestRecordFlow(@Param("schema") String schema, @Param("table") String table);

}
