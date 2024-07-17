import React, { useState } from "react";
import { Route, Switch } from 'react-router-dom';
import AuthService from "../../../../services/AuthService";
import useAppStore from "../../../../applications/store/AppStore";
import AccessDenied from "../../access-denied/AccessDenied";
import UserRouters from './user.routes';

const UserComponent = () => {
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const [usersPath] = useState(useAppStore(state => state.categoryPath).find(f => f.path.includes("/category/users")));

    return (
        <Switch>
            {
                role === "ROLE_ADMIN" ? (
                    UserRouters.map((route, i) => {
                        let permissionActions = null;
                        if(route?.path === "/category/users"){
                            permissionActions = {
                                hasCreatePermission: true,
                                hasReadPermission: true,
                                hasUpdatePermission: true,
                                hasDeletePermission: true,
                            }
                        }
                        return ((route?.path === "/category/users") ) ?  (
                            <Route key={i} path={route.path} >
                                <route.component permissionActions={permissionActions}/>
                            </Route>
                        ) : 
                        <Route key={i} path={route.path} component={route.component}/>
                    })
                ) : (
                    UserRouters.map((route) => {
                        let permissionActions = null;
                        let path = paths.find(f => f.path === route.path);
                        let actions = path?.actions;
                        if(path?.path === "/category/users"){
                            permissionActions = {
                                hasCreatePermission: actions?.some(a => a === "CREATE"),
                                hasReadPermission: actions?.some(a => a === "READ"),
                                hasUpdatePermission: actions?.some(a => a === "UPDATE"),
                                hasDeletePermission: actions?.some(a => a === "DELETE"),
                            }
                        }

                        return usersPath.actions.map((act, i) => {
                            if(route.action === act){
                                return ((path?.path === "/category/users") ) ? (
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

export default UserComponent;