package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataInverter1View;

@Mapper
public interface DataInverter1ViewMapper {

    DataInverter1View selectInverterLastestByDeviceId(@Param ("schema") String schema,
        @Param ("deviceId") Long deviceId, @Param ("viewType") int viewType);

    List<DataInverter1View> selectInverterByTypeLastTime(@Param ("schema") String schema);

    DataInverter1View selectInverterLastestTime(@Param ("schema") String schema);

    void updateLastTimeInverter(@Param ("schema") String schema, @Param ("viewTime") String viewTime,
        @Param ("sentDate") String sentDate);

    void updateInverter(@Param ("schema") String schema, DataInverter1View data);

    void saveAllInverter(@Param ("schema") String schema, List<DataInverter1View> datas);

    List<String> getCustomerList(@Param ("schema") String schema, @Param ("table") String table);

}
