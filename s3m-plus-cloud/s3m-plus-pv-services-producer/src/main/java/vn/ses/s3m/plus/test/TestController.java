package vn.ses.s3m.plus.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/pv")
public class TestController {

    @GetMapping ("/")
    public Map<String, Integer> test() {
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        return map;
    }
}
