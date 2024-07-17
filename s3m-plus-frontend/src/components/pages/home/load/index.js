import React, { useEffect } from "react";
import './pickadate.css';
import { Link, Route, Switch, useLocation, useParams } from "react-router-dom";
import AccessDenied from "../../access-denied/AccessDenied";
import LoadRouters from "./load.routes";
import overviewLoadService from "../../../../services/OverviewLoadService";
import { useState } from "react";


const Load = ({ ajaxGetListData, ajaxGetMeterData }) => {
    const param = useParams();
    const location = useLocation();

    const setLine = (data) => {
        let a = document.getElementById("lineSet");
        a.style.left = `${data}%`;
    }
    const [projectInfor, setProjectInfor] = useState();

    const getPowers = async () => {
        let res = await overviewLoadService.getPower(param.customerId, param.projectId, "");
        if (res.status === 200) {
            console.log(res.data);
            setProjectInfor(res.data.projectInfor+"LOAD");
    
        }
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
                        <Link to={`/home/load/${param.customerId}/${param.projectId}`}
                            onClick={() => setLine(0)}><i className="fas fa-home"></i>&nbsp; <span>Tổng quan</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/systemMap`}
                            onClick={() => setLine(11.1)}
                        ><i className="fas fa-diagram-project"></i>&nbsp; <span>Sơ đồ</span></Link>
                    </li>
                    <li>
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/device-information/0`}
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
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/warning`}
                            onClick={() => setLine(33.3)}
                        ><i className="fas fa-triangle-exclamation"></i>&nbsp; <span>Cảnh báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/control`}><i className="fas fa-wrench"
                            onClick={() => setLine(44.4)}
                        ></i>&nbsp; <span>Điều khiển</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/report`}
                            onClick={() => setLine(55.5)}
                        ><i className="fas fa-file-contract"></i>&nbsp; <span>Báo cáo</span></Link>
                    </li>

                    <li >
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/forecast`}
                            onClick={() => setLine(66.6)}
                        ><i className="fas fa-eye"></i>&nbsp; <span>Dự báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/setting`}
                            onClick={() => setLine(77.7)}
                        ><i className="fas fa-gear"></i>&nbsp; <span>Cài đặt cảnh báo</span></Link>
                    </li>
                    <li >
                        <Link to={`/home/load/${param.customerId}/${param.projectId}/receiver`}
                            onClick={() => setLine(88.8)}
                        ><i class="fas fa-arrow-down-short-wide"></i>&nbsp; <span>Nhận cảnh báo</span></Link>
                    </li>
                    <div className="line" id="lineSet"  style={{ width: "11.2%"}}></div>
                </ul>
            </div>

            <Switch>
                {
                    LoadRouters.map((router, i) => {
                        return (
                            <Route key={i} path={router.path}>
                                <router.component
                                    projectId={param.projectId} customerId={param.customerId} ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData} projectInfo={projectInfor}
                                ></router.component>
                            </Route>
                        )
                    })
                }

                <Route path={"*"}>
                    <AccessDenied />
                </Route>
            </Switch>
        </>
    )
};
export default Load;
