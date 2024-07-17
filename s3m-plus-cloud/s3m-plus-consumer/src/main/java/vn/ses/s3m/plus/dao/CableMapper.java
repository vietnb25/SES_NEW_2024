package vn.ses.s3m.plus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Cable;

@Mapper
public interface CableMapper {

    List<Cable> getCables();

    Cable getCableById(int id);

    void insertCable(Cable cable);

    void updateCable(Cable cable);

    void deleteCable(int cableId);

    List<Cable> searchCables(String keyword);

    Cable getCableByCableName(String cableName);
}
