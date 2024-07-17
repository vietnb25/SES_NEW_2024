package vn.ses.s3m.plus.service;

import java.util.List;

import vn.ses.s3m.plus.dto.Cable;

public interface CableService {

    List<Cable> searchCables(String keyword);

    void deleteCable(int cableId);

    void updateCable(Cable cable);

    void insertCable(Cable cable);

    Cable getCableById(int id);

    List<Cable> getCables();

    Cable getCableByCableName(String cableName);

}
