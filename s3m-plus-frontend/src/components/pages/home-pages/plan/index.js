import React, { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { useFormik } from 'formik';
import { useParams } from "react-router-dom";
import moment from "moment";
import { Calendar } from "primereact/calendar";
import "./index.css";

import PlanService from "../../../../services/PlanService";
import ReactModal from "react-modal";
import { useTranslation } from "react-i18next";
import ProjectService from "../../../../services/ProjectService";
import UserService from "../../../../services/UserService";


const Plan = () => {
  const $ = window.$;
  const param = useParams();
  const [valueTime, setValueTime] = useState(0);
  const [isActiveButton, setIsActiveButton] = useState(true);


  const [projects, setProjects] = useState([]);
  const [projectsAdd, setProjectsAdd] = useState([]);
  const [error, setError] = useState(null);
  const { t } = useTranslation();
  const [startDate, setStartDate] = useState(moment(new Date()).format("YYYY-MM-DD") + " 00:00:00");
  const [endDate, setEndDate] = useState(moment(new Date()).format("YYYY-MM-DD") + " 23:59:59");
  const [display, setDisplay] = useState(false);

  const [query, setQuery] = useState("");
  // active modal state

  // current page state
  const [data, setData] = useState([]);
  const [order, setOrder] = useState();
  const [typeOrder, setTypeOrder] = useState();
  const [errorsValidate, setErrorsValidate] = useState([]);


  const getDataByDate = () => {
    if (startDate > endDate) {
      setDisplay(true);
    } else {
      setDisplay(false);
      let fromTime = moment(startDate).format("YYYY-MM-DD") + " 00:00:00";
      let toTime = moment(endDate).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
      funcGetPlan(
        fromTime,
        toTime,
        systemTypeId
      );
    }
  };


  const handleChangeView = (isActive) => {
    setIsActiveButton(!isActive);
    setValueTime(() => 1);
    let fromTime = moment(new Date()).format("YYYY-MM-DD") + " 00:00:00";
    let toTime = moment(new Date()).format("YYYY-MM-DD") + " 23:59:59";
    setStartDate(fromTime);
    setEndDate(toTime);
    funcGetPlan(fromTime, toTime, systemTypeId)
  };

  const onChangeValue = async (e) => {
    let time = e.target.value;
    setValueTime(() => e.target.value);
    const today = new Date();
    let fromTime = "";
    let toTime = "";
    if (time == 2) {
      today.setDate(today.getDate() - 1);
      fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else if (time == 3) {
      fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else if (time == 4) {
      today.setMonth(today.getMonth() - 1);
      fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
      /**Xét ngày cuối tháng trước */
      today.setMonth(today.getMonth() + 1);
      let temp = new Date(
        today.getFullYear() + "-" + today.getMonth() + "-" + "01"
      );
      today.setDate(temp.getDate() - 1);
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else if (time == 5) {
      today.setMonth(today.getMonth() - 3);
      fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
      /**Xét ngày cuối 3 tháng trước */
      today.setMonth(today.getMonth() + 3);
      let temp = new Date(
        today.getFullYear() + "-" + today.getMonth() + "-" + "01"
      );
      today.setDate(temp.getDate() - 1);
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else if (time == 6) {
      today.setMonth(today.getMonth() - 6);
      fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
      /**Xét ngày cuối 6 tháng trước */
      today.setMonth(today.getMonth() + 6);
      let temp = new Date(
        today.getFullYear() + "-" + today.getMonth() + "-" + "01"
      );
      today.setDate(temp.getDate() - 1);
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else if (time == 7) {
      fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else if (time == 8) {
      today.setYear(today.getFullYear() - 1);
      fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
      /**Xét ngày cuối năm ngoái */
      toTime = moment(today).format("YYYY") + "-12-31" + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    } else {
      fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
      toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
      setStartDate(fromTime);
      setEndDate(toTime);
    }
    funcGetPlan(
      fromTime,
      toTime,
      systemTypeId
    );
  };

  //Sort
  const funcSort = (col) => {
    setTypeOrder(col);
    if (order === "ASC") {
      const sorted = [...data].sort((a, b) => {
        return a[col] > b[col] ? 1 : -1

      });
      setData(sorted);
      setOrder("DSC");
    }
    else {
      const sorted = [...data].sort((a, b) => {
        return a[col] < b[col] ? 1 : -1
      });
      setData(sorted);
      setOrder("ASC");
    }
  }

  //SORT TIME
  const funcSortTime = (col) => {
    setTypeOrder(col);
    if (order === "ASC") {
      const sorted = [...data].sort((a, b) => {
        let date1 = new Date(a[col]);
        let date2 = new Date(b[col]);
        return date1 > date2 ? 1 : -1;
      });
      setData(sorted);
      setOrder("DSC");
    }
    else {
      const sorted = [...data].sort((a, b) => {
        let date1 = new Date(a[col]);
        let date2 = new Date(b[col]);
        return date1 < date2 ? 1 : -1;
      });
      setData(sorted);
      setOrder("ASC");
    }
  }
  const selectedProjectId = param.projectId;
  const selectedProject = projects.find(project => project.projectId === selectedProjectId);
  const projectName = selectedProject ? selectedProject.projectName : "Project Not Found";
  //LIST DATA

  const funcGetPlan = async (fromTime, toTime, systemTypeId) => {
    let acustomerId = param.customerId;
    let aprojectId = param.projectId;
    let res = await PlanService.getPlan(acustomerId, aprojectId, fromTime, toTime, systemTypeId);
    if (res.status === 200) {
      setData(() => res.data);
    }
  }

  const funcAllGetPlan = async (systemTypeId) => {
    let fromTime = moment(new Date()).format("YYYY-MM-DD") + " 00:00:00";
    let toTime = moment(new Date()).format("YYYY-MM-DD") + " 23:59:59";
    let acustomerId = param.customerId;
    let aprojectId = param.projectId;
    let res = await PlanService.getPlan(acustomerId, aprojectId, fromTime, toTime, systemTypeId);
    if (res.status === 200) {
      setData(() => res.data);
    }
  }

  // MODEL DETAIL
  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  //HANDLE CLICK ADD OPEN MODAL
  const [isModalOpen1, setIsModalOpen1] = useState(false);
  const openModal1 = () => {
    setIsModalOpen1(true);
  };

  const closeModal1 = () => {
    setIsModalOpen1(false);
  };
  const handleClickAdd = () => {
    openModal1(true);
  };

  // Search-filter
  function removeDiacritics(text) {
    return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  }

  const filteredData = data.filter((item) =>
    (item.projectName !== null && removeDiacritics(item.projectName.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))) ||
    (item.systemTypeName !== null && removeDiacritics(item.systemTypeName.toString().toLowerCase()).includes(removeDiacritics(query.toLowerCase()))),
  );

  //Debounce cho Search Input
  let timeout;
  const handleSearchChange = (e) => {
    const value = e.target.value;

    if (timeout) {
      clearTimeout(timeout); // Xóa bất kỳ timeout nào còn đang chạy
    }

    timeout = setTimeout(() => {
      setQuery(value);
    }, 400); // Sau 1000ms (1 giây), gọi hàm setQuery
  };

  //ADD
  const initialValues = {
    organizationCreate: "",
    organizationTest: "",
    organizationExecution: "",
    completionTime: "",
    customerId: "",
    projectId: param.projectId,
    content: "",
    startDate: "",
    endDate: "",
    status: "",
    selectedProject: "",
    resultExecution: "",
    systemTypeId: ""
  }
  function validateFormData(data) {
    const fields = ['content', 'endDate', 'projectId'];

    for (const field of fields) {
      if (!data[field]) {
        $.alert({
          title: t('content.home_page.plan.notification'),
          content: t('content.home_page.plan.check_null') + ` ${field}`,
        });
        return false; // Thoát khỏi hàm nếu trường không hợp lệ
      }

      if (typeof data[field] === 'string' && data[field].trim() === "") {
        $.alert({
          title: 'Thông báo!',
          content: t('content.home_page.plan.check_null') + ` ${field}`,
        });
        return false; // Thoát khỏi hàm nếu trường là chuỗi trống
      }
    }

    return true;
  }
  const formik = useFormik({
    initialValues,
    enableReinitialize: true,
    onSubmit: async (data) => {
      let sys = systemTypeId
      let cusId = param.customerId

      if (validateFormData(data)) {
        let res = await PlanService.addPlan(cusId, { ...data, systemTypeId: sys });
        if (res.status === 200) {
          $.alert({
            title: t('content.home_page.plan.notification'),
            content: t('content.home_page.plan.add_success')
          });
          funcAllGetPlan(sys)
          closeModal1()
        } else {
          setError(t('validate.area.INSERT_FAILED'));
        }
      }
    }

  })

  //UPDATE
  const [dataPlanUpdate, setDataPlanUpdate] = useState([]);


  const handleInputChange = (e) => {
    const { name, value } = e.target;

    setDataPlanUpdate((prevData) => ({
      ...prevData,
      [name]: value,
      [e.target.name]: e.target.value
    }));
  };

  const handleRadioChange = (e) => {
    const { name, value } = e.target;
    setDataPlanUpdate((prevData) => ({
      ...prevData,
      [name]: parseInt(value),
    }));
  };

  const handleUpdatePlan = async () => {
    let response = await PlanService.updatePlan(param.customerId, dataPlanUpdate);
    if (response.status === 200) {
      $.alert({
        title: t('content.home_page.plan.notification'),
        content: t('content.home_page.plan.update_success')
      });

      closeModal()
      funcGetPlan(startDate, endDate, systemTypeId)
    } else {
    }
  }
  //GETONE
  const handleClickPlan = async (planId) => {
    let customerId = param.customerId;
    let res = await PlanService.getPlanById(customerId, planId)
    if (res.status === 200) {
      setDataPlanUpdate(() => res.data)
      setIsModalOpen(true);
    };
  }
  //DELETE
  const handleDeletePlanById = (planId) => {
    $.confirm({
      type: 'red',
      typeAnimated: true,
      icon: 'fa fa-warning',
      title: t('content.home_page.plan.confirm'),
      content: t('content.home_page.plan.delete_confirm') + " ?",
      buttons: {
        confirm: {
          text: t('content.home_page.plan.accept'),
          action: async () => {
            let customerId = param.customerId;
            let res = await PlanService.deletePlanById(customerId, planId);
            if (res.status === 200) {
              $.alert({
                title: t('content.home_page.plan.notification'),
                content: t('content.home_page.plan.delete_success')
              });

            } else {
              $.alert({
                type: 'red',
                title: t('content.home_page.plan.notification'),
                content: t('content.home_page.plan.delete_fail')
              });
            }
            funcGetPlan(startDate, endDate, systemTypeId)
          }
        },
        cancel: {
          text: t('content.home_page.plan.cancel')
        }
      }
    })

  };

  const [systemTypeId, setSystemTypeId] = useState(1);
  const funcSetSystemType = (e) => {
    setSystemTypeId(() => e.target.value)
    funcGetPlan(startDate, endDate, e.target.value)
  }

  const [projectIdAdd, setProjectIdAdd] = useState();
  const [projectId, setProjectId] = useState();
  const [customerId, setCustomerId] = useState(0);
  const listProject = async () => {
    let customerId = param.customerId;
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      $(".input-project-m").show();
      let res = await ProjectService.getProjectByCustomerId(customerId);
      $('#siteManufacture').html("");
      setProjectsAdd(res.data)
      if (res.data.length > 0) {
        $('#siteManufacture').prop('disable', false);
        let data = res.data;
        let projectId = res.data[0].projectId;
        setProjectIdAdd(projectId)
        $.each(data, function (index, value) {
          $('#siteManufacture').append('<option value="' + value.projectId + '">' + value.projectName + '</option>')
        });

      } else {
        $('#siteManufacture').prop('disable', true);
      }
    } else {
      $(".input-project-m").hide();
    }
  }


  const getProject = async (id) => {
    if (id != undefined) {
      let res = await ProjectService.getProject(id);
      setProjects([res.data]);
    }
  }

  const checkPriority = async () => {
    let response = await UserService.getUserByUsername();
    if (response.status === 200) {
      const userData = response.data;
      setSystemTypeId(userData.prioritySystem);
    }
  }

  useEffect(() => {
    checkPriority()
    listProject()
    funcGetPlan(startDate, endDate, systemTypeId)
    // funcAllGetPlan(systemTypeId)
    getProject(param.projectId)
    setProjectId(param.projectId)

    // funcGetListProject();
  }, [param.customerId, param.projectId]);


  return (
    <>
      <>
        <div className="div-right-plan">
          <div className="system-type">
            <div className="radio-tabs">
              <label className="radio-tabs__field">
                <input type="radio" name="radio-tabs" value={1} className="radio-tabs__input" checked={systemTypeId == 1 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                <span className="radio-tabs__text text-uppercase">
                  <img src="/resources/image/icon-load-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                  {t('content.home_page.load')}</span>
              </label>
              <label className="radio-tabs__field">
                <input type="radio" name="radio-tabs" value={2} className="radio-tabs__input" checked={systemTypeId == 2 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                <span className="radio-tabs__text text-uppercase">
                  <img src="/resources/image/icon-solar-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                  {t('content.home_page.solar')}</span>
              </label>
              <label className="radio-tabs__field">
                <input type="radio" name="radio-tabs" value={5} className="radio-tabs__input" checked={systemTypeId == 5 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                <span className="radio-tabs__text text-uppercase">
                  <img src="/resources/image/icon-grid-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                  {t('content.home_page.grid')}</span>
              </label>
              <label className="radio-tabs__field">
                <input type="radio" name="radio-tabs" value={3} className="radio-tabs__input" checked={systemTypeId == 3 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                <span className="radio-tabs__text text-uppercase" >
                  <img src="/resources/image/icon-battery-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                  {t('content.home_page.battery')}</span>
              </label>
              <label className="radio-tabs__field" >
                <input type="radio" name="radio-tabs" value={4} className="radio-tabs__input" checked={systemTypeId == 4 ? true : false} onChange={(e) => funcSetSystemType(e)} />
                <span className="radio-tabs__text  text-uppercase">
                  <img src="/resources/image/icon-wind-circle.svg" style={{ height: "23px", marginRight: "10px" }}></img>
                  {t('content.home_page.wind')}</span>
              </label>
            </div>
          </div>

          <hr
            style={{
              background: "var(--ses-blue-80-color)",
              color: "var(--ses-blue-80-color)",
              borderColor: "var(--ses-blue-80-color)",
              height: "2px",
              borderRadius: "5px",
              margin: "0em",
            }}
          />
          {/* Date */}
          <div>
            <div className="input-group mb-3" style={{ padding: 10 }}>
              <div className="" style={{ width: "90%", zIndex: 0 }}>
                <div className="input-group-prepend float-left">
                  <button className="btn btn-outline-secondary" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#0a1a5c" : "#e9ecef" }} onClick={() => handleChangeView(isActiveButton)}>
                    <img src="/resources/image/icon-calendar.svg" style={{ height: "18px" }}></img>
                  </button>
                  <button className="btn btn-outline-secondary btn-time" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#e9ecef" : "#0a1a5c" }} onClick={() => handleChangeView(isActiveButton)}>
                    <img src="/resources/image/icon-play.svg" style={{ height: "18px" }}></img>
                  </button>
                </div>

                {!isActiveButton && (
                  <div className="input-group float-left mr-1 select-calendar" style={{ width: "100px", marginLeft: 10, height: 34 }}>
                    <select className="form-control select-value"
                      //onChange={(e) => handleChangeChartType(e.target.value)}
                      style={{ backgroundColor: "#0a1a5c", borderRadius: 5, border: "1px solid #FFA87D", color: "white" }}
                      title="Chi tiết"
                      onChange={onChangeValue}
                    >
                      <option className="value" key={1} value={1}>{t('content.home_page.today')}</option>
                      <option className="value" key={2} value={2}>{t('content.home_page.yesterday')}</option>
                      <option className="value" key={3} value={3}>{t('content.home_page.this_month')}</option>
                      <option className="value" key={4} value={4}>{t('content.home_page.last_month')}</option>
                      <option className="value" key={5} value={5}>{t('content.home_page.3_months_ago')}</option>
                      <option className="value" key={6} value={6}>{t('content.home_page.6_months_ago')}</option>
                      <option className="value" key={7} value={7}>{t('content.home_page.this_year')}</option>
                      <option className="value" key={8} value={8}>{t('content.home_page.last_year')}</option>
                    </select>
                  </div>
                )}
                {isActiveButton && (

                  <div className="input-group float-left mr-1 select-time" title="Chi tiết" style={{ width: "300px", marginLeft: 10, height: 34 }}>
                    <button className="form-control button-calendar" readOnly data-toggle="modal" data-target={"#modal-calendar"} style={{ backgroundColor: "#ffffff", border: "1px solid #0A1A5C" }}>
                      {moment(startDate).format("YYYY-MM-DD") + " - " + moment(endDate).format("YYYY-MM-DD")}
                    </button>
                    <div className="input-group-append" style={{ zIndex: 0 }}>
                      <button className="btn button-infor" type="button" data-toggle="modal" data-target={"#modal-calendar"} style={{ fontWeight: "bold", height: 34 }}>......</button>
                    </div>
                  </div>
                )}
                {/* <div>
                    <button className="btn btn-warning" onClick={openModalPopup}>Pop Up</button>
                  </div> */}
              </div>

              <div
                className="modal fade"
                id="modal-calendar"
                tabIndex="-1"
                role="dialog"
                aria-labelledby="exampleModalLabel"
                aria-hidden="true"
              >
                <div className="modal-dialog" role="document">
                  <div className="modal-content">
                    <div
                      className="modal-header"
                      style={{
                        backgroundColor: "#0a1a5c",
                        height: "44px",
                        color: "white",
                      }}
                    >
                      <h5 style={{ color: "white" }}>CALENDAR</h5>
                      <button
                        style={{ color: "#fff" }}
                        type="button"
                        className="close"
                        data-dismiss="modal"
                        aria-label="Close"
                      >
                        <span aria-hidden="true">&times;</span>
                      </button>
                    </div>
                    <div className="modal-body">
                      <div
                        className="input-group float-left mr-1"
                        style={{ width: "270px" }}
                      >
                        <h5>Từ ngày</h5>
                        <Calendar
                          id="from-value"
                          className="celendar-picker"
                          dateFormat="yy-mm-dd"
                          maxDate={new Date()}
                          value={startDate}
                          onChange={(e) => setStartDate(e.value)}
                        />
                        <div className="input-group-prepend background-ses">
                          <span className="input-group-text pickericon">
                            <span className="far fa-calendar"></span>
                          </span>
                        </div>
                      </div>
                      <div
                        className="input-group float-left mr-1"
                        style={{ width: "270px" }}
                      >
                        <h5>Đến ngày</h5>
                        <Calendar
                          id="to-value"
                          className="celendar-picker"
                          dateFormat="yy-mm-dd"
                          maxDate={new Date()}
                          value={endDate}
                          onChange={(e) => setEndDate(e.value)}
                        />
                        <div className="input-group-prepend background-ses">
                          <span className="input-group-text pickericon">
                            <span className="far fa-calendar"></span>
                          </span>
                        </div>
                      </div>
                      <div className="input-group float-left mr-1"></div>
                    </div>
                    <div className="modal-footer">
                      <button
                        type="button"
                        className="btn btn-secondary"
                        data-dismiss="modal"
                      >
                        Đóng
                      </button>
                      <button
                        type="button"
                        className="btn btn-primary"
                        onClick={() => getDataByDate()}
                        style={{
                          backgroundColor: "#0a1a5c",
                          borderColor: "#fff",
                        }}
                      >
                        Lấy dữ liệu
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div className="" style={{ width: "10%" }}>
                {/* ICON CIRCLE-PLUS */}

                <div className="position-relative mb-5" data-toggle="modal" onClick={handleClickAdd} >
                  <i className="fas fa-solid fa-circle-plus fa-2x float-right add-user" style={{ fontSize: "2rem", marginRight: "10px" }}></i>
                </div>
              </div>
              {/* Search-bar */}
              <div className="position-relative mt-1" style={{ width: "300px", height: "" }}><input type="text" style={{ width: "390px", height: "" }} className="" name="equipment-name" placeholder={t('content.home_page.search') + ' .......'}
                onChange={handleSearchChange} />
                {/* onChange={(e) => setQuery(e.target.value)} /> */}
                <i className="fa fa-search position-absolute" style={{ left: "360px", top: "10px", }}></i>
              </div>


              <div className="mt-2" style={{ width: "100%", height: "5px", backgroundColor: "#fff" }} ></div>
              <div className="table-warning mt-2">

                <div
                  className="input-group float-left mr-1 select-time "
                  title="Chi tiết"
                >
                  <div className="input-group-append" style={{ zIndex: 1 }}>

                    {/* MODAL ADD */}

                    <ReactModal
                      isOpen={isModalOpen1}
                      onRequestClose={closeModal1}
                      contentLabel="Modal 1"
                      style={{
                        content: {
                          width: "50%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                          height: "80%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                          margin: "auto", // Căn giữa modal
                          marginTop: "10px",
                        },
                      }}
                    >
                      {/* {
                        (errorsValidate.length > 0) &&
                        <div className="alert alert-warning" role="alert">
                          {errorsValidate.map((error, index) => {
                            return (<p key={index} className="m-0 p-0">{error}</p>)
                          })
                          }
                        </div>
                      }

                      {
                        (error != null) &&
                        <div className="alert alert-danger">
                          <p className="m-0 p-0">{error}</p>
                        </div>
                      }

                      {
                        ((formik.errors.projectId && formik.touched.projectId) ||
                          (formik.errors.content && formik.touched.content) ||
                          (formik.errors.startDate && formik.touched.startDate) ||
                          (formik.errors.status && formik.touched.status) ||
                          (formik.errors.endDate && formik.touched.endDate))
                        &&
                        <div className='alert alert-warning' role="alert">
                          <p className='m-0'>{formik.errors.projectId}</p>
                          <p className='m-0'>{formik.errors.content}</p>
                          <p className='m-0'>{formik.errors.startDate}</p>
                          <p className='m-0'>{formik.errors.endDate}</p>
                          <p className='m-0'>{formik.errors.status}</p>

                        </div>
                      } */}
                      <form onSubmit={formik.handleSubmit}>
                        <div>
                          <h2 className="text-uppercase"
                            style={{
                              textAlign: "center", backgroundColor: "#0A1A5C", color: "#fff", width: "100%", padding: "5px"
                            }}
                          >
                            {t('content.home_page.plan.work_plan')}
                          </h2>

                          <div className="row">
                            <div className="col-md-6">
                              <label className="form-label">{t('content.home_page.plan.start_date') + ':'}</label>
                              <div className="input-group">
                                <input
                                  type="datetime-local"
                                  name="startDate"
                                  placeholder={t('content.home_page.plan.start_date')}
                                  defaultValue={moment(new Date()).format("YYYY-MM-DDTHH:mm")}
                                  className="form-control"
                                  onChange={formik.handleChange}
                                />
                              </div>
                            </div>
                            <div className="col-md-6">
                              <label className="form-label">{t('content.home_page.plan.end_date') + ':'}</label>
                              <div className="input-group">
                                <input
                                  type="datetime-local"
                                  name="endDate"
                                  placeholder={t('content.home_page.plan.end_date') + ':'}
                                  className="form-control"
                                  onChange={formik.handleChange}
                                />
                              </div>
                            </div>
                          </div>
                          <div style={{ backgroundColor: "white", height: "20px" }}></div>
                          <input
                            type="text"
                            name="so"
                            placeholder={t('content.home_page.plan.id') + ':'}
                            style={{ width: "100%" }}
                            onChange={formik.handleChange}
                            disabled
                          />
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <input
                            type="text"
                            name="organizationCreate"
                            className="form-control"
                            placeholder={t('content.home_page.plan.organization_create') + ':'}
                            style={{ width: "100%" }}
                            onChange={formik.handleChange}
                          />
                          <div style={{ backgroundColor: "white", height: "20px" }}></div>

                          <textarea
                            className="form-control" name="content" rows="7" placeholder={t('content.home_page.plan.content') + ':'} onChange={formik.handleChange}
                          ></textarea>

                          <div style={{ backgroundColor: "white", height: "20px" }}></div>

                          {/* ADD PROJECT */}
                          {/* <select className="form-select" style={{ width: "100%" }} name="projectId" onChange={formik.handleChange} required>

                            <option value="">Chọn một dự án</option>
                            {projects.map((item, index) => (
                              <option key={index} value={item.projectId}>
                                {item.projectName}
                              </option>
                            ))}
                          </select> */}
                          {param.projectId == null ? <select className="form-select" style={{ width: "100%" }} name="projectId" onChange={formik.handleChange} required>

                            <option value="">{t('content.home_page.plan.choose_project')}</option>
                            {projectsAdd.map((item, index) => (
                              <option key={index} value={item.projectId}>
                                {item.projectName}
                              </option>
                            ))}
                          </select> : <select className="custom-select block" onChange={formik.handleChange} style={{ fontSize: "13px" }} name="projectId" disabled={!formik.values.projectId || formik.values.projectId === "undefined"} required>
                            {projects.map((pro, index) => {
                              return (
                                <option selected key={index} value={pro.projectId}>{pro.projectName}</option>
                              )
                            })}
                          </select>}
                          {/* <select className="custom-select block" onChange={formik.handleChange} style={{ fontSize: "13px" }} name="projectId" disabled={!formik.values.projectId || formik.values.projectId === "undefined"} required>
                            {projects.map((pro, index) => {
                              return (
                                <option selected key={index} value={pro.projectId}>{pro.projectName}</option>
                              )
                            })}
                          </select> */}

                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <input
                            type="text"
                            name="organizationExecution"
                            placeholder={t('content.home_page.plan.organization_execution') + ':'}
                            style={{ width: "100%" }}
                            onChange={formik.handleChange}
                          />
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>

                          <div style={{ backgroundColor: "white", height: "10px" }}></div>

                          <input
                            type="text"
                            name="resultExecution"
                            placeholder={t('content.home_page.plan.result_execution') + ':'}
                            style={{ width: "100%" }}
                            onChange={formik.handleChange}
                          />
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <input
                            type="text"
                            name="organizationTest"
                            placeholder={t('content.home_page.plan.organization_test') + ':'}
                            style={{ width: "100%" }}
                            onChange={formik.handleChange}
                          />

                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <div className="form-check form-check-inline">

                            <label className="form-check-label" htmlFor="inlineRadio3">{t('content.status') + ':'}</label>
                          </div>
                          <div className="form-check form-check-inline">
                            <input className="form-check-input" type="radio" name="status" id="inlineRadio1" value="0" onChange={formik.handleChange} defaultChecked />
                            <label className="form-check-label" htmlFor="inlineRadio1">{t('content.home_page.plan.pending') + ':'}</label>
                          </div>
                          {/* <div className="form-check form-check-inline">
                            <input className="form-check-input" type="radio" name="status" id="inlineRadio2" value="1" onChange={formik.handleChange} />
                            <label className="form-check-label" htmlFor="inlineRadio2">Đã Xử lí</label>
                          </div> */}

                          <div style={{ backgroundColor: "white", height: "10px" }}></div>
                          <div style={{ backgroundColor: "white", height: "10px" }}></div>

                          <div className="row">
                            <div className="col-4 mr-3"></div>
                            <div className="col-0">
                              <button
                                type="submit"
                                style={{
                                  backgroundColor: "#0A1A5C",
                                  color: "#fff",
                                  width: "70px",
                                  height: "40px",
                                  borderRadius: "5px"
                                }}

                              >
                                {t('content.home_page.plan.save')}
                              </button>
                            </div>
                            <div className="col-1"></div>

                            <div className="col-0">
                              <button
                                style={{
                                  backgroundColor: "#9DA3BE",
                                  color: "#fff",
                                  width: "70px",
                                  height: "40px",
                                  borderRadius: "5px"
                                }}
                                onClick={closeModal1}
                              >
                                {t('content.home_page.plan.close')}
                              </button>
                            </div>
                            <div className="col-6"></div>
                          </div>
                        </div>
                      </form>
                      {/* Các ô input */}
                    </ReactModal>
                  </div>
                </div>


                {/* TABLE DATA PLAN */}
                <div className="table-container">
                  <table className="table" id="table">

                    <thead>
                      <tr height="40px">
                        <th style={{ width: "3%" }}>{t('content.no')}</th>

                        <th style={{ width: "25%" }} className="text-uppercase">
                          {t('content.project')}
                          <i
                            className={
                              typeOrder == "projectId"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }} onClick={() => funcSort("projectId")}

                          ></i>
                        </th>
                        <th style={{ width: "12%" }} className="text-uppercase">
                          {t('content.home_page.plan.system_type')}
                          <i
                            className={
                              typeOrder == "deviceName"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }}

                          ></i>
                        </th>

                        <th style={{ width: "12%" }} className="text-uppercase">
                          {t('content.home_page.plan.id')}
                          <i
                            className={
                              typeOrder == "planId"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }} onClick={() => funcSort("planId")}

                          ></i>
                        </th>

                        <th style={{ width: "15%" }} className="text-uppercase">
                          {t('content.home_page.plan.from_date')}
                          <i
                            className={
                              typeOrder == "startDate"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }} onClick={() => funcSortTime("startDate")}

                          ></i>
                        </th>
                        <th style={{ width: "15%" }} className="text-uppercase">
                          {t('content.home_page.plan.to_date')}
                          <i
                            className={
                              typeOrder == "updateDate"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }} onClick={() => funcSortTime("updateDate")}

                          ></i>
                        </th>
                        <th style={{ width: "10%" }} className="text-uppercase">
                          {t('content.status')}
                          <i
                            className={
                              typeOrder == "status"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }} onClick={() => funcSort("status")}

                          ></i>
                        </th>
                        <th style={{ width: "10%" }}>
                          <div style={{ display: "inline-block", marginLeft: "30px", marginBottom: "5px" }}>
                            <i className="fa-regular fa-hand fa-lg"></i>
                          </div>
                          <i
                            className={
                              typeOrder == "total"
                                ? order == "ASC"
                                  ? "fas fa-solid fa-sort-down ml-2 fa-lg"
                                  : "fas fa-solid fa-sort-up ml-2 fa-lg"
                                : "fas fa-solid fa-sort ml-2 fa-lg"
                            }
                            style={{ color: "#FFF" }}
                          ></i>
                        </th>

                      </tr>
                    </thead>

                    <tbody style={{ lineHeight: 1 }}>

                      {filteredData?.map((item, index) => (
                        <tr
                          key={index}
                          height="30px"
                          onClick={() => handleClickPlan(item.planId)
                          }
                        >
                          <td className="text-center">{index + 1}</td>
                          <td className="text-left">{item.projectName}</td>
                          <td className="text-left">{item.systemTypeName}</td>
                          <td className="text-right" style={{ padding: "0 10px" }}>
                            {item.planId}
                          </td>
                          <td className="text-center">{item.createDate}</td>
                          {/* <td className="text-center">{item.updateDate}</td> */}
                          <td className="text-center">{item.updateDate}</td>

                          <td className="text-center">
                            {item.status === 0 ? (
                              <div className="levelStatus0">
                                <i className="fa-solid fa-clock fa-xl" style={{ color: "#f7aa02" }}></i>
                              </div>
                            ) : (
                              <div className="levelStatus1">
                                <i className="fa-solid fa-circle-check fa-xl" style={{ color: "#29d67d" }}></i>
                              </div>
                            )}
                          </td>
                          <td className="text-center">
                            <i type="button" // Đặt type là "button" để tránh xử lý mặc định của nút submit
                              onClick={(e) => {
                                e.stopPropagation(); // Ngăn sự kiện click từ việc lan rộng lên hàng tr trong table
                                handleDeletePlanById(item.planId);
                              }} className="fa-solid fa-circle-xmark fa-xl" style={{ color: "#ff0000" }}></i>

                          </td>
                        </tr>
                      )
                      )}
                      {data.length < 1 && (
                        <tr height="30px">
                          <td colSpan="9" className="text-center">
                            {t('content.no_data')}
                          </td>
                        </tr>
                      )}
                    </tbody>

                  </table>
                </div>


              </div>
            </div>
          </div>

          {/* MODAL DETAILS */}
          <ReactModal
            isOpen={isModalOpen}
            onRequestClose={() => {
              setIsModalOpen(false);
            }}
            style={{
              content: {
                width: "50%", // Kích thước chiều rộng của modal (có thể điều chỉnh)
                height: "90%", // Kích thước chiều cao của modal (có thể điều chỉnh)
                margin: "auto", // Căn giữa modal
                marginTop: "10px",
              },
            }}
          >

            <h2
              className="text-uppercase"
              style={{
                textAlign: "center",
                backgroundColor: "#0A1A5C",
                color: "#fff",
                width: "100%",
                padding: "5px", // Thay đổi kích thước màu nền bằng padding
              }}
            >
              {t('content.home_page.plan.work_plan')}
            </h2>
            <div style={{ backgroundColor: "white", height: "20px" }}></div>

            <table className="table" >
              <tbody>
                <tr>
                  <th scope="row" width="150px">{t('content.home_page.plan.start_date')}</th>
                  <td className="col-10">
                    <input
                      type="datetime-local"
                      className="form-control"
                      name="startDate"
                      value={dataPlanUpdate.startDate}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>

                <div style={{ backgroundColor: "white", height: "20px" }}></div>

                <tr>
                  <th scope="row">{t('content.home_page.plan.end_date')}</th>
                  <td>
                    <input
                      type="datetime-local"
                      className="form-control"
                      name="endDate"
                      value={dataPlanUpdate.endDate}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>

                <div style={{ backgroundColor: "white", height: "20px" }}></div>

                <tr>
                  <th scope="row">{t('content.home_page.plan.id')}</th>
                  <td>
                    <input
                      type="text"
                      className="form-control"
                      name="planId"
                      readOnly="true"
                      value={dataPlanUpdate.planId}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>
                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th scope="row">{t('content.home_page.plan.organization_create')}</th>
                  <td>
                    <input
                      type="text"
                      className="form-control"
                      name="organizationCreate"
                      value={dataPlanUpdate.organizationCreate}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>
                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th>{t('content.home_page.plan.content')}</th>
                  <td>
                    <textarea
                      className="form-control"
                      name="content"
                      rows="10"
                      value={dataPlanUpdate.content}
                      onChange={handleInputChange}
                    ></textarea>
                  </td>
                </tr>

                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th>{t('content.project')}</th>
                  <td>
                    <input
                      type="text"
                      className="form-control"
                      name="projectName"
                      value={dataPlanUpdate.projectName}
                      onChange={handleInputChange}
                      readOnly
                    />
                  </td>
                  {/* <select className="custom-select block" style={{ fontSize: "13px" }} readOnly>
                      <option value="">{dataPlanUpdate.projectName}</option>
                      {projects.map((item, index) => (
                        <option key={index} value={item.projectId}>
                          {item.projectName}
                        </option>
                      ))}
                    </select> */}

                </tr>
                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th scope="row">{t('content.home_page.plan.organization_execution')}</th>
                  <td>
                    <input
                      type="text"
                      className="form-control"
                      name="organizationExecution"
                      value={dataPlanUpdate.organizationExecution}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>

                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th scope="row">{t('content.home_page.plan.result_execution')}</th>
                  <td>
                    <input
                      type="text"
                      className="form-control"
                      name="resultExecution"
                      value={dataPlanUpdate.resultExecution}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>

                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th scope="row">{t('content.home_page.plan.organization_test')}</th>
                  <td>
                    <input
                      type="text"
                      className="form-control"
                      name="organizationTest"
                      value={dataPlanUpdate.organizationTest}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>
                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th scope="row">{t('content.home_page.plan.completion_time')}</th>
                  <td>
                    <input
                      readOnly="true"
                      type="datetime-local"
                      className="form-control"
                      name="completionTime"
                      value={dataPlanUpdate.completionTime}
                      onChange={handleInputChange}
                    />
                  </td>
                </tr>

                <div style={{ backgroundColor: "white", height: "20px" }}></div>
                <tr>
                  <th>{t('content.status')}</th>
                  <td style={{ paddingTop: "10px" }}>

                    <label>
                      <input
                        type="radio"
                        name="status"
                        value="0"
                        checked={dataPlanUpdate.status === 0 ? "checked" : ""}
                        onChange={handleRadioChange}
                      /> {t('content.home_page.plan.pending')}
                    </label>
                    <div style={{ backgroundColor: "white", height: "0px" }}></div>
                    <label>
                      <input
                        type="radio"
                        name="status"
                        value="1"
                        checked={dataPlanUpdate.status === 1 ? "checked" : ""}
                        onChange={handleRadioChange}
                      /> {t('content.home_page.plan.resolve')}
                    </label>

                  </td>
                </tr>
              </tbody>
            </table>

            <div className="row">
              <div className="col-4 mr-5"></div>
              <div className="col-0">
                <button
                  style={{
                    backgroundColor: "#0A1A5C",
                    color: "#fff",
                    width: "70px",
                    height: "40px",
                    borderRadius: "5px"
                  }}
                  onClick={handleUpdatePlan}
                >
                  {t('content.home_page.plan.save')}
                </button>
              </div>
              <div className="col-1"></div>
              <div className="col-0">
                <button
                  style={{
                    backgroundColor: "#9DA3BE",
                    color: "#fff",
                    width: "70px",
                    height: "40px",
                    borderRadius: "5px"
                  }}
                  onClick={closeModal}
                >
                  {t('content.home_page.plan.close')}
                </button>
              </div>
              <div className="col-5"></div>
            </div>
          </ReactModal>

        </div>
      </>

    </>
  );

};

export default Plan;
