import React, { useEffect, useState } from "react";
import './pickadate.css';
import { Link, Route, Switch, useLocation, useParams } from "react-router-dom";
import OverviewLoad from "./overview";
import WarningLoad from "./warning";
import ReportLoad from "./report";
import SystemMapLoad from "./systemMap";
import DeviceInformation from "./device-information";
import Forecast from "./forecast"
import ControlLoad from "./control";
import ControlSolar from "./control";
import ReportSolar from "./report";
import SystemMapSolar from "./systemMap";
import WarningSolar from "./warning";
import OverviewSolar from "./overview";
import overviewLoadService from "../../../../services/OverviewLoadService";
import ReceiverWarning from "./receiver-warning";
import SettingWarning from "./setting";


const Solar = ({ ajaxGetListData, ajaxGetMeterData }) => {
    const param = useParams();
    const location = useLocation();

    const [projectInfo, setProjectInfo] = useState();

    const getPowers = async () => {
        let res = await overviewLoadService.getPower(param.customerId, param.projectId, "");
        if (res.status === 200) {
            setProjectInfo(res.data.projectInfor + "SOLAR");
        }
    }
    const setLine = (data) => {
        let a = document.getElementById("lineSet");
        a.style.left = `${data}%`;
    }
    useEffect(() => {
        getPowers();
        const handleLine = () => {
            let pathname = location.pathname;
            let _html = document.getElementById("lineSet");
            if (pathname.includes('/home/systemMap')) {
                _html.style.left = `11.1%`;
            } else if (pathname.includes('/home/device-information')) {
                _html.style.left = `22.2%`;
            } else if (pathname.includes('/home/warning')) {
                _html.style.left = `33.3%`;
            } else if (pathname.includes('/home/control')) {
                _html.style.left = `44.4%`;
            } else if (pathname.includes('/home/report')) {
                _html.style.left = `55.5%`;
            } else if (pathname.includes('/home/forecast')) {
                _html.style.left = `66.6%`;
            } else if (pathname.includes('/home/setting')) {
                _html.style.left = `77.7%`;
            } else if (pathname.includes('/home/receiver')) {
                _html.style.left = `88.8%`;
            }else {
                _html.style.left = `0%`;
            }
        };
        handleLine();
    }, [location]);

    return (
        <>
            <div className="tab-container">
                <ul className="menu">
                    <li>
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}`}
                            onClick={() => setLine(0)}><i className="fas fa-home"></i>&nbsp; <span>Tổng quan</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/systemMap`}
                            onClick={() => setLine(11.1)}
                        ><i className="fas fa-diagram-project"></i>&nbsp; <span>Sơ đồ</span></Link>
                    </li>
                    <li>
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/device-information/0`}
                            onClick={(e) => {
                                setLine(22.2);
                                if (location.pathname.includes('/home/device-information')) {
                                    e.preventDefault();
                                    return
                                }
                            }}
                        ><i className="fas fa-server"></i>&nbsp; <span>Thông tin thiết bị</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/warning`}
                            onClick={() => setLine(33.3)}
                        ><i className="fas fa-triangle-exclamation"></i>&nbsp; <span>Cảnh báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/control`}><i className="fas fa-wrench"
                            onClick={() => setLine(44.4)}
                        ></i>&nbsp; <span>Điều khiển</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/report`}
                            onClick={() => setLine(55.5)}
                        ><i className="fas fa-file-contract"></i>&nbsp; <span>Báo cáo</span></Link>
                    </li>

                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/forecast`}
                            onClick={() => setLine(66.6)}
                        ><i className="fas fa-eye"></i>&nbsp; <span>Dự báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/setting`}
                            onClick={() => setLine(77.7)}
                        ><i className="fas fa-gear"></i>&nbsp; <span>Cài đặt cảnh báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/solar/${param.customerId}/${param.projectId}/receiver`}
                            onClick={() => setLine(88.8)}
                        ><i className="fas fa-arrow-down-short-wide"></i>&nbsp; <span>Nhận cảnh báo</span></Link>
                    </li>
                    <div className="line" id="lineSet" style={{ width: "11.2%"}}></div>
                </ul>
            </div>

            <Switch>
                <Route path={"/home/solar/:customerId/:projectId/receiver"}>
                    <ReceiverWarning
                        projectId={param.projectId} projectInfo={projectInfo}
                    ></ReceiverWarning>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/control"}>
                    <ControlSolar
                        projectId={param.projectId} projectInfo={projectInfo}
                    ></ControlSolar>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/report"}>
                    <ReportSolar
                        projectInfo={projectInfo}
                    ></ReportSolar>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/systemMap/:systemMapId/:deviceId"}>
                    <SystemMapSolar projectId={param.projectId} customerId={param.customerId} ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData} projectInfo={projectInfo}  ></SystemMapSolar>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/systemMap/:systemMapId"}>
                    <SystemMapSolar projectId={param.projectId} customerId={param.customerId} ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData} projectInfo={projectInfo} ></SystemMapSolar>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/systemMap"}>
                    <SystemMapSolar projectId={param.projectId} customerId={param.customerId} ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData} projectInfo={projectInfo} ></SystemMapSolar>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/forecast"}>
                    <Forecast projectInfo={projectInfo}></Forecast>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/setting"}>
                    <SettingWarning projectInfo={projectInfo}></SettingWarning>
                </Route>
                <Route path={"/home/solar/:customerId/report/download/:path"}>
                    <ReportSolar projectInfo={projectInfo}></ReportSolar>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/warning"}>
                    <WarningSolar
                        customerId={param.customerId}
                        projectId={param.projectId}
                        projectInfo={projectInfo}
                    />
                </Route>
                <Route path={"/home/solar/:customerId/:projectId/device-information/:deviceId"}>
                    <DeviceInformation
                        projectInfo={projectInfo}
                    ></DeviceInformation>
                </Route>
                <Route path={"/home/solar/:customerId/:projectId"}>
                    <OverviewSolar projectInfo={projectInfo}></OverviewSolar>
                </Route>
            </Switch>
        </>
    )
};
export default Solar;
