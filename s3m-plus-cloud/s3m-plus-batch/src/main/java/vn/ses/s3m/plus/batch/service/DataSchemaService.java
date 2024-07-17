package vn.ses.s3m.plus.batch.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.batch.common.Constants;
import vn.ses.s3m.plus.batch.dto.DataCombiner1;
import vn.ses.s3m.plus.batch.dto.DataInverter1;
import vn.ses.s3m.plus.batch.dto.DataLoadFrame1;
import vn.ses.s3m.plus.batch.dto.DataPqs;
import vn.ses.s3m.plus.batch.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.batch.dto.Setting;
import vn.ses.s3m.plus.batch.mapper.DataCombiner1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataInverter1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataPqsMapper;
import vn.ses.s3m.plus.batch.mapper.DataRmuDrawer1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataSchemaMapper;
import vn.ses.s3m.plus.batch.mapper.SettingMapper;

@Service
public class DataSchemaService {

	private static final Logger log = LoggerFactory.getLogger(DataPqsService.class);

	private Map<MultiKey, DataPqs> map = new HashMap<MultiKey, DataPqs>();

	private Map<Long, DataPqs> mapCache = new HashMap<Long, DataPqs>();

	@Autowired
	private DataSchemaMapper dataSchemaMapper;

	@Autowired
	private DataPqsMapper dataPqsMapper;

	public boolean doProcess() {
		// ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
		boolean isNoData = true;

		List<String> schemas = dataPqsMapper.getCustomerList("s3m_plus", "s3m_customer");
		process(schemas, isNoData);
		// processCombiner1(schemas, isNoData);
		// processRmuDrawer1(schemas, isNoData);
		return isNoData;
	}

	public void process(List<String> schemas, boolean isNoData) {
		for (String schema : schemas) {
			schema = "s3m_plus_customer_" + schema;
			Calendar instance = Calendar.getInstance();
			int year = instance.get(Calendar.YEAR);
			year += 1;
			// -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
			log.info("!!!READING SCHEMA.........");
			dataSchemaMapper.insertNewTables(schema, year);
			try {
				log.info("!!!Insert new tables in schema customer..");
				continue;
			} catch (Exception e) {
				log.error("!!!ERROR Don't insert new tables in schema customer");
				continue;
			}
		}

	}
}
