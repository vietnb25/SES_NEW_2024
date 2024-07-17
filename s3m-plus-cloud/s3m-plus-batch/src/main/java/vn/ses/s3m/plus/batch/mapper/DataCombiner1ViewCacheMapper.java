package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataCombiner1ViewCache;

@Mapper
public interface DataCombiner1ViewCacheMapper {

    List<DataCombiner1ViewCache> selectAll(@Param ("schema") String schema);

    DataCombiner1ViewCache selectByDeviceIdAndViewType(@Param ("schema") String schema,
        @Param ("deviceId") Long deviceId, @Param ("viewType") int viewType);

    void update(@Param ("schema") String schema, @Param ("data") DataCombiner1ViewCache data);

    void saveAll(@Param ("schema") String schema, List<DataCombiner1ViewCache> datas);
}
