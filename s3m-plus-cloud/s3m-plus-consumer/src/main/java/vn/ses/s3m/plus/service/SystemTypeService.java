package vn.ses.s3m.plus.service;

import java.util.List;

import vn.ses.s3m.plus.dto.SystemType;

public interface SystemTypeService {

    List<SystemType> getSystemTypes();

    SystemType getSystemTypeById(Integer systemTypeId);
}
