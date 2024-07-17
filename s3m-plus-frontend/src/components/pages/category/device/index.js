import React, { useState } from "react";
import { Route, Switch } from 'react-router-dom';
import AuthService from "../../../../services/AuthService";
import useAppStore from "../../../../applications/store/AppStore";
import AccessDenied from "../../access-denied/AccessDenied";
import DeviceRouters from './device.routes';

const DeviceComponent = () => {
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const [devicePath] = useState(useAppStore(state => state.categoryPath).find(f => f.path.includes("/category/device")));
    const [dataId, setDataId] = useState();

    const callbackFunction = (childData) => {
        setDataId(childData)
    }

    return (
        <Switch>
            {
                role === "ROLE_ADMIN" ? (
                    DeviceRouters.map((route, i) => {
                        let permissionActions = null;
                        if (route?.path === "/category/device") {
                            permissionActions = {
                                hasCreatePermission: true,
                                hasReadPermission: true,
                                hasUpdatePermission: true,
                                hasDeletePermission: true,
                            }
                        }
                        return ((route?.path === "/category/device")) ? (
                            <Route key={i} path={route.path} >
                                <route.component permissionActions={permissionActions} dataId={dataId} />
                            </Route>
                        ) :
                            <Route key={i} path={route.path}>
                                <route.component dataId={dataId} />
                            </Route>
                    })
                ) : (
                    DeviceRouters.map((route) => {
                        let permissionActions = null;
                        let path = paths.find(f => f.path === route.path);
                        let actions = path?.actions;
                        if (path?.path === "/category/device") {
                            permissionActions = {
                                hasCreatePermission: actions?.some(a => a === "CREATE"),
                                hasReadPermission: actions?.some(a => a === "READ"),
                                hasUpdatePermission: actions?.some(a => a === "UPDATE"),
                                hasDeletePermission: actions?.some(a => a === "DELETE"),
                            }
                        }

                        return devicePath.actions.map((act, i) => {
                            if (route.action === act) {
                                return ((path?.path === "/category/device")) ? (
                                    <Route key={i} path={route.path} exact>
                                        <route.component permissionActions={permissionActions} />
                                    </Route>
                                ) :
                                    <Route key={i} path={route.path} component={route.component} exact />
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

export default DeviceComponent;