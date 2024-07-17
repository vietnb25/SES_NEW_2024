import React, { useState } from "react";
import { Route, Switch } from 'react-router-dom';
import AuthService from "../../../../services/AuthService";
import useAppStore from "../../../../applications/store/AppStore";
import AccessDenied from "../../access-denied/AccessDenied";
import ObjectTypeRouters from './objectType.routes';
import { path } from "d3";

const ObjectTypeComponent = () => {
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const [objectTypePath] = useState(useAppStore(state => state.categoryPath).find(f => f.path.includes("/category/object-type")));
    const [dataId, setDataId] = useState();

    const callbackFunction = (childData) => {
        setDataId(childData)
    }

    return (
        <Switch>
            {
                role === "ROLE_ADMIN" ? (
                    ObjectTypeRouters.map((route, i) => {
                        let permissionActions = null;
                        if (route?.path === "/category/object-type") {
                            permissionActions = {
                                hasCreatePermission: true,
                                hasReadPermission: true,
                                hasUpdatePermission: true,
                                hasDeletePermission: true,
                            }
                        }

                        console.log(route);
                        console.log("per", permissionActions);
                        return ((route?.path === "/category/object-type")) ? (
                            <Route key={i} path={route.path} >
                                <route.component permissionActions={permissionActions} dataId={dataId} parentCallback={callbackFunction} />
                            </Route>
                        ) :
                            <Route key={i} path={route.path}>
                                <route.component dataId={dataId} parentCallback={callbackFunction} />
                            </Route>
                    })
                ) : (
                    ObjectTypeRouters.map((route) => {
                        let permissionActions = null;
                        let path = paths.find(f => f.path === route.path);
                        let actions = path?.actions;
                        if (path?.path === "/category/object-type") {
                            permissionActions = {
                                hasCreatePermission: actions?.some(a => a === "CREATE"),
                                hasReadPermission: actions?.some(a => a === "READ"),
                                hasUpdatePermission: actions?.some(a => a === "UPDATE"),
                                hasDeletePermission: actions?.some(a => a === "DELETE"),

                            }
                        }

                        return objectTypePath.actions.map((act, i) => {
                            if (route.action === act) {
                                return ((path?.path === "/category/object-type")) ? (
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

export default ObjectTypeComponent;