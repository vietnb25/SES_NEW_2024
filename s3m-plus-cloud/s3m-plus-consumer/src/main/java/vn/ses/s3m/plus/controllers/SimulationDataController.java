package vn.ses.s3m.plus.controllers;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.SimulationDataEP;
import vn.ses.s3m.plus.dto.SimulationDataMoney;
import vn.ses.s3m.plus.form.SimulationDataForm;
import vn.ses.s3m.plus.response.SimulationDataEpResponse;
import vn.ses.s3m.plus.response.SimulationDataMoneyResponse;
import vn.ses.s3m.plus.service.SimulationDataEpService;
import vn.ses.s3m.plus.service.SimulationDataMoneyService;

@RestController
@RequestMapping("/common/simulation-data")
public class SimulationDataController {
	@Autowired
	private SimulationDataEpService service;
	@Autowired
	private SimulationDataMoneyService moneyService;

	@GetMapping("/list/ep")
	public ResponseEntity<List<SimulationDataEpResponse>> getList(@RequestParam("project") final String projectId,
			@RequestParam("system-type") final String systemType,
			@RequestParam("customer") final String customer) {
		List<SimulationDataEpResponse> listResp = new ArrayList<>();
		Map<String, String> condition = new HashMap<String, String>();
		String schema = Schema.getSchemas(Integer.parseInt(customer));
		System.out.println("asdasd: " + schema);
		if (projectId != null && projectId != "") {
			condition.put("projectId", projectId);
		}
		if (systemType != null && systemType != "") {
			condition.put("systemTypeId", systemType);
		}if(customer != null && customer != "") {
			condition.put("schema", schema);
		}
		List<SimulationDataEP> list = this.service.getList(condition);
		for (SimulationDataEP e : list) {
			SimulationDataEpResponse res = new SimulationDataEpResponse(e);
			listResp.add(res);
		}
		return new ResponseEntity<List<SimulationDataEpResponse>>(listResp, HttpStatus.OK);
	}
	@GetMapping("/list/money")
	public ResponseEntity<List<SimulationDataMoneyResponse>> getListMoney(@RequestParam("project") final String projectId,
			@RequestParam("system-type") final String systemType,
			@RequestParam("customer") final String customer) {
		List<SimulationDataMoneyResponse> listResp = new ArrayList<>();
		Map<String, String> condition = new HashMap<String, String>();
		String schema = Schema.getSchemas(Integer.parseInt(customer));
		if (projectId != null && projectId != "") {
			condition.put("projectId", projectId);
		}
		if (systemType != null && systemType != "") {
			condition.put("systemTypeId", systemType);
		}if(customer != null && customer != "") {
			condition.put("schema", schema);
		}
		List<SimulationDataMoney> list = this.moneyService.getList(condition);
		for (SimulationDataMoney e : list) {
			SimulationDataMoneyResponse res = new SimulationDataMoneyResponse(e);
			listResp.add(res);
		}
		return new ResponseEntity<List<SimulationDataMoneyResponse>>(listResp, HttpStatus.OK);
	}
	@PostMapping("/add/ep")
	public ResponseEntity<?> add(@Valid @RequestBody SimulationDataForm entity) {
		String schema = Schema.getSchemas(Integer.parseInt(entity.getCustomer()));
		entity.setCustomer(schema);
		System.out.println("asdasd: " + entity.getCustomer());
		this.service.addData(entity);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@PutMapping("/edit/ep/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody SimulationDataForm e, @PathVariable("id") Integer id) {
		String schema = Schema.getSchemas(Integer.valueOf(e.getCustomer()));
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("schema", schema);
		condition.put("id", id);
		condition.put("systemTypeId",e.getSystemTypeId());
		condition.put("projectId",e.getProjectId());
		if(e.getYear() != null) {
			condition.put("year", e.getYear());
		}
		if(e.getJan() != null) {
			condition.put("jan", e.getJan());
		}
		if(e.getMar() != null) {
			condition.put("mar", e.getMar()); 
		}
		if(e.getApr() != null) {
			condition.put("apr", e.getApr());
		}
		if(e.getMay() != null) {
			condition.put("may", e.getMay());
		}
		if(e.getJun() != null) {
			condition.put("jun", e.getJun());
		}
		if(e.getJul() != null) {
			condition.put("jul", e.getJul());
		}
		if(e.getAug() != null) {
			condition.put("aug", e.getAug());
		}
		if(e.getOct() != null) {
			condition.put("oct", e.getOct());
		}
		if(e.getSep() != null) {
			condition.put("sep", e.getSep());
		}
		if(e.getNov() != null) {
			condition.put("nov", e.getNov());
		}
		if(e.getDec() != null) {
			condition.put("dec", e.getDec());
		}
		if(e.getFeb() != null) {
			condition.put("feb", e.getFeb());
		}
		System.out.println("data"+  e.toString());
		this.service.updateData(condition);		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@PostMapping("/add/money")
	public ResponseEntity<?> addMoney(@Valid @RequestBody SimulationDataForm entity) {
		String schema = Schema.getSchemas(Integer.parseInt(entity.getCustomer()));
		entity.setCustomer(schema);
		System.out.println("asdasd: " + entity.getCustomer());
		this.moneyService.addData(entity);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@PutMapping("/edit/money/{id}")
	public ResponseEntity<?> updateMoney(@Valid @RequestBody SimulationDataForm e, @PathVariable("id") Integer id) {
		String schema = Schema.getSchemas(Integer.valueOf(e.getCustomer()));
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("schema", schema);
		condition.put("id", id);
		condition.put("systemTypeId",e.getSystemTypeId());
		condition.put("projectId",e.getProjectId());
		if(e.getYear() != null) {
			condition.put("year", e.getYear());
		}
		if(e.getJan() != null) {
			condition.put("jan", e.getJan());
		}
		if(e.getMar() != null) {
			condition.put("mar", e.getMar()); 
		}
		if(e.getApr() != null) {
			condition.put("apr", e.getApr());
		}
		if(e.getMay() != null) {
			condition.put("may", e.getMay());
		}
		if(e.getJun() != null) {
			condition.put("jun", e.getJun());
		}
		if(e.getJul() != null) {
			condition.put("jul", e.getJul());
		}
		if(e.getAug() != null) {
			condition.put("aug", e.getAug());
		}
		if(e.getOct() != null) {
			condition.put("oct", e.getOct());
		}
		if(e.getSep() != null) {
			condition.put("sep", e.getSep());
		}
		if(e.getNov() != null) {
			condition.put("nov", e.getNov());
		}
		if(e.getDec() != null) {
			condition.put("dec", e.getDec());
		}
		if(e.getFeb() != null) {
			condition.put("feb", e.getFeb());
		}
		this.moneyService.updateData(condition);		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
