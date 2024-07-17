import React, { useEffect, useState } from "react";
import { Route, Switch } from 'react-router-dom';
import useAppStore from "../../../../applications/store/AppStore";
import AuthService from "../../../../services/AuthService";
import AccessDenied from "../../access-denied/AccessDenied";
import SettingRouters from "./settingShift.routes";
import SettingLink from "../setting/setting-link";

const SettingShiftComponent = () => {
    const [role, setRole] = useState(AuthService.getRoleName());
    return (
        <>
            <div> <SettingLink active={2} path={"/setting-shift"} /></div>
            <hr></hr>
            <Switch>
                {
                    SettingRouters.map((route, i) => {
                        return <Route key={i} path={route.path} component={route.component} />
                    })
                }
            </Switch>
        </>
    )
}
export default SettingShiftComponent;