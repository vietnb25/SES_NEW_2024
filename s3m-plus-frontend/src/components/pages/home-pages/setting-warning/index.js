import React, { useEffect, useState } from "react";
import { Route, Switch } from 'react-router-dom';
import useAppStore from "../../../../applications/store/AppStore";
import AuthService from "../../../../services/AuthService";
import AccessDenied from "../../access-denied/AccessDenied";
import SettingRouters from "./setting.routes";
import { Router } from "react-router-dom/cjs/react-router-dom.min";
import SettingLink from "../setting/setting-link";

const SettingComponent = () => {
    const [role, setRole] = useState(AuthService.getRoleName());
    const [ids, setIds] = useState('')
    const callbackFunction = (childData) => {
        setIds(childData)
    }
    return (
        <>
            <div> <SettingLink active={1} path={"/setting-warning"} /></div>
            <hr></hr>
            <Switch>
                {
                    SettingRouters.map((route, i) => {
                        return <Route key={i} path={route.path} component={route.component}>
                            <route.component parentCallback={callbackFunction} ids={ids} />
                        </Route>
                    })
                }   
            </Switch>
       </>
    )
}
export default SettingComponent;