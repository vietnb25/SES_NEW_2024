import React, { useState } from "react";
import { Route, Switch } from 'react-router-dom';
import useAppStore from "../../../../applications/store/AppStore";
import AuthService from "../../../../services/AuthService";
import AccessDenied from "../../access-denied/AccessDenied";
import LoadTypeRouters from './load-type.routes';

const LoadType = () => {
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const [loadTypePath] = useState(useAppStore(state => state.categoryPath).find(f => f.path.includes("/category/load-type")));

    return (
        <Switch>
            {
                role === "ROLE_ADMIN" ? (
                    LoadTypeRouters.map((route, i) => {
                        let permissionActions = null;
                        if (route?.path === "/category/load-type") {
                            permissionActions = {
                                hasCreatePermission: true,
                                hasReadPermission: true,
                                hasUpdatePermission: true,
                                hasDeletePermission: true,
                            }
                        }
                        return ((route?.path === "/category/load-type")) ? (
                            <Route key={i} path={route.path} >
                                <route.component permissionActions={permissionActions} />
                            </Route>
                        ) :
                            <Route key={i} path={route.path} component={route.component} />
                    })
                ) : (
                    LoadTypeRouters.map((route) => {
                        let permissionActions = null;
                        let path = paths.find(f => f.path === route.path);
                        let actions = path?.actions;
                        if (path?.path === "/category/load-type") {
                            permissionActions = {
                                hasCreatePermission: actions?.some(a => a === "CREATE"),
                                hasReadPermission: actions?.some(a => a === "READ"),
                                hasUpdatePermission: actions?.some(a => a === "UPDATE"),
                                hasDeletePermission: actions?.some(a => a === "DELETE"),
                            }
                        }

                        return loadTypePath.actions.map((act, i) => {
                            if (route.action === act) {
                                return ((path?.path === "/category/load-type")) ? (
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

export default LoadType;