import React, { useEffect, useState } from "react";
import CustomerService from "../../../../../services/CustomerService";
import ProjectService from "../../../../../services/ProjectService";
import $ from "jquery";
import SettingService from "../../../../../services/SettingService";
import CONS from "../../../../../constants/constant";
import moment from "moment";
import { Link, useHistory } from "react-router-dom";
import { useLocation } from "react-router-dom/cjs/react-router-dom.min";
import { t } from "i18next";

const ListSetting = ({ permissionActions, ids }) => {
  const [customers, setCustomers] = useState([]);
  const [customer, setCustomer] = useState({});
  const [settings, setSettings] = useState([]);
  const [customerId, setCustomerId] = useState(0);
  const [projectId, setProjectId] = useState(0);
  const [project, setProject] = useState({});
  const [projects, setProjects] = useState([]);
  const [status, setStatus] = useState(null);
  const location = useLocation();
  const history = useHistory();
  const getInfoAdd = async () => {
    if (ids.idCustomer !== undefined) {
      let res = await CustomerService.getCustomer(ids.idCustomer);
      setCustomer(res.data);
    }
    let res = await CustomerService.getListCustomer();
    setCustomers(res.data);
    listProject();
  };
  const listProject = async () => {
    if (ids.idProject !== undefined) {
      let res = await ProjectService.getProject(ids.idProject);
      setProject(res.data)
    }
    let customerId = $("#customerId").val();
    if (customerId != null && parseInt(customerId) > 0) {
      setCustomerId(customerId);
      $(".input-project-m").show();
      let res = await ProjectService.getProjectByCustomerId(customerId);
      setProjects(res.data);
      // $("#projectId").html("");
      // if (res.data.length > 0) {
      //   $("#projectId").prop("disable", false);
      //   let data = res.data;
      //   $.each(data, function (index, value) {
      //     $("#projectId").append(
      //       '<option value="' +
      //         value.projectId +
      //         '">' +
      //         value.projectName +
      //         "</option>"
      //     );
      //   });
      // } else {
      //   $("#projectId").prop("disable", true);
      // }
    } else {
      $(".input-project-m").hide();
    }
    listSetting();
  };

  const listSetting = async () => {
    let customerId = document.getElementById("customerId").value;
    setCustomerId(customerId);
    let projectId = document.getElementById("projectId").value;
    setProjectId(projectId);
    let typeSystem = document.getElementById("typeSystem").value;
    let res = await SettingService.listSetting(
      customerId,
      projectId,
      typeSystem
    );
    if (res.status === 200 && parseInt(res.data.length) > 0) {
      setSettings(res.data);
    } else {
      setSettings([]);
    }
  };

  const setNotification = (state) => {
    if (state?.status === 200 && state?.message === "INSERT_SUCCESS") {
      setStatus({
        code: 200,
        message: t('content.category.setting.list.add_success')
      });
    } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS") {
      setStatus({
        code: 200,
        message: t('content.category.setting.list.edit_success')
      });
    }
    setTimeout(() => {
      setStatus(null);
    }, 3000);
  };

  useEffect(() => {
    document.title = t('content.category.setting.list.header')
    if (location.state) {
      setNotification(location.state);
    }
    getInfoAdd();
  }, [customerId]);

  return (
    <div id="page-body">
      <div id="main-title">
        <h5 className="d-block mb-0 float-left text-uppercase">
          <i className="fas fa-gear"></i> &nbsp;{t('content.category.setting.list.header')}
        </h5>
      </div>

      <div id="main-search" className="ml-1" style={{ height: 32 }}>
        <div className="input-group float-left mr-1" style={{ width: 270 }}>
          <div className="input-group-prepend">
            <span className="input-group-text pickericon">
              <span className="fas fa-user-tie"></span>
            </span>
          </div>
          {/* <select
            name="customerId"
            id="customerId"
            defaultValue={customerId}
            className="custom-select block custom-select-sm"
            onChange={() => getInfoAdd()}
          >          
            {customers?.map((cus, index) => {
              return (
                <option key={index + 1} value={cus.customerId}>
                  {cus.customerName}
                </option>
              );
            })}
          </select> */}
          {ids.idCustomer ? (
            <select
              name="customerId"
              id="customerId"
              defaultValue={customerId}
              className="custom-select block custom-select-sm"
              onChange={() => getInfoAdd()}
            >
              <option value={customer?.customerId}>
                {customer.customerName}
              </option>
              {customers
                ?.filter((cus) => cus.customerId != customer.customerId)
                .map((cus, index) => {
                  return (
                    <option key={index + 1} value={cus.customerId}>
                      {cus.customerName}
                    </option>
                  );
                })}
            </select>
          ) : (
            <select
              name="customerId"
              id="customerId"
              defaultValue={customerId}
              className="custom-select block custom-select-sm"
              onChange={() => getInfoAdd()}
            >
              {customers?.map((cus, index) => {
                return (
                  <option key={index + 1} value={cus.customerId}>
                    {cus.customerName}
                  </option>
                );
              })}
            </select>
          )}
        </div>
        <div
          className="input-group float-left mr-1 input-project-m"
          style={{ width: 270 }}
        >
          <div className="input-group-prepend">
            <span className="input-group-text pickericon">
              <span className="far fa-file-lines"></span>
            </span>
          </div>
          <select
            id="projectId"
            className="custom-select block custom-select-sm projectId"
            defaultValue={projectId}
            onChange={() => listSetting()}
          >
            {projects?.map((pro, index) => {
              return (
                <option selected={ids?.idProject == pro.projectId} key={index + 1} value={pro.projectId}>
                  {pro.projectName}
                </option>
              );
            })}
          </select>
          {/* {projects.includes(project) ? (
            <select
              id="projectId"
              className="custom-select block custom-select-sm projectId"
              defaultValue={projectId}
              onChange={() => listSetting()}
            >
              <option value={project?.projectId}>
                {project.projectName}
              </option>
              {projects
                ?.filter((pro) => pro.projectId != project.projectId)
                .map((pro, index) => {
                  return (
                    <option key={index + 1} value={pro.projectId}>
                      {pro.projectName}
                    </option>
                  );
                })}
            </select>
          ) : (
            <select
              id="projectId"
              className="custom-select block custom-select-sm projectId"
              defaultValue={projectId}
              onChange={() => listSetting()}
            >
              {projects?.map((pro, index) => {
                return (
                  <option key={index + 1} value={pro.projectId}>
                    {pro.projectName}
                  </option>
                );
              })}
            </select>
          )} */}
        </div>
        <div className="input-group float-left mr-1" style={{ width: 270 }}>
          <div className="input-group-prepend">
            <span className="input-group-text pickericon">
              <span className="fas fa-gear"></span>
            </span>
          </div>
          <select
            id="typeSystem"
            defaultValue={ids?.typeSystem ? ids.typeSystem : 1}
            className="custom-select block custom-select-sm"
            onChange={() => getInfoAdd()}
          >
            <option value={1}>LOAD</option>
            <option value={2}>SOLAR</option>
            <option value={3}>WIND</option>
            <option value={4}>BATTERY</option>
            <option value={5}>GRID</option>
          </select>
        </div>
        <div className="search-buttons float-left">
          <button
            type="button"
            className="btn btn-outline-secondary btn-sm mr-1"
            onClick={() => listSetting()}
          >
            <i className="fa-solid fa-search" />
          </button>
        </div>
      </div>

      {status != null && (
        <div>
          {status.code === 200 ? (
            <div
              className="alert alert-success alert-dismissible fade show"
              role="alert"
            >
              <p className="m-0 p-0">{status?.message}</p>
            </div>
          ) : (
            <div className="alert alert-warning" role="alert">
              <p className="m-0 p-0">{status?.message}</p>
            </div>
          )}
        </div>
      )}
      <div id="main-content">
        <table className="table">
          <thead>
            <tr>
              <th width="40px">{t('content.no')}</th>
              <th width="370px">{t('content.category.setting.name')}</th>
              <th width="130px">{t('content.category.setting.value')}</th>
              <th>{t('content.description')}</th>
              <th width="150px">{t('content.update_date')}</th>
              <th width="40px">
                <i className="fa-regular fa-hand"></i>
              </th>
            </tr>
          </thead>
          <tbody>
            {parseInt(settings?.length) > 0 &&
              settings?.map((setting, index) => {
                return (
                  <tr key={setting.settingId}>
                    <td className="text-center">{index + 1}</td>
                    <td style={{ wordWrap: "break-word" }}>
                      {setting.settingMstId}-{setting.settingMstName}
                    </td>
                    <td style={{ wordWrap: "break-word" }}>
                      {setting.settingValue}
                    </td>
                    <td style={{ wordWrap: "break-word" }}>
                      {setting.description}
                    </td>
                    <td className="text-center">
                      {moment(setting.updateDate).format(CONS.DATE_FORMAT)}
                    </td>
                    <td className="text-center">
                      {permissionActions?.hasUpdatePermission && (
                        <Link
                          className="button-icon"
                          to={`/category/setting/${setting.settingId}/edit/`}
                          title="Chỉnh sửa"
                        >
                          <img
                            height="16px"
                            src="/resources/image/icon-edit.png"
                            alt="Cập nhật"
                          />
                        </Link>
                      )}
                    </td>
                  </tr>
                );
              })}
            {parseInt(settings?.length) === 0 && (
              <tr>
                <td colSpan={6} className="text-center">
                  Không có dữ liệu
                </td>
              </tr>
            )}
          </tbody>
        </table>

        <div id="main-button" className="text-left">
          <button
            type="button"
            className="btn btn-outline-secondary btn-s3m w-120px"
            onClick={() => history.push("/")}
          >
            <i className="fa-solid fa-house"></i> &nbsp;Trang chủ
          </button>
        </div>
      </div>
    </div>
  );
};

export default ListSetting;
