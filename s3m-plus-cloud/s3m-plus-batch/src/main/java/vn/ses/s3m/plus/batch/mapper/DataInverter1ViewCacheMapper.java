package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataInverter1ViewCache;

@Mapper
public interface DataInverter1ViewCacheMapper {

    List<DataInverter1ViewCache> selectAllInverter(@Param ("schema") String schema);

    DataInverter1ViewCache selectInverterByDeviceIdAndViewType(@Param ("schema") String schema,
        @Param ("deviceId") Long deviceId, @Param ("viewType") int viewType);

    void updateInverter(@Param ("schema") String schema, DataInverter1ViewCache data);

    void saveAllInverter(@Param ("schema") String schema, List<DataInverter1ViewCache> datas);

}
