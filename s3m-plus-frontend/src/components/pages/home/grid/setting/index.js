import React, { useState } from "react";
import { Route, Switch } from 'react-router-dom';
import AuthService from "../../../../../services/AuthService";
import SettingRouters from "./setting.routes";
import useAppStore from "../../../../../applications/store/AppStore";
import AccessDenied from "../../../access-denied/AccessDenied";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";

const SettingWarning = ({projectInfo}) => {
    
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const param = useParams();
    const [settingPath] = useState(useAppStore(state => state.categoryPath).find(f => f.path.includes("/home/grid/:customerId/:projectId/setting")));

    return (
        <Switch>
            {
                role === "ROLE_ADMIN" ? (
                    SettingRouters.map((route, i) => {
                        let permissionActions = null;
                        if(route?.path === "/home/grid/:customerId/:projectId/setting"){
                            permissionActions = {
                                hasCreatePermission: true,
                                hasReadPermission: true,
                                hasUpdatePermission: true,
                                hasDeletePermission: true,
                            }
                        }
                        return ((route?.path === "/home/grid/:customerId/:projectId/setting") ) ?  (
                            <Route key={i} path={route.path} >
                                <route.component permissionActions={permissionActions} projectInfo={projectInfo}/>
                            </Route>
                        ) : 
                        <Route key={i} path={route.path} component={route.component} projectInfo={projectInfo}/>
                    })
                ) : (
                    SettingRouters.map((route) => {
                        let permissionActions = null;
                        let path = paths.find(f => f.path === route.path);
                        let actions = path?.actions;
                        if(path?.path === `/home/load/${param.customerId}/${param.projectId}/setting`){
                            permissionActions = {
                                hasCreatePermission: actions?.some(a => a === "CREATE"),
                                hasReadPermission: actions?.some(a => a === "READ"),
                                hasUpdatePermission: actions?.some(a => a === "UPDATE"),
                                hasDeletePermission: actions?.some(a => a === "DELETE"),
                            }
                        }

                        return settingPath.actions.map((act, i) => {
                            if(route.action === act){
                                return ((path?.path === "/home/grid/:customerId/:projectId/setting") ) ? (
                                    <Route key={i} path={route.path} exact>
                                        <route.component permissionActions={permissionActions} projectInfo={projectInfo}/>
                                    </Route>
                                ) : 
                                <Route key={i} path={route.path} component={route.component} exact projectInfo={projectInfo}/>
                            }
                            return null
                        })
                    })
                )
            }
            <Route path="*" component={AccessDenied} />
        </Switch>
    )
}
export default SettingWarning;