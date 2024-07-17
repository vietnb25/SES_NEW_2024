package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataCombiner1View;

@Mapper
public interface DataCombiner1ViewMapper {

    DataCombiner1View selectLastestByDeviceId(@Param ("schema") String schema, @Param ("deviceId") Long deviceId,
        @Param ("viewType") int viewType);

    List<DataCombiner1View> selectByTypeLastTime(@Param ("schema") String schema);

    DataCombiner1View selectLastestTime(@Param ("schema") String schema);

    void updateLastTime(@Param ("schema") String schema, @Param ("viewTime") String viewTime,
        @Param ("sentDate") String sentDate);

    void update(@Param ("schema") String schema, @Param ("data") DataCombiner1View data);

    void saveAll(@Param ("schema") String schema, List<DataCombiner1View> datas);

    List<String> getCustomerList(@Param ("schema") String schema, @Param ("table") String table);
}
