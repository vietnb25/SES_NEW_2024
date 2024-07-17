package vn.ses.s3m.plus.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SystemTreeData {
    private String key;

    private String label;

    private String icon;

    private String type;

    private Map<String, Object> data;

    private List<SystemTreeData> children = new ArrayList<>();
}
