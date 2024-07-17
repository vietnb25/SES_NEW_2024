import React, { useEffect } from "react";
import './pickadate.css';
import { Link, Route, Switch, useLocation, useParams } from "react-router-dom";
import AccessDenied from "../../access-denied/AccessDenied";
import GridRouters from "./grid.routes";
import OverviewGrid from "./overview";
import WarningGrid from "./warning";
import overviewLoadService from "../../../../services/OverviewLoadService";
import { useState } from "react";

const Grid = ({ ajaxGetListData, ajaxGetMeterData }) => {
    const param = useParams();
    const location = useLocation();

    const setLine = (data) => {
        let a = document.getElementById("lineSet");
        a.style.left = `${data}%`;
    }

    const [projectInfo, setProjectInfo] = useState();

    const getPowers = async () => {
        let res = await overviewLoadService.getPower(param.customerId, param.projectId, "");
        if (res.status === 200) {
            setProjectInfo(res.data.projectInfor+"GRID");
    
        }
    }
    useEffect(() => {
        getPowers();
        const handleLine = () => {
            let pathname = location.pathname;
            let _html = document.getElementById("lineSet");
            if (pathname.includes('/home/systemMap')) {
                _html.style.left = `12.5%`;
            } else if (pathname.includes('/home/device-information')) {
                _html.style.left = `25.0%`;
            } else if (pathname.includes('/home/warning')) {
                _html.style.left = `37.5%`;
            } else if (pathname.includes('/home/report')) {
                _html.style.left = `50.0%`;
            } else if (pathname.includes('/home/forecast')) {
                _html.style.left = `62.5%`;
            } else if (pathname.includes('/home/setting')) {
                _html.style.left = `75.0%`;
            }   else if (pathname.includes('/home/receiver')) {
                _html.style.left = `87.5%`;
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
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/overview`}
                            onClick={() => setLine(0)}><i className="fas fa-home"></i>&nbsp; <span>Tổng quan</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/systemMap`}
                            onClick={() => setLine(12.5)}
                        ><i className="fas fa-diagram-project"></i>&nbsp; <span>Sơ đồ</span></Link>
                    </li>
                    <li>
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/device-information/0`}
                            onClick={(e) => {
                                setLine(25.0);
                                if (location.pathname.includes('/home/device-information')) {
                                    e.preventDefault();
                                    return
                                }
                            }}
                        ><i className="fas fa-server"></i>&nbsp; <span>Thông tin thiết bị</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/warning`}
                            onClick={() => setLine(37.5)}
                        ><i className="fas fa-triangle-exclamation"></i>&nbsp; <span>Cảnh báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/report`}
                            onClick={() => setLine(50.0)}
                        ><i className="fas fa-file-contract"></i>&nbsp; <span>Báo cáo</span></Link>
                    </li>

                    <li >
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/forecast`}
                            onClick={() => setLine(62.5)}
                        ><i className="fas fa-eye"></i>&nbsp; <span>Dự báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/setting`}
                            onClick={() => setLine(75.0)}
                        ><i className="fas fa-gear"></i>&nbsp; <span>Cài đặt cảnh báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/grid/${param.customerId}/${param.projectId}/receiver`}
                            onClick={() => setLine(87.5)}
                        ><i className="fas fa-arrow-down-short-wide"></i>&nbsp; <span>Nhận cảnh báo</span></Link>
                    </li>
                    <div className="line-grid" id="lineSet" style={{ width: "12.5%"}}></div>
                </ul>
            </div>

            <Switch>
                {
                    GridRouters.map((router, i) => {
                        return (
                            <Route key={i} path={router.path}>
                                <router.component
                                    projectId={param.projectId} customerId={param.customerId} ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData} projectInfo={projectInfo}
                                ></router.component>
                            </Route>
                        )
                    })
                }
            </Switch>
        </>
    )
};
export default Grid;
