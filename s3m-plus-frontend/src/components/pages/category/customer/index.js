import React, { useState } from "react";
import { Route, Switch } from 'react-router-dom';
import CustomerRouters from "./customer.routes";
import AuthService from "../../../../services/AuthService";
import AccessDenied from './../../access-denied/AccessDenied';
import useAppStore from "../../../../applications/store/AppStore";

const Customer = () => {
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const [customerPath] = useState(useAppStore(state => state.categoryPath).find(f => f.path.includes("/category/customer")));

    return (
        <Switch>
            {
                role === "ROLE_ADMIN" ? (
                    CustomerRouters.map((route, i) => {
                        let permissionActions = null;
                        if(route?.path === "/category/customer"){
                            permissionActions = {
                                hasCreatePermission: true,
                                hasReadPermission: true,
                                hasUpdatePermission: true,
                                hasDeletePermission: true,
                            }
                        }
                        return ((route?.path === "/category/customer") ) ?  (
                            <Route key={i} path={route.path} >
                                <route.component permissionActions={permissionActions}/>
                            </Route>
                        ) : 
                        <Route key={i} path={route.path} component={route.component}/>
                    })
                ) : (
                    CustomerRouters.map((route) => {
                        let permissionActions = null;
                        let path = paths.find(f => f.path === route.path);
                        let actions = path?.actions;
                        if(path?.path === "/category/customer"){
                            permissionActions = {
                                hasCreatePermission: actions?.some(a => a === "CREATE"),
                                hasReadPermission: actions?.some(a => a === "READ"),
                                hasUpdatePermission: actions?.some(a => a === "UPDATE"),
                                hasDeletePermission: actions?.some(a => a === "DELETE"),
                            }
                        }

                        return customerPath.actions.map((act, i) => {
                            if(route.action === act){
                                return ((path?.path === "/category/customer") ) ? (
                                    <Route key={i} path={route.path} exact>
                                        <route.component permissionActions={permissionActions}/>
                                    </Route>
                                ) : 
                                <Route key={i} path={route.path} component={route.component} exact/>
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

export default Customer;