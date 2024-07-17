package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataLoadFrame1;

public interface DataLoadFrame1Service {
    List<DataLoadFrame1> getDataLoadWarning(Map<String, Object> condition);
}
