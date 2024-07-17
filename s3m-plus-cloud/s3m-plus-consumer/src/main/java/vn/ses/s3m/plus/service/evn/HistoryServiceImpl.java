package vn.ses.s3m.plus.service.evn;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.evn.HistoryMapperEVN;
import vn.ses.s3m.plus.dto.evn.HistoryEVN;

@Service
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    private HistoryMapperEVN historyMapper;

    @Override
    public List<HistoryEVN> getListHistory(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return historyMapper.getListHistory(condition);
    }

    @Override
    public List<HistoryEVN> getListHistoryBySuperManagerId(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return historyMapper.getListHistoryBySuperManagerId(condition);
    }

	@Override
	public List<HistoryEVN> getHistorys(Map<String, String> condition) {
		return historyMapper.getHistorys(condition);
	}

	@Override
	public List<HistoryEVN> searchHistory(Map<String, String> condition) {
		return historyMapper.searchHistory(condition);
	}

}
