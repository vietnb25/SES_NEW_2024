import React, { useEffect, useState } from "react";
import $ from "jquery";
import moment from "moment";
import { Link, useHistory } from "react-router-dom";
import CONS from "../../../../../../constants/constant";
import { useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import SettingService from "../../../../../../services/SettingService";
const ListSetting = ({ permissionActions, projectInfo }) => {
    console.log(permissionActions.hasUpdatePermission);
    const [settings, setSettings] = useState([])
    const param = useParams();
    const [status, setStatus] = useState(null);
    const location = useLocation();

    const listSetting = async () => {
        let res = await SettingService.listSetting(param.customerId, param.projectId, CONS.SYSTEM_TYPE.GRID);
        if (res.status === 200 && parseInt(res.data.length) > 0) {
            setSettings(res.data);
        } else {
            setSettings([]);
        }
    };

    const setNotification = state => {
        if (state?.status === 200 && state?.message === "INSERT_SUCCESS") {
            setStatus({
                code: 200,
                message: "Thêm mới cài đặt thành công"
            });
        } else if (state?.status === 200 && state?.message === "UPDATE_SUCCESS") {
            setStatus({
                code: 200,
                message: "Chỉnh sửa cài đặt thành công"
            });
        }
        setTimeout(() => {
            setStatus(null);
        }, 3000);
    };

    useEffect(() => {
        document.title = "Danh sách cài đặt";
        if (location.state) {
            setNotification(location.state);
        };
        listSetting();
    }, [param.customerId, param.projectId]);
    return (
        <div className="tab-content">
            <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                <span className="project-tree">{projectInfo}</span>
            </div>
            {
                status != null &&
                <div>
                    {
                        status.code === 200 ?
                            <div className="alert alert-success alert-dismissible fade show" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div> :
                            <div className="alert alert-warning" role="alert">
                                <p className="m-0 p-0">{status?.message}</p>
                            </div>
                    }
                </div>

            }
            <table className="table tbl-overview mt-3">
                <thead>
                    <tr>
                        <th width="40px">TT</th>
                        <th width="370px">Cài đặt</th>
                        <th width="130px">Giá trị</th>
                        <th>Mô tả</th>
                        <th width="150px">Ngày cập nhật</th>
                        <th width="40px"><i className="fa-regular fa-hand"></i></th>
                    </tr>
                </thead>
                <tbody>
                    {
                        parseInt(settings?.length) > 0 && settings?.map((setting, index) => {
                            return (
                                <tr key={setting.settingId}>
                                    <td className="text-center">{index + 1}</td>
                                    <td style={{ wordWrap: "break-word" }}>{setting.settingMstName}</td>
                                    <td style={{ wordWrap: "break-word" }}>{setting.settingValue}</td>
                                    <td style={{ wordWrap: "break-word" }}>{setting.description}</td>
                                    <td className="text-center">{moment(setting.updateDate).format(CONS.DATE_FORMAT)}</td>
                                    <td className="text-center">
                                        {
                                            permissionActions?.hasUpdatePermission === true &&
                                            <Link className="button-icon" to={`/home/grid/${param.customerId}/${param.projectId}/setting/${setting.settingId}/edit`} title="Chỉnh sửa">
                                                <img height="16px" src="/resources/image/icon-edit.png" alt="Cập nhật" />
                                            </Link>
                                        }
                                    </td>
                                </tr>
                            )
                        })
                    }
                    {
                        parseInt(settings?.length) === 0 &&
                        <tr>
                            <td colSpan={6} className="text-center">Không có dữ liệu</td>
                        </tr>
                    }
                </tbody>

            </table>
        </div>
    )
}

export default ListSetting;