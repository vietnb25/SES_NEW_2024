
import React, { useEffect, useState } from "react";
import { Calendar } from 'primereact/calendar';
import { useParams, useLocation, useHistory } from 'react-router-dom';
import HomeService from "../../../../../services/HomeService";
import reportService from "../../../../../services/ReportService";
import AuthService from "../../../../../services/AuthService";
import { ProgressBar } from "primereact/progressbar";
import moment from "moment";
import CONS from "../../../../../constants/constant";
import $ from "jquery";
import profileService from "../../../../../services/ProfileService";


const ReportSolar = ({ projectInfo }) => {
  const { projectId, customerId } = useParams();
  const location = useLocation();
  const history = useHistory();
  let userName = AuthService.getUserName();
  const [dateType, setDateType] = useState(1);
  const [date, setDate] = useState(new Date());
  const [listReport, setListReport] = useState([]);
  const [selectedInputDevice, setSelectedInputDevice] = useState(true);
  const [device, setDevice] = useState([]);
  const [deviceName, setDeviceName] = useState("");
  const [listDevice, setListDevice] = useState([]);
  const [state, setState] = useState(0);


  function Interval() {
    setTimeout(() => {
      setState(state + 1);
    }, 60000)
  }
  const [profiledata, setProfiledata] = useState({
    staffName: "",
    email: "",
    img: null,
    id: ""

  });

  const profile = async () => {
    let res = await profileService.detailProfile();
    if (res.status === 200) {
      setProfiledata({
        staffName: res.data.staffName,
        email: res.data.email,
        img: res.data.img,
        id: res.data.id
      });
    }
  }

  const [reportType, setReportType] = useState({
    type1: "Báo cáo năng lượng"
    // type2: "Thông số nhiệt độ",
    // type3: "Sóng hài",
    // type4: "Thông số cảnh báo",
    // type5: "Báo cáo tổng hợp"
  });

  const searchDevice = (device_name) => {
    document.getElementById("device-search").value = device_name;
    document.getElementById("btn-export-report").disabled = false;
    setListDevice([]);
  }

  const handleChangeChartType = (value) => {
    setDateType(parseInt(value));
  }

  const changeSearch = () => {
    setSelectedInputDevice(!selectedInputDevice);
    if (selectedInputDevice == true) {
      document.getElementById("btn-export-report").disabled = true;
    } else {
      document.getElementById("btn-export-report").disabled = false;
    }
  }

  const download = async (url) => {
    let res = await reportService.downloadReportPV(url);
    if (res.status != 200) {
      $.alert({
        title: 'Thông báo!',
        content: 'Lỗi không xác định!',
      });
    }
  }
  const deleteReport = async (id) => {
    $.confirm({
      type: 'red',
      typeAnimated: true,
      icon: 'fa fa-warning',
      title: 'Xác nhận!',
      content: 'Bạn có chắc chắn muốn xóa thông tin báo cáo này không?',
      buttons: {
        confirm: {
          text: 'Đồng ý',
          action: async () => {
            let response = await reportService.deleteReportPV(id);
            if (response.status === 200) {
              $.alert({
                title: 'Thông báo',
                content: 'Đã xóa thông tin báo cáo thành công!'
              });
            } else {
              $.alert({
                type: 'red',
                title: 'Thông báo',
                content: 'Lỗi không xác định!'
              });
            }
            reportDetail();
          }
        },
        cancel: {
          text: 'Hủy bỏ'
        }
      }
    })
  }
  const reportDetail = async () => {
    let res = await reportService.getReportPV(userName, customerId, projectId);
    if (res.status == 200) {
      setListReport(res.data.listReport);
      setDevice(res.data.listDevice);
    }
  }
  const getDeviceName = (deviceId) => {
    let deviceName = "";
    if (deviceId === "all") {
      return deviceName = "Tất cả thiết bị";
    }

    for (let index = 0; index < device.length; index++) {
      const d = device[index];
      if (d.device_id === parseInt(deviceId)) {
        return d.device_name;
      }
    }
  }

  const getReportName = (reportId) => {
    let reportName = "";
    if (reportId == 1) {
      return reportName = "Báo cáo trong ngày "
    } else if (reportId == 2) {
      return reportName = "Báo cáo trong tháng "
    } else {
      return reportName = "Báo cáo trong năm "
    }
  }

  const addReport = async () => {
    let day = date.getDate() >= 10 ? date.getDate() : `0${date.getDate()}`;
    let month = date.getMonth() + 1 >= 10 ? date.getMonth() + 1 : `0${date.getMonth() + 1}`;
    let year = date.getFullYear();
    let _date = "";
    let deviceId = document.getElementById("selectDevice").value;
    if (dateType == 1) {
      _date = `${year}-${month}-${day}`;
    } else if (dateType == 2) {
      _date = `${year}-${month}`;
    } else {
      _date = `${year}`;
    }

    let data = {
      reportType: dateType,
      date: _date,
      projectId,
      deviceId
    }
    let response = await reportService.addReportPV(data, userName, customerId);
    if (response.status == 200) {
      reportDetail();
      let res = await reportService.generateReportsPV(data, userName, customerId, profiledata);
      if (res.status == 200) {
        reportDetail();
      }
    } else {
      $.alert({
        title: 'Thông báo!',
        content: 'Không có dữ liệu!',
      });
    }

  }
  useEffect(() => {
    document.title = "Báo cáo";
    reportDetail();
    profile();
  }, [projectId, state]);
  return (
    <div className="tab-content">

      <div className="d-flex flex-row-reverse justify-content-between">
        <div className="tab-chart m-0" style={{ border: "none", width: "calc(100% - 10px)" }}>
          <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "5px" }}>
            <span className="project-tree">{projectInfo}</span>
          </div>
          <div className="form-group mb-0 ml-2 mt-2">

            <div className="input-group float-left" style={{ width: "270px" }}>
              <div className="input-group-prepend">
                <span className="input-group-text pickericon">
                  <span className="far fa-calendar"></span>
                </span>
              </div>
              {
                dateType === 1 &&
                <Calendar
                  id="to-value"
                  className=""
                  dateFormat="yy-mm-dd"
                  value={date}
                  onChange={e => setDate(e.value)} />
              }
              {
                dateType === 2 &&
                <Calendar
                  id="to-value"
                  className=""
                  view={"month"}
                  dateFormat="yy-mm"
                  value={date}
                  onChange={e => setDate(e.value)} />
              }
              {
                dateType === 3 &&
                <Calendar
                  id="to-value"
                  className=""
                  view={"year"}
                  dateFormat="yy"
                  value={date}
                  onChange={e => setDate(e.value)} />
              }
            </div>
            <div className="input-group float-left mr-1" style={{ width: "100px", height: 31.25 }}>
              <select className="form-control"
                onChange={(e) => handleChangeChartType(e.target.value)}
                style={{ backgroundColor: "#FFA87D", borderRadius: 0, border: "1px solid #FFA87D" }}>
                <option value={1}>Ngày</option>
                <option value={2}>Tháng</option>
                <option value={3}>Năm</option>
              </select>
            </div>

            <div className="report-search-item input-group float-left ml-2" style={{ width: "250px", height: "31.25px" }} >
              <div className="input-group-prepend select-toggle">
                <span className="input-group-text pickericon" style={{ height: "31.25px" }} onClick={() => changeSearch()}>
                  <span className="fa fa-server" ></span>
                </span>
                {
                  selectedInputDevice ?
                    <select className='from-select' name="deviceId" style={{ width: "220px" }} id="selectDevice" >
                      <option value="all">Tất cả</option>
                      {device?.map(m => {
                        return <option key={m.device_id} value={m.device_id}>{m.device_name}</option >
                      })
                      }

                    </select> :
                    <>
                      <div className="complete" style={{ zIndex: '99' }}>
                        <input id="device-search" className="search-hdevice " type="text" name="deviceId"
                          placeholder="Tìm kiếm thiết bị" onChange={e => {
                            let deviceName = e.target.value;
                            setDeviceName(deviceName);
                            let submit = document.getElementById("btn-export-report");
                            let devices = device?.filter(d => {
                              return d.device_name.toLowerCase().includes(deviceName);
                            });
                            if (deviceName === "") {
                              setListDevice([]);
                            } else {
                              setListDevice(devices);
                            }
                            if (devices.length === 0) {
                              submit.disabled = true;
                            } else {
                              let devicesName = devices?.filter(d => {
                                return d.device_name.toLowerCase().includes(deviceName);
                              });
                              devicesName.length === 1 ? submit.disabled = false : submit.disabled = true;
                            }
                          }} />
                        {
                          listDevice?.map((m, index) => {
                            return <div className="autocomplete" key={index}>
                              <div className="form-control" onClick={() => searchDevice(m.device_name)} >{m.device_name}</div>
                            </div>
                          })
                        }
                      </div>
                    </>
                }
              </div>
            </div>
            <div className="report-search-item input-group float-left ml-3" style={{ width: "250px" }}>
              <div className="input-group-prepend select-toggle">
                <span className="input-group-text pickericon">
                  <span className="fa fa-clipboard"></span>
                </span>
                <select className="from-select" style={{ width: "220px" }} name="reportType" id="reportTyte" >
                  <option value={1}>{reportType.type1}</option>
                  {/* <option value={2}>{reportType.type2}</option>
                <option value={3}>{reportType.type3}</option>
                <option value={4}>{reportType.type4}</option>
                <option value={5}>{reportType.type5}</option> */}
                </select>
              </div>
            </div>

            <div>
              <button type="button" className="btn btn-outline-warning ml-3" style={{ height: 31.25, padding: 0 }}
                onClick={addReport}
              >
                <i className="fa-solid fa-download"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
      {/* Table */}
      <div className="table-report ml-2"
        style={{ paddingTop: "30px" }}>
        <table className="table text-center tbl-overview table-oper-info-tool" style={{ position: "absolute" }}>
          <thead>
            <tr>
              <th width="40px">TT</th>
              <th width="100px">Thời gian</th>
              <th>Loại báo cáo</th>
              <th>Tên thiết bị</th>
              <th width="150px">Trạng thái</th>
              <th width="150px">Ngày tạo</th>
              <th width="150px">Ngày cập nhật</th>
              <th width="60px"><i className="fa-regular fa-hand"></i></th>
            </tr>
          </thead>
          <tbody>
            {
              listReport?.map((report, index) => {
                return (
                  <tr key={index}>
                    <td>{index + 1}</td>
                    <td style={{ wordWrap: "break-word", textAlign: "center" }}> {report.dateType}</td>
                    <td style={{ wordWrap: "break-word", textAlign: "left" }}>{getReportName(report.reportId)} </td>
                    <td style={{ wordWrap: "break-word", textAlign: "center" }}> {getDeviceName(report.deviceId)} </td>
                    <td style={{ wordWrap: "break-word", textAlign: "center" }}>
                      {report.status === 1 ? "Xong" : <ProgressBar value={report.percent} style={{ fontSize: "11px", height: "15px" }}></ProgressBar>}
                    </td>
                    <td style={{ wordWrap: "break-word" }}>{moment(report.reportDate).format(CONS.DATE_FORMAT)}</td>
                    <td style={{ wordWrap: "break-word" }}>
                      {report.updated != null ? <>{moment(report.updated).format(CONS.DATE_FORMAT)}</> : ""}
                    </td>
                    < td className="text-center">
                      {
                        report.status == 1 ?
                          <>
                            <img height="16px" src="/resources/image/icon-download.png" alt="download" onClick={() => download(report.url)} className="mr-1" />
                            <img height="16px" src="/resources/image/icon-delete.png" alt="Xóa" onClick={() => deleteReport(report.id)} />
                          </>
                          : <></>
                      }
                    </td>
                  </tr>
                )

              })

            }
          </tbody>
        </table>
      </div>
    </div >
  )
}

export default ReportSolar 