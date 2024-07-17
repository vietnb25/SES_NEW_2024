package vn.ses.s3m.plus.service.evn;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.evn.HistoryEVN;

public interface HistoryService {
    List<HistoryEVN> getListHistory(Map<String, String> condition);

    List<HistoryEVN> getListHistoryBySuperManagerId(Map<String, String> condition);
    
    List<HistoryEVN> getHistorys(Map<String, String> condition);
    
    List<HistoryEVN> searchHistory(Map<String, String> condition);
}
