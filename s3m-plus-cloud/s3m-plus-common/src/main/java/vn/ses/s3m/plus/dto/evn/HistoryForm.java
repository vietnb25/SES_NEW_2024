package vn.ses.s3m.plus.dto.evn;

import java.sql.Timestamp;
import java.util.Date;

public class HistoryForm {
	 private Integer historyId;

	    private Integer deviceId;

	    private String fromDate;

	    private String toDate;

	    private String timeFrame;

	    private String timeInsert;

	    private Double congSuatChoPhep;

	    private Double congSuatDinhMuc;

	    private Double congSuatTietGiam;

	    private Integer deleteFlag;

	    private Integer status;

	    private Integer typeScrop;

	    private Integer stt;

	    private Integer parentId;

	    private Integer updateFlag;

	    private Timestamp createDate;

	    private Timestamp deleteDate;

	    private String viTri;

		public Integer getHistoryId() {
			return historyId;
		}

		public void setHistoryId(Integer historyId) {
			this.historyId = historyId;
		}

		public Integer getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(Integer deviceId) {
			this.deviceId = deviceId;
		}

		public String getFromDate() {
			return fromDate;
		}

		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}

		public String getToDate() {
			return toDate;
		}

		public void setToDate(String toDate) {
			this.toDate = toDate;
		}

		public String getTimeFrame() {
			return timeFrame;
		}

		public void setTimeFrame(String timeFrame) {
			this.timeFrame = timeFrame;
		}

		public String getTimeInsert() {
			return timeInsert;
		}

		public void setTimeInsert(String timeInsert) {
			this.timeInsert = timeInsert;
		}

		public Double getCongSuatChoPhep() {
			return congSuatChoPhep;
		}

		public void setCongSuatChoPhep(Double congSuatChoPhep) {
			this.congSuatChoPhep = congSuatChoPhep;
		}

		public Double getCongSuatDinhMuc() {
			return congSuatDinhMuc;
		}

		public void setCongSuatDinhMuc(Double congSuatDinhMuc) {
			this.congSuatDinhMuc = congSuatDinhMuc;
		}

		public Double getCongSuatTietGiam() {
			return congSuatTietGiam;
		}

		public void setCongSuatTietGiam(Double congSuatTietGiam) {
			this.congSuatTietGiam = congSuatTietGiam;
		}

		public Integer getDeleteFlag() {
			return deleteFlag;
		}

		public void setDeleteFlag(Integer deleteFlag) {
			this.deleteFlag = deleteFlag;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Integer getTypeScrop() {
			return typeScrop;
		}

		public void setTypeScrop(Integer typeScrop) {
			this.typeScrop = typeScrop;
		}

		public Integer getStt() {
			return stt;
		}

		public void setStt(Integer stt) {
			this.stt = stt;
		}

		public Integer getParentId() {
			return parentId;
		}

		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		public Integer getUpdateFlag() {
			return updateFlag;
		}

		public void setUpdateFlag(Integer updateFlag) {
			this.updateFlag = updateFlag;
		}

		public Timestamp getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Timestamp createDate) {
			this.createDate = createDate;
		}

		public Timestamp getDeleteDate() {
			return deleteDate;
		}

		public void setDeleteDate(Timestamp deleteDate) {
			this.deleteDate = deleteDate;
		}

		public String getViTri() {
			return viTri;
		}

		public void setViTri(String viTri) {
			this.viTri = viTri;
		}
	    
	    
	    
}
