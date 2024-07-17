package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataPqs;

@Mapper
public interface DataPqsMapper {

    DataPqs selectLastestByDeviceId(@Param ("schema") String schema, @Param ("deviceId") Long deviceId,
        @Param ("viewType") int viewType);

    List<DataPqs> selectByTypeLastTime(@Param ("schema") String schema);

    List<DataPqs> selectAllCache(@Param ("schema") String schema,  @Param ("deviceType") int deviceType);

    DataPqs selectLastestTime(@Param ("schema") String schema, @Param ("deviceType") int deviceType);

    void updateLastTime(@Param ("schema") String schema, @Param ("viewTime") String viewTime,
        @Param ("sentDate") String sentDate,  @Param ("deviceType") int deviceType);

    void update(@Param ("schema") String schema, DataPqs data);

    void saveAll(@Param ("schema") String schema, List<DataPqs> datas);

    List<String> getCustomerList(@Param ("schema") String schema, @Param ("table") String table);

    void updateCache(@Param ("schema") String schema, @Param ("data") DataPqs data);

    void saveAllCache(@Param ("schema") String schema, List<DataPqs> datas);
    
    void insertFirstRecord(@Param("schema") String schema, @Param("data") DataPqs data);

}
