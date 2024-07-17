import React, { useEffect, useState } from "react";
import { useRef } from "react";
import { useParams, useHistory, useLocation } from "react-router-dom";
import "./index.css";
import { useFormik } from "formik";
import $ from "jquery";
import projectService from "../../../../services/ProjectService";
import deviceService from "../../../../services/DeviceService";
import reportService from "../../../../services/ReportService";
import customerService from "../../../../services/CustomerService";
import objectService from "../../../../services/ObjectService";
import loadTypeService from "../../../../services/LoadTypeService";
import settingService from "../../../../services/SettingShiftService";
import unitService from "../../../../services/UnitService";
import { useTranslation } from "react-i18next";
import { MultiSelect } from "react-multi-select-component";
import * as Yup from "yup";
import moment from "moment";
import CONS from "../../../../constants/constant";
import { PatternFormat } from "react-number-format";
import { Calendar } from "primereact/calendar";
import { locale, addLocale } from "primereact/api";
import { wait } from "@testing-library/user-event/dist/utils";
import Select from "react-select";
import { count } from "d3";
const Report = () => {
  addLocale("vn", {
    monthNames: [
      "Tháng 1",
      "Tháng 2",
      "Tháng 3",
      "Tháng 4",
      "Tháng 5",
      "Tháng 6",
      "Tháng 7",
      "Tháng 8",
      "Tháng 9",
      "Tháng 10",
      "Tháng 11",
      "Tháng 12",
    ],
    monthNamesShort: [
      "T1",
      "T2",
      "T3",
      "T4",
      "T5",
      "T6",
      "T7",
      "T8",
      "T9",
      "T10",
      "T11",
      "T12",
    ],
  });

  locale("vn");

  const param = useParams();
  const location = useLocation();
  const [type, setType] = useState(0);
  const [selectedLayer, setSelectedLayer] = useState([]);
  const { t } = useTranslation();

  const [errorsValidate, setErrorsValidate] = useState([]);

  const [error, setError] = useState(null);
  const history = useHistory();
  const [customerId, setCustomerId] = useState(0);
  const [systemTypeId, setSystemTypeId] = useState(1);
  const [customerName, setCustomerName] = useState(0);
  const [customerDescription, setCustomerDescription] = useState(0);
  const [projectId, setProjectId] = useState(0);

  const [areas, setAreas] = useState([]);
  const [fromDate, setFromDate] = useState(new Date());
  const [toDate, setToDate] = useState(new Date());
  const [checkDate, setCheckDate] = useState(new Date());
  const [typeTime, setTypeTime] = useState(1);
  const [reportName, setReportName] = useState(
    "Báo cáo sử dụng năng lượng tổng"
  );
  const [shift1, setshift1] = useState();
  const [shift2, setshift2] = useState();
  const [shift3, setshift3] = useState();
  const [loadTypes, setLoadTypes] = useState([]);
  const [selectedCompare, setSelectedCompare] = useState([]);
  const [optionCompare, setOptionCompare] = useState([])

  const callbackFunction = (childData) => {
    console.log(childData);
  }

  const initialValues = {
    reportName: "",
    reportTemplate: "",
    reportModule: "",
    reportSite: "",
    reportDevices: [],
    reportArea: "",
    reportLoad: "",
    reportDeviceType: "",
    reportCa1: "",
    reportCa2: "",
    reportCa3: "",
    reportUnit: "",
    reportTypeTime: "",
    reportFromDate: "",
    reportToDate: "",
    customerId: "",
    customerName: "",
    customerDescription: "",
    type: "",
  };


  const funcGetCustomerId = async () => {
    let cusId = param.customerId;
    let res = await customerService.getCustomer(cusId);
    if (res.status == 200) {
      setCustomerName(() => res.data.customerName);
      setCustomerDescription(() => res.data.description);
    }
  };

  const [options, setOptions] = useState([])

  const funcSetType = async (typeSelected, text) => {
    $('#deviceTypeReport').html("");
    $('#areaReport').html("");
    $('#phutaiReport').html("");
    setType(() => typeSelected)
    setReportName(() => text)
    if (typeSelected == 0 || typeSelected == 8) {
      await listDevice(typeSelected);
    } else if (typeSelected == 3 || typeSelected == 11) {
      await listObjectType(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 1 || typeSelected == 9) {
      await listArea(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 2 || typeSelected == 10) {
      await listLoad(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 4) {
      await getShifts(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 5) {
      await getShifts(typeSelected);
      await listArea(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 6) {
      await getShifts(typeSelected);
      await listLoad(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 7) {
      await getShifts(typeSelected);
      await listObjectType(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 12) {
      await listUnit();
      await listDevice(typeSelected);
    } else if (typeSelected == 14) {
      await listArea(typeSelected);
    } else if (typeSelected == 15) {
      await listLoad(typeSelected);
    } else if (typeSelected == 16) {
      await listObjectType(typeSelected);
    } else if (typeSelected == 17) {
      await listDevice(typeSelected);
    } else if (typeSelected == 18) {
      await listUnit();
      await listArea(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 19) {
      await listUnit();
      await listLoad(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 20) {
      await listUnit();
      await listObjectType(typeSelected);
      await listDevice(typeSelected);
    } else if (typeSelected == 21) {
      await listArea(typeSelected);
    } else if (typeSelected == 22) {
      await listLoad(typeSelected);
    } else if (typeSelected == 23) {
      await listObjectType(typeSelected);
    } else if (typeSelected == 24 || typeSelected == 25) {
      await listDevice(typeSelected);
    }
  }

  const funcHandleChange = (event) => {
    setReportName(() => event.target.value);
  };

  const funcSetTypeTime = (e) => {
    setTypeTime(() => e.target.value);
    setFromDate(new Date());
    setToDate(new Date());
    if (e.target.value == 1) {
      let date = new Date();
      date.setDate(date.getDate() - 31);
      setCheckDate(date);
    } else if (e.target.value == 2) {
      let date = new Date();
      date.setMonth(date.getMonth() - 12);
      setCheckDate(date);
    } else if (e.target.value == 3) {
      let date = new Date();
      date.setFullYear(date.getFullYear() - 5);
      setCheckDate(date);
    }
  };

  const formik = useFormik({
    initialValues,
    onSubmit: async (data) => {
      let devices = [];
      var string = JSON.stringify(selectedLayer);
      var jsonData = JSON.parse(string);
      for (var i = 0; i < jsonData.length; i++) {
        var counter = jsonData[i];
        devices.push(counter.value);
      }
      data.reportDevices = "";
      data.reportFromDate = moment(fromDate).format(`YYYY-MM-DD`);
      data.reportToDate = moment(toDate).format(`YYYY-MM-DD`);
      data.type = type;
      data.customerId = customerId;
      data.reportTemplate = document.getElementById(`reportTemplate`).value;
      data.reportSite = document.getElementById(`reportSite`).value;
      data.reportModule = document.getElementById(`reportModule`).value;
      data.reportTypeTime = document.getElementById(`typeTimeReport`).value;
      data.reportName = document.getElementById(`reportName`).value;
      let newReport = {
        reportName: data.reportName,
        reportTemplate: data.reportTemplate,
        reportModule: data.reportModule,
        reportSite: data.reportSite,
        reportDevices: data.reportDevices,
        reportArea: data.reportArea,
        reportLoad: data.reportLoad,
        reportDeviceType: data.reportDeviceType,
        reportCa1: data.reportCa1,
        reportCa2: data.reportCa2,
        reportCa3: data.reportCa3,
        reportUnit: data.reportUnit,
        reportTypeTime: data.reportTypeTime,
        reportFromDate: data.reportFromDate,
        reportToDate: data.reportToDate,
        type: data.type,
        customerId: data.customerId,
        customerName: customerName,
        customerDescription: customerDescription,
      };
      let response = await reportService.downloadNewReport(newReport);
      if (response.status === 200) {
      } else if (response.status === 500) {
        setError(t("validate.project.INSERT_FAILED"));
      } else if (response.status === 400) {
        setErrorsValidate(Array.from(new Set(response.data.errors)));
      } else {
        $.alert({
          title: "Thông báo!",
          content: "Không có dữ liệu!",
        });
      }
    },
  });

  const getInfoAdd = async (typeSelected) => {
    listProject(typeSelected);
  };

  const listProject = async (typeSelected) => {
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      $(".input-project-m").show();
      let res = await projectService.getProjectByCustomerId(customerId);
      $("#reportSite").html("");
      if (res.data.length > 0) {
        $("#reportSite").prop("disable", false);
        let data = res.data;
        $.each(data, function (index, value) {
          $("#reportSite").append(
            '<option value="' +
            value.projectId +
            '">' +
            value.projectName +
            "</option>"
          );
        });
      } else {
        $("#reportSite").prop("disable", true);
      }
      if (typeSelected == 0 || typeSelected == 8) {
        await listDevice(typeSelected);
      } else if (typeSelected == 3 || typeSelected == 11) {
        await listObjectType(typeSelected);
        await listDevice(typeSelected);
      } else if (typeSelected == 1 || typeSelected == 9) {
        await (typeSelected);
        await listDevice(typeSelected);
      } else if (typeSelected == 2 || typeSelected == 10) {
        await listLoad(typeSelected);
        await listDevice(typeSelected);
      } else if (typeSelected == 4) {
        await getShifts(typeSelected);
        await listDevice(typeSelected);
      } else if (typeSelected == 5) {
        await getShifts(typeSelected);
        await listArea(typeSelected);
        await listDevice(typeSelected);
      } else if (typeSelected == 6) {
        await getShifts(typeSelected);
        await listLoad(typeSelected);
        await listDevice(typeSelected);
      } else if (typeSelected == 7) {
        await getShifts(typeSelected);
        await listObjectType(typeSelected);
        await listDevice(typeSelected);
      }
    }
  }



  const listObjectType = async (typeSelected) => {
    setSelectedCompare(() => [])
    while (optionCompare.length > 0) {
      optionCompare.pop();
    }
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      $(".input-object-type-m").show();
      let projectId = document.getElementById("reportSite").value;
      setProjectId(projectId);
      let typeSystem = document.getElementById("reportModule").value;
      let res = await objectService.getListObjectTypeBySystemTypeIdAndProjectId(typeSystem, projectId);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          optionCompare.push({ label: res.data[i].objectTypeName, value: res.data[i].objectTypeId });
        }
      }
      $('#deviceTypeReport').html("");
      if (res.data.length > 0) {
        $('#deviceTypeReport').prop('disable', false);
        let data = res.data;
        $.each(data, function (index, value) {
          $('#deviceTypeReport').append('<option value="' + value.objectTypeId + '">' + value.objectTypeName + '</option>')
        });
      } else {
        $('#deviceTypeReport').prop('disable', true);
      }
    } else {
      $(".input-project-m").hide();
    }
  };

  const listArea = async (typeSelected) => {
    setSelectedCompare(() => [])
    while (optionCompare.length > 0) {
      optionCompare.pop();
    }
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      $(".input-area-m").show();
      let projectId = document.getElementById("reportSite").value;
      setProjectId(projectId);
      let typeSystem = document.getElementById("reportModule").value;
      let res = await objectService.getListAreaBySystemTypeIdAndProjectId(typeSystem, projectId);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          optionCompare.push({ label: res.data[i].area, value: res.data[i].area });
        }
      }
      $('#areaReport').html("");
      if (res.data.length > 0) {
        $('#areaReport').prop('disable', false);
        let data = res.data;
        $.each(data, function (index, value) {
          $('#areaReport').append('<option value="' + index + '">' + value.area + '</option>')
        });
      }
      else {
        $("#areaReport").prop("disable", true);
      }
    } else {
      $(".input-area-m").hide();
    }
  };

  const listLoad = async (typeSelected) => {
    setSelectedCompare(() => [])
    while (optionCompare.length > 0) {
      optionCompare.pop();
    }
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      $(".input-load-m").show();
      let projectId = document.getElementById("reportSite").value;
      setProjectId(projectId);
      let typeSystem = document.getElementById("reportModule").value;
      let res = await loadTypeService.getListLoadBySystemTypeIdAndProjectId(typeSystem, projectId);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          optionCompare.push({ label: res.data[i].loadTypeName, value: res.data[i].loadTypeId });
        }
      }
      $('#phutaiReport').html("");
      if (res.data.length > 0) {
        $('#phutaiReport').prop('disable', false);
        let data = res.data;

        $.each(data, function (index, value) {
          $("#phutaiReport").append(
            '<option value="' +
            value.loadTypeId +
            '">' +
            value.loadTypeName +
            "</option>"
          );
        });
      } else {
        $("#phutaiReport").prop("disable", true);
      }
    } else {
      $(".input-load-m").hide();
    }
  };

  const getShifts = async (typeSelected) => {
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      let projectId = document.getElementById("reportSite").value;
      setProjectId(projectId);
      let typeSystem = document.getElementById("reportModule").value;
      let res = await settingService.getSettingByProjectId(customerId, typeSystem, projectId)
      if (res.status == 200) {
        if (res.data) {
          setshift1(res.data.shift1)
          setshift2(res.data.shift2)
          setshift3(res.data.shift3)
        } else {
          setshift1('')
          setshift2('')
          setshift3('')
        }

      }
    }
  }

  const listUnit = async () => {
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      let res = await unitService.getUnit();
      $('#unitReport').html("");
      if (res.data.length > 0) {
        $('#unitReport').prop('disable', false);
        let data = res.data;

        $.each(data, function (index, value) {
          $('#unitReport').append('<option value="' + value.unitId + '">' + value.unitName + '</option>')
        });
      } else {
        $('#unitReport').prop('disable', true);
      }
    } else {
    }
  }

  const listDevice = async (typeSelected) => {
    setSelectedLayer(() => [])
    while (options.length > 0) {
      options.pop();
    }
    setSelectedCompare(() => [])
    while (optionCompare.length > 0) {
      optionCompare.pop();
    }
    let customerId = param.customerId;
    setCustomerId(customerId);
    let projectId = $("#reportSite option:selected").val();
    setProjectId(projectId);
    let typeSystem = $("#reportModule option:selected").val();
    let objectType = $("#deviceTypeReport option:selected").val();
    let load = $("#phutaiReport option:selected").val();
    let area = $("#areaReport option:selected").text();
    if (typeSelected == 0 || typeSelected == 4 || typeSelected == 8 || typeSelected == 12) {
      let res = await deviceService.getListDeviceCalculateFlag(projectId, typeSystem);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          options.push({ label: res.data[i].deviceName, value: res.data[i].deviceId });
        }
      }
    } else if (typeSelected == 3 || typeSelected == 7 || typeSelected == 11 || typeSelected == 20) {
      let res = await deviceService.getListDeviceAllFlag(projectId, typeSystem, null, null, objectType);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          options.push({ label: res.data[i].deviceName, value: res.data[i].deviceId });
        }
      }
    } else if (typeSelected == 1 || typeSelected == 5 || typeSelected == 9 || typeSelected == 18) {
      let res = await deviceService.getListDeviceAllFlag(projectId, typeSystem, area, null, null);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          options.push({ label: res.data[i].deviceName, value: res.data[i].deviceId });
        }
      }
    } else if (typeSelected == 2 || typeSelected == 6 || typeSelected == 10 || typeSelected == 19) {
      let res = await deviceService.getListDeviceAllFlag(projectId, typeSystem, null, load, null);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          options.push({ label: res.data[i].deviceName, value: res.data[i].deviceId });
        }
      }
    } else if (typeSelected == 24 || typeSelected == 25) {
      let res = await deviceService.getListDeviceAllFlag(projectId, typeSystem, null, null, null);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          optionCompare.push({ label: res.data[i].deviceName, value: res.data[i].deviceId });
        }
      }
    } else if (typeSelected == 17) {
      let res = await deviceService.getListDeviceAllFlag(projectId, typeSystem, null, null, null);
      if (res.status === 200 && parseInt(res.data.length) > 0) {
        var i;
        for (i = 0; i < res.data.length; i++) {
          optionCompare.push({ label: res.data[i].deviceName, value: res.data[i].deviceId });
        }
      }
    }
  }



  const funcChangeModuleAndSite = async () => {
    if (type == 0 || type == 8 || type == 12) {
      await listDevice(type);
    } else if (type == 3 || type == 11 || type == 20) {
      await listObjectType(type);
      await listDevice(type);
    } else if (type == 1 || type == 9 || type == 18) {
      await listArea(type);
      await listDevice(type);
    } else if (type == 2 || type == 10 || type == 19) {
      await listLoad(type);
      await listDevice(type);
    } else if (type == 4) {
      await getShifts(type);
      await listDevice(type);
    } else if (type == 5) {
      await getShifts(type);
      await listArea(type);
      await listDevice(type);
    } else if (type == 6) {
      await getShifts(type);
      await listArea(type);
      await listDevice(type);
    } else if (type == 7) {
      await getShifts(type);
      await listObjectType(type);
      await listDevice(type);
    } else if (type == 17) {
      await listDevice(type);
    } else if (type == 21) {
      await listArea(type);
    } else if (type == 22) {
      await listLoad(type);
    } else if (type == 23) {
      await listObjectType(type);
    } else if (type == 24 || type == 25) {
      await listDevice(type);
    }
  };

  const funcOpenReportType = event => {
    let subMenu = document.getElementById(event.currentTarget.id).nextSibling;
    if (subMenu.style.display == 'block') {
      subMenu.style.display = 'none'
    } else {
      subMenu.style.display = 'block'
    }
  }

  const funcCheck = (e) => {
    if (typeTime == 1) {
      const date1 = new Date(moment(fromDate).format("YYYY-MM-DD"));
      const date2 = new Date(moment(e.target.value).format("YYYY-MM-DD"));

      var date = new Date(date2);
      date.setDate(date.getDate() - 31);
      setCheckDate(date);
      const diffTime = date2 - date1;
      let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      if (diffDays < 0) {
        setFromDate(e.value);
      } else if (diffDays > 31) {
        setFromDate(date);
      }
    } else if (typeTime == 2) {
      const date1 = new Date(moment(fromDate).format("YYYY-MM"));
      const date2 = new Date(moment(e.target.value).format("YYYY-MM"));

      var date = new Date(date2);
      date.setMonth(date.getMonth() - 12);
      setCheckDate(date);
      const diffTime = date2 - date1;
      let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24 * 30));
      if (diffDays < 0) {
        setFromDate(e.value);
      } else if (diffDays > 12) {
        setFromDate(date);
      }
    } else if (typeTime == 3) {
      const date1 = new Date(moment(fromDate).format("YYYY-MM"));
      const date2 = new Date(moment(e.target.value).format("YYYY-MM"));

      var date = new Date(date2);
      date.setFullYear(date.getFullYear() - 5);
      setCheckDate(date);
      const diffTime = date2 - date1;
      let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24 * 365));
      if (diffDays < 0) {
        setFromDate(e.value);
      } else if (diffDays > 5) {
        setFromDate(date);
      }
    }
  };

  const funcReset = () => {
    setType(() => 0);
    setTypeTime(() => 1);
    setFromDate(new Date());
    setToDate(new Date());
    setReportName("Báo cáo sử dụng năng lượng tổng");
    let element = document.getElementById("typeTimeReport");
    element.value = 1;
    let elementModule = document.getElementById("reportModule");
    elementModule.value = 1;
    var date = new Date();
    date.setDate(date.getDate() - 31);
    setCheckDate(date);
    setAreas($("#areaReport option:selected").text());
    setSystemTypeId(() => 1);
    $("#deviceTypeReport").html("");
  };

  const funcDownload = async () => {
    let devices = []
    var string = JSON.stringify(selectedLayer)
    var jsonData = JSON.parse(string);
    var countDevice = 0;
    for (var i = 0; i < jsonData.length; i++) {
      var counter = jsonData[i];
      devices.push(counter.value);
      countDevice++;
    }
    devices.sort();

    let listCompare = []
    var string = JSON.stringify(selectedCompare)
    var jsonData = JSON.parse(string);
    for (var i = 0; i < jsonData.length; i++) {
      var counter = jsonData[i];
      listCompare.push(counter.value);
    }

    // loại báo cáo
    let typeInfor = type
    // danh sách điểm đo
    let devicesInfor = devices.toString()
    // danh sách khu vực OR phụ tải OR loại thiết bị OR điểm đo
    let listInfor = listCompare.toString()
    // thông tin khách hàng
    let customerInfor = customerId + "@" + customerName + "@" + customerDescription
    //loại thời gian và khoảng thời gian
    let timeInfor = ""
    if (typeTime == 1) {
      timeInfor = typeTime + "@" + moment(fromDate).format(`YYYY-MM-DD`) + "@" + moment(toDate).format(`YYYY-MM-DD`)
    } else if (typeTime == 2) {
      timeInfor = typeTime + "@" + moment(fromDate).format(`YYYY-MM`) + "@" + moment(toDate).format(`YYYY-MM`)
    } else if (typeTime == 3) {
      timeInfor = typeTime + "@" + moment(fromDate).format(`YYYY`) + "@" + moment(toDate).format(`YYYY`)
    }
    let reportSite = document.getElementById(`reportSite`).value
    let reportModule = document.getElementById(`reportModule`).value
    //site-module
    let siteModuleInfor = reportSite + "@" + reportModule

    let reportTemplate = document.getElementById(`reportTemplate`).value;
    let reportName = document.getElementById(`reportName`).value;
    //mẫu-tên
    let reportInfor = reportTemplate + "@" + reportName;

    let areaReport = $("#areaReport option:selected").text();
    let phutaiReport = $("#phutaiReport option:selected").text();
    let loadTypeId = $("#phutaiReport option:selected").val();
    let deviceTypeReport = $("#deviceTypeReport option:selected").text();
    let deviceTypeId = $("#deviceTypeReport option:selected").val();
    let strengthReport = document.getElementById(`strengthReport`).value;
    let unitReport = document.getElementById(`unitReport`).value
    let unitName = $("#unitReport option:selected").text();
    //khu-phụ tải- loại thiết bị-
    let categoryInfor = areaReport + "@" + phutaiReport + "@" + deviceTypeReport + "@" + deviceTypeId + "@" + loadTypeId + "@" + selectedCompare.value;
    //Ca làm việc
    let shiftInfor = shift1 + "@" + shift2 + "@" + shift3
    //Đơn vị-Cường độ
    let unitInfor = unitReport + "@" + unitName + "@" + strengthReport
    let response = await reportService.downloadNewReport(typeInfor, reportInfor, customerInfor, devicesInfor, timeInfor, siteModuleInfor, categoryInfor, shiftInfor, unitInfor, listInfor);
    if (response.status === 200) {
    } else {
      $.alert({
        title: 'Thông báo!',
        content: 'Không có dữ liệu!',
      });
    }
  };


  useEffect(() => {
    document.title = "Báo cáo";
    funcGetCustomerId();
    getInfoAdd(0);
    funcReset();
  }, [param.customerId]);

  return (
    <div>
      <div>
        <div className="report-zone1">
          <div className="submenu-report">
            <div className="main-submenu" id="reportType1" style={type == 0 || type == 1 || type == 2 || type == 3 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcOpenReportType(e)}>
              <i className="fa fa-bolt ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo sử dụng năng lượng
            </div>
            <div className="sub-submenu" style={{ display: "block" }}>
              <div className="parameter" title="năng lượng tổng" style={type == 0 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(0, "Báo cáo sử dụng năng lượng tổng")}>Sử dụng năng lượng tổng</div>
              <div className="parameter" title="năng lượng theo khu vực" style={type == 1 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(1, "Báo cáo sử dụng năng lượng theo khu vực")}>Sử dụng năng lượng theo khu vực</div>
              <div className="parameter" title="năng lượng theo phụ tải" style={type == 2 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(2, "Báo cáo sử dụng năng lượng theo phụ tải")}>Sử dụng năng lượng theo phụ tải</div>
              <div className="parameter" title="năng lượng theo loại thiết bị" style={type == 3 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(3, "Báo cáo sử dụng năng lượng theo loại thiết bị")}>Sử dụng năng lượng theo loại thiết bị</div>
            </div>
          </div>
          <div className="submenu-report">
            <div className="main-submenu" id="reportType2" title="Báo cáo sử dụng năng lượng theo ca làm việc" style={type == 4 || type == 5 || type == 6 || type == 7 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcOpenReportType(e)}>
              <i className="fa fa-puzzle-piece ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo sử dụng năng lượng theo ca làm việc
            </div>
            <div className="sub-submenu">
              <div className="parameter" title="năng lượng tổng theo ca làm việc" style={type == 4 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(4, "Báo cáo sử dụng năng lượng tổng theo ca làm việc")}>Sử dụng năng lượng tổng theo ca làm việc</div>
              <div className="parameter" title="năng lượng theo khu vực theo ca làm việc" style={type == 5 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(5, "Báo cáo sử dụng năng lượng khu vực theo ca làm việc")}>Sử dụng năng lượng khu vực theo ca làm việc</div>
              <div className="parameter" title="năng lượng theo phụ tải theo ca làm việc" style={type == 6 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(6, "Báo cáo sử dụng năng lượng phụ tải theo ca làm việc")}>Sử dụng năng lượng phụ tải theo ca làm việc</div>
              <div className="parameter" title="năng lượng theo loại thiết bị theo ca làm việc" style={type == 7 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(7, "Báo cáo sử dụng năng lượng loại thiết bị theo ca làm việc")}>Sử dụng năng lượng loại thiết bị theo ca làm việc</div>
            </div>
          </div>
          <div className="submenu-report">
            <div className="main-submenu" id="reportType3" title="Báo cáo tiền điện" style={type == 8 || type == 9 || type == 10 || type == 11 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcOpenReportType(e)}>
              <i className="fa fa-money-check-dollar ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo tiền điện
            </div>
            <div className="sub-submenu">
              <div className="parameter" title="tiền điện tổng" style={type == 8 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(8, "Báo cáo tiền điện tổng")}>Tiền điện tổng</div>
              <div className="parameter" title="tiền điện theo khu vực" style={type == 9 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(9, "Báo cáo tiền điện tổng theo khu vực")}>Tiền điện theo khu vực</div>
              <div className="parameter" title="tiền điện theo phụ tải" style={type == 10 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(10, "Báo cáo tiền điện tổng theo phụ tải")}>Tiền điện theo phụ tải</div>
              <div className="parameter" title="tiền điện theo loại thiết bị" style={type == 11 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(11, "Báo cáo tiền điện tổng theo loại thiết bị")}>Tiền điện theo loại thiết bị</div>
            </div>
          </div>
          <div className="submenu-report">
            <div className="main-submenu" id="reportType4" title="Báo cáo cường độ sử dụng năng lượng" style={type == 12 || type == 18 || type == 19 || type == 20 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcOpenReportType(e)}>
              <i className="fa fa-bars ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo cường độ sử dụng năng lượng
            </div>
            <div className="sub-submenu">
              <div className="parameter" title="cường độ sử dụng năng lượng tổng" style={type == 12 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(12, "Báo cáo cường độ sử dụng năng lượng tổng")}>Sử dụng năng lượng tổng</div>
              <div className="parameter" title="cường độ sử dụng năng lượng theo khu vực" style={type == 18 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(18, "Báo cáo cường độ sử dụng năng lượng theo khu vực")}>Sử dụng năng lượng theo khu vực</div>
              <div className="parameter" title="cường độ sử dụng năng lượng theo phụ tải" style={type == 19 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(19, "Báo cáo cường độ sử dụng năng lượng theo phụ tải")}>Sử dụng năng lượng theo phụ tải</div>
              <div className="parameter" title="cường độ sử dụng năng lượng theo loại thiết bị" style={type == 20 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(20, "Báo cáo cường độ sử dụng năng lượng theo loại thiết bị")}>Sử dụng năng lượng theo loại thiết bị</div>
            </div>
          </div>
          <div className="submenu-report">
            <div className="main-submenu" id="reportType5" title="Báo cáo cảnh báo" style={type == 13 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcOpenReportType(e)}>
              <i className="fa fa-plug-circle-exclamation ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo cảnh báo
            </div>
            <div className="sub-submenu">
              <div className="parameter" title="Báo cáo cảnh báo tổng" style={type == 13 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(13, "Báo cáo cảnh báo tổng")}>Cảnh báo tổng</div>
              <div className="parameter" title="Báo cáo cảnh báo theo khu vực" style={type == 14 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(14, "Báo cáo cảnh báo theo khu vực")}>Cảnh báo theo khu vực</div>
              <div className="parameter" title="Báo cáo cảnh báo theo phụ tải" style={type == 15 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(15, "Báo cáo cảnh báo theo phụ tải")}>Cảnh báo theo phụ tải</div>
              <div className="parameter" title="Báo cáo cảnh báo theo loại thiết bị" style={type == 16 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(16, "Báo cáo cảnh báo theo thiết bị")}>Cảnh báo theo thiết bị</div>
              <div className="parameter" title="Báo cáo cảnh báo theo điểm đo" style={type == 17 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(17, "Báo cáo cảnh báo theo điểm đo")}>Cảnh báo theo điểm đo</div>
            </div>
          </div>
          <div className="submenu-report">
            <div className="main-submenu" id="reportType6" title="Báo cáo so sánh sử dụng năng lượng" style={type == 21 || type == 22 || type == 23 || type == 24 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcOpenReportType(e)}>
              <i className="fa fa-magnifying-glass-chart ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo so sánh sử dụng năng lượng
            </div>
            <div className="sub-submenu">
              <div className="parameter" title="so sánh khu vực" style={type == 21 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(21, "Báo cáo so sánh sử dụng năng lượng khu vực")}>So sánh khu vực</div>
              <div className="parameter" title="so sánh loại phụ tải" style={type == 22 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(22, "Báo cáo so sánh sử dụng năng lượng loại phụ tải")}>So sánh loại phụ tải</div>
              <div className="parameter" title="so sánh loại thiết bị" style={type == 23 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(23, "Báo cáo so sánh sử dụng năng lượng loại thiết bị")}>So sánh loại thiết bị</div>
              <div className="parameter" title="so sánh điểm đo" style={type == 24 ? { backgroundColor: "#f9a46f" } : {}} onClick={() => funcSetType(24, "Báo cáo so sánh sử dụng năng lượng điểm đo")}>So sánh điểm đo</div>
            </div>
          </div>
          <div className="submenu-report">
            <div className="main-submenu" id="reportType7" title="Báo cáo hiệu suất sử dụng năng lượng" style={type == 25 ? { backgroundColor: "#f37021" } : {}} onClick={(e) => funcSetType(25, "Báo cáo hiệu suất sử dụng năng lượng điểm đo theo ngày")}>
              <i className="fa fa-chess-queen ml-1" style={{ color: "#FFF" }}></i> &nbsp;
              Báo cáo hiệu suất sử dụng năng lượng điểm đo theo ngày
            </div>
          </div>
        </div>
        <div className="report-zone2">
          <table className="table table-input">
            <tbody className="report-tbody">
              <tr>
                <th width="200px">
                  <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i>
                  TÊN BÁO CÁO<span className="required">※</span>
                </th>
                <td colSpan={3}>
                  <input id="reportName" type="text" className="form-control" style={{ fontSize: "13px" }} maxLength={255} onChange={(e) => {
                    funcHandleChange(e);
                  }} value={reportName} />
                </td>
              </tr>
              <tr>
                <th width="200px">
                  <i className="fas fa-solid fa-clone mr-1" style={{ color: "#333" }}></i>MẪU</th>
                <td colSpan={3}>
                  <select id="reportTemplate" className="custom-select block" style={{ fontSize: "13px" }}>
                    <option value={1}>MẪU MẶC ĐỊNH</option>
                  </select>
                </td>
              </tr>
              <tr>
                <th width="200px">
                  <i className="fas fa-solid fa-layer-group mr-1" style={{ color: "#333" }}></i>THÀNH PHẦN</th>
                <td>
                  <select id="reportModule" className="custom-select block" style={{ fontSize: "13px" }} onChange={(e) => {
                    setSystemTypeId(e.target.value)
                    funcChangeModuleAndSite();
                  }}>
                    <option value={1} defaultValue>LOAD</option>
                    <option value={2}>SOLAR</option>
                    <option value={3}>BATTERY</option>
                    <option value={4}>WIND</option>
                    <option value={5}>GRID</option>
                  </select>
                </td>
                <th width="200px">
                  <i className="fas fa-solid fa-folder-open mr-1" style={{ color: "#333" }}></i>DỰ ÁN</th>
                <td className="input-project-m">
                  <select id="reportSite" className="custom-select block" style={{ fontSize: "13px" }} onChange={(e) => {
                    funcChangeModuleAndSite();
                  }}>
                  </select>
                </td>
              </tr>
              <tr hidden={type == 1 || type == 5 || type == 9 || type == 14 || type == 18 ? false : true}>
                <th width="200px">
                  <i className="fas fa-solid fa-map-location mr-1" style={{ color: "#333" }}></i>
                  KHU VỰC
                </th>
                <td colSpan={3} className="input-area-m">
                  <select id="areaReport" className="custom-select block" style={{ fontSize: "13px" }} onChange={(e) => { listDevice(type); }}>

                  </select>
                </td>
              </tr>

              <tr hidden={(type == 2 || type == 6 || type == 10 || type == 15 || type == 19) && systemTypeId == 1 ? false : true}>
                <th width="200px" className="input-load-m">
                  <i className="fas fa-solid fa-spinner mr-1" style={{ color: "#333" }}></i>
                  PHỤ TẢI
                </th>
                <td colSpan={3}>
                  <select id="phutaiReport" className="custom-select block" style={{ fontSize: "13px" }} onChange={(e) => { listDevice(type); }}>
                  </select>
                </td>
              </tr>
              <tr hidden={type == 3 || type == 7 || type == 11 || type == 16 || type == 20 ? false : true}>
                <th width="200px">
                  <i className="fas fa-solid fa-tablet-screen-button mr-1" style={{ color: "#333" }}></i>
                  LOẠI THIẾT BỊ
                </th>
                <td colSpan={3} className="input-object-type-m">
                  <select id="deviceTypeReport" className="custom-select block" style={{ fontSize: "13px" }} onChange={(e) => { listDevice(type); }}>
                  </select>
                </td>
              </tr>
              <tr hidden={(type >= 13 && type <= 17) || type >= 21 ? true : false} >
                <th width="200px">
                  <i className="fas fa-solid fa-tag mr-1" style={{ color: "#333" }}></i>ĐIỂM ĐO</th>
                {/* <td colSpan={3} className="layer-select" >
                  <MultiSelect
                    id="deviceReport"
                    name="reportDevices"
                    options={options}
                    value={selectedLayer}
                    onChange={setSelectedLayer}
                    labelledBy={"Select"}
                    isCreatable={true}
                    overrideStrings={{
                      selectSomeItems: "Lựa chọn một hay nhiều điểm đo...",
                      selectAll: "Chọn tất cả",
                      search: "Tìm kiếm",
                      allItemsAreSelected: "Chọn tất cả điểm đo",
                      selectAllFiltered: "Chọn tất cả theo tìm kiếm",
                      noOptions: "Không có điểm đo",

                    }}
                  />
                </td> */}
                <td colSpan={3}>
                  {/* <SelectDevice projectId={projectId} systemTypeId={systemTypeId} parentCallback={callbackFunction} /> */}
                </td>
              </tr>
              <tr hidden={type === 17 || type === 25 ? false : true}>
                <th width="200px" className="input-load-m">
                  <i className="fas fa-solid fa-spinner mr-1" style={{ color: "#333" }}></i>ĐIỂM ĐO</th>
                <td colSpan={3}>
                  <Select value={selectedCompare}
                    onChange={(e) => setSelectedCompare(e)}
                    id="device"
                    name="device" options={optionCompare}
                    // only allow user to choose up to 3 options 
                    isOptionDisabled={() => selectedCompare.length >= 1}
                    className="basic-multi-select" classNamePrefix="select"
                    placeholder="Chọn điểm đo" noOptionsMessage=
                    {() => "Không có điểm đo"}
                  />
                </td>
              </tr>
              <tr hidden={type == 21 ? false : true} >
                <th width="200px">
                  <i className="fas fa-solid fa-map-location mr-1" style={{ color: "#333" }}></i>KHU VỰC</th>
                <td colSpan={3} className="layer-select" >
                  <Select
                    isMulti
                    value={selectedCompare}
                    onChange={(e) => setSelectedCompare(e)}
                    name="area"
                    options={optionCompare}
                    // only allow user to choose up to 3 options
                    isOptionDisabled={() => selectedCompare.length >= 3}
                    className="basic-multi-select"
                    classNamePrefix="select"
                    placeholder="Chọn khu vực tối đa 3 khu vực"
                    noOptionsMessage={() => "Không có khu vực"}
                  />
                </td>
              </tr>
              <tr hidden={type == 22 ? false : true} >
                <th width="200px">
                  <i className="fas fa-solid fa-map-location mr-1" style={{ color: "#333" }}></i>PHỤ TẢI</th>
                <td colSpan={3} className="layer-select" >
                  <Select
                    isMulti
                    value={selectedCompare}
                    onChange={(e) => setSelectedCompare(e)}
                    name="area"
                    options={optionCompare}
                    // only allow user to choose up to 3 options
                    isOptionDisabled={() => selectedCompare.length >= 3}
                    className="basic-multi-select"
                    classNamePrefix="select"
                    placeholder="Chọn phụ tải tối đa 3 loại phụ tải"
                    noOptionsMessage={() => "Không có phụ tải"}
                  />
                </td>
              </tr>
              <tr hidden={type == 23 ? false : true} >
                <th width="200px">
                  <i className="fas fa-solid fa-map-location mr-1" style={{ color: "#333" }}></i>THIẾT BỊ</th>
                <td colSpan={3} className="layer-select" >
                  <Select
                    isMulti
                    value={selectedCompare}
                    onChange={(e) => setSelectedCompare(e)}
                    name="area"
                    options={optionCompare}
                    // only allow user to choose up to 3 options
                    isOptionDisabled={() => selectedCompare.length >= 3}
                    className="basic-multi-select"
                    classNamePrefix="select"
                    placeholder="Chọn loại thiết bị tối đa 3 loại thiết bị"
                    noOptionsMessage={() => "Không có loại thiết bị"}
                  />
                </td>
              </tr>
              <tr hidden={type == 24 ? false : true} >
                <th width="200px">
                  <i className="fas fa-solid fa-map-location mr-1" style={{ color: "#333" }}></i>ĐIỂM ĐO</th>
                <td colSpan={3} className="layer-select" >
                  <Select
                    isMulti
                    value={selectedCompare}
                    onChange={(e) => setSelectedCompare(e)}
                    name="area"
                    options={optionCompare}
                    // only allow user to choose up to 3 options
                    isOptionDisabled={() => selectedCompare.length >= 3}
                    className="basic-multi-select"
                    classNamePrefix="select"
                    placeholder="Chọn điểm đo tối đa 3 điểm đo"
                    noOptionsMessage={() => "Không có điểm đo"}
                  />
                </td>
              </tr>
              <tr className="report-shift" hidden={type == 4 || type == 5 || type == 6 || type == 7 ? false : true}>
                <th width="200px" >
                  <i className="fas fa-solid fa-hourglass-half mr-1" style={{ color: "#333" }}></i>
                  CA LÀM VIỆC</th>
                <td>
                  <div className="text-center text-header-ses">
                    <label className="text-bold">CA 1:</label>
                  </div>
                  <PatternFormat id="ca1Report" format="##:## - ##:##" mask="_" placeholder="__:__ - __:__" value={shift1} readOnly />
                </td>
                <td>
                  <div className="text-center text-header-ses">
                    <label className="text-bold">CA 2:</label>
                  </div>
                  <PatternFormat id="ca2Report" format="##:## - ##:##" mask="_" placeholder="__:__ - __:__" value={shift2} readOnly />
                </td>
                <td>
                  <div className="text-center text-header-ses">
                    <label className="text-bold">CA 3:</label>
                  </div>
                  <PatternFormat id="ca2Report" format="##:## - ##:##" mask="_" placeholder="__:__ - __:__" value={shift3} readOnly />
                </td>
              </tr>
               <tr hidden={type == 12 ? false : true}>
                <th width="200px">
                  <i className="fas fa-solid fa-signal mr-1" style={{ color: "#333" }}></i>
                  ĐƠN VỊ
                </th>
                <td colSpan={3}>
                  <select id="unitReport" className="custom-select block" style={{ fontSize: "13px" }} disabled={type == 12 ? false : true}>

                  </select>
                </td>
              </tr>
              <tr hidden={type == 12 || type == 18 || type == 19 || type == 20 ? false : true}>
                <th width="200px">
                  <i className="fas fa-solid fa-file mr-1" style={{ color: "#333" }}></i>
                  CƯỜNG ĐỘ CƠ SỞ (EnB)
                </th>
                <td colSpan={3}>
                  <input id="strengthReport" type="number" className="form-control" style={{ fontSize: "13px" }} maxLength={255} />
                </td>
              </tr>
              <tr hidden={type < 25 ? false : true}>
                <th width="200px">
                  <i className="fas fa-solid fa-calendar-days mr-1" style={{ color: "#333" }}></i>
                  THỜI GIAN
                </th>
                <td>
                  <select id="typeTimeReport" className="custom-select block" style={{ fontSize: "13px" }}
                    onChange={e => {
                      funcSetTypeTime(e);
                      formik.handleChange(e)
                    }}>
                    <option value={1}>Ngày</option>
                    <option value={2}>Tháng</option>
                    <option value={3}>Năm</option>
                  </select>
                </td>
                <td>
                  <label>Từ:</label>
                  <Calendar locale="vn"
                    value={fromDate}
                    onChange={(e) => setFromDate(e.value)}
                    view={typeTime == 2 ? "month" : typeTime == 3 ? "year" : "date"}
                    dateFormat={typeTime == 2 ? "yy-mm" : typeTime == 3 ? "yy" : "yy-mm-dd"}
                    maxDate={toDate}
                    minDate={checkDate} />
                </td>
                <td>
                  Đến:
                  <Calendar locale="vn"
                    value={toDate}
                    onChange={(e) => {
                      setToDate(e.value)
                      funcCheck(e)
                    }}
                    view={typeTime == 2 ? "month" : typeTime == 3 ? "year" : "date"}
                    dateFormat={typeTime == 2 ? "yy-mm" : typeTime == 3 ? "yy" : "yy-mm-dd"}
                    maxDate={new Date()} />
                </td>
              </tr>
              <tr hidden={type >= 25 ? false : true}>
                <th width="200px">
                  <i className="fas fa-solid fa-calendar-days mr-1" style={{ color: "#333" }}></i>
                  THỜI GIAN
                </th>
                <td>
                  <select id="typeTimeReport" className="custom-select block" style={{ fontSize: "13px" }}
                    onChange={e => {
                      funcSetTypeTime(e);
                      formik.handleChange(e)
                    }}>
                    <option value={1}>Ngày</option>
                  </select>
                </td>
                <td>
                  <label>Từ:</label>
                  <Calendar locale="vn"
                    value={fromDate}
                    onChange={(e) => setFromDate(e.value)}
                    view="date"
                    dateFormat="yy-mm-dd"
                    maxDate={toDate}
                    minDate={checkDate} />
                </td>
                <td>
                  Đến:
                  <Calendar locale="vn"
                    value={toDate}
                    onChange={(e) => {
                      setToDate(e.value)
                      funcCheck(e)
                    }}
                    view="date"
                    dateFormat="yy-mm-dd"
                    maxDate={new Date()} />
                </td>
              </tr>
            </tbody>
          </table>

          <div id="main-button">
            <button id="submitReport" type="submit" className="btn btn-outline-secondary btn-agree" style={{ width: "200px" }} onClick={() => funcDownload()}>
              <i className="fa-solid fa-check"></i> BÁO CÁO
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}


export default Report;
