package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataLoadFrame1ViewCache;

@Mapper
public interface DataLoadFrame1ViewCacheMapper {

    List<DataLoadFrame1ViewCache> selectAll(@Param ("schema") String schema);

    DataLoadFrame1ViewCache selectByDeviceIdAndViewType(@Param ("schema") String schema,
        @Param ("deviceId") Long deviceId, @Param ("viewType") int viewType);

    void update(@Param ("schema") String schema, @Param ("data") DataLoadFrame1ViewCache data);

    void saveAll(@Param ("schema") String schema, List<DataLoadFrame1ViewCache> datas);

}
