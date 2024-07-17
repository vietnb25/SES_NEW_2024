package vn.ses.s3m.plus.dao.evn;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.evn.HistoryEVN;

@Mapper
public interface HistoryMapperEVN {
    List<HistoryEVN> getListHistory(Map<String, String> condition);

    List<HistoryEVN> getListHistoryBySuperManagerId(Map<String, String> condition);

    public void add(Map<String, Object> condition);

    public HistoryEVN getHistory(Map<String, String> condition);

    public List<HistoryEVN> getHistoryRealTime(Map<String, String> condition);

    public List<HistoryEVN> getHistoryByToDate(Map<String, String> condition);

    public List<HistoryEVN> getHistorysParent(Map<String, String> condition);

    public void delete(Map<String, String> condition);

    public void update(Map<String, String> condition);

    public void updateParent(Map<String, String> condition);

    void updateUpdateFlag(Map<String, String> condition);

    void updateStatus(Map<String, String> condition);

    public void updateSendStatusById(Map<String, String> condition);
    
    List<HistoryEVN> getHistorys(Map<String, String> condition);
    
    List<HistoryEVN> searchHistory(Map<String, String> condition);
    
    public List<HistoryEVN> getHistorysSetting(Map<String, String> condition);
}
