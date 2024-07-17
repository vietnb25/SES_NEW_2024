package vn.ses.s3m.plus.service;

import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataLoadFrame2;

import java.util.List;
import java.util.Map;

public interface DataLoadFrame2Service {
    DataLoadFrame2 getInforDataFrame2LoadByTime(Map<String, Object> condition);

    List<DataLoadFrame2> getListWarnedDataFrame2(Map<String, Object> condition);

}
