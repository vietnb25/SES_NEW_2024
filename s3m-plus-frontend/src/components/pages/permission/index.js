import React, { useEffect, useState } from "react";
import { PanelMenu } from 'primereact/panelmenu';
import { useParams, Switch, Route, useHistory } from 'react-router-dom';
import PermissionSystemData from "./permission-system-data";
import PermissionCategory from "./permission-category";
import PermissionUrl from "./permission-url";
import UserService from "../../../services/UserService";

import "./index.css";

const Permission = () => {
    const { userId } = useParams();
    const history = useHistory();

    const [user, setUser] = useState();

    const items = [
        {
            label:'Phân quyền hệ thống',
            icon:'pi pi-fw pi-file',
            path: `/permission/${userId}/system`,
            command: e => {
                handleRedirect(e.item)
            }
        },
        {
            label:'Phân quyền danh mục',
            icon:'pi pi-align-justify',
            path: `/permission/${userId}/category`,
            command: e => {
                handleRedirect(e.item)
            }
        },
        // {
        //     label:'Phân quyền URL',
        //     icon:'pi pi-compass',
        //     path: `/permission/${userId}/path`,
        //     command: e => {
        //         handleRedirect(e.item)
        //     }
        // }
    ];

    const handleRedirect = (item) => {
        let path = item.path;
        history.push(path);
    }

    const loadUser = async () => {
        let res = await UserService.getUser(userId);
        if(res.status === 200){
            setUser(res.data.user);
        }
    }

    useEffect(() => {
        document.title = "Phân quyền";
        loadUser();
    }, []);

    return (
        <div id="page-body">
            <div id="main-content">
                <div id="project-list">
                    <div id="project-tree">
                        <div>
                            <p style={{padding: "8px 3px 0px 3px", fontSize: 16, color: "black"}}>Họ tên: <b>{user?.staffName}</b></p>
                            <p style={{padding: "8px 3px 0px 3px", fontSize: 16, color: "black"}}>Tài khoản: <b>{user?.username}</b></p>
                            <hr/>
                        </div>
                        <PanelMenu model={items} className="w-full md:w-25rem"/>
                        <input type="button" className="btn w-100" defaultValue="Quay lại" onClick={() => history.push("/category/users")}/>
                    </div>
                </div>
                <div id="project-info" style={{ position: "absolute" }}>
                    <Switch>
                        <Route path={`/permission/${userId}/system`}>
                            <PermissionSystemData userId={userId}/>
                        </Route>
                        <Route path={`/permission/${userId}/category`}>
                            <PermissionCategory userId={userId}/>
                        </Route>
                        {/* <Route path={`/permission/${userId}/path`}>
                            <PermissionUrl userId={userId}/>
                        </Route> */}
                    </Switch>
                </div>
            </div>
        </div>
    )
    
}

export default Permission;