package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataLoadFrame1View;

@Mapper
public interface DataLoadFrame1ViewMapper {

	DataLoadFrame1View selectLastestByDeviceId(@Param("schema") String schema, @Param("deviceId") Long deviceId, @Param("viewType") int viewType);

	List<DataLoadFrame1View> selectByTypeLastTime(@Param("schema") String schema);

	DataLoadFrame1View selectLastestTime(@Param("schema") String schema);

	void updateLastTime(@Param("schema") String schema, @Param("viewTime") String viewTime, @Param("sentDate") String sentDate);
	
	void update(@Param("schema") String schema, DataLoadFrame1View data);

	void saveAll(@Param("schema") String schema, List<DataLoadFrame1View> datas);

	List<String> getCustomerList(@Param("schema") String schema, @Param("table") String table);

}
