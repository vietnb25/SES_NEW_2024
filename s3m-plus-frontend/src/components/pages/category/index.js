import React, {  useState, useEffect } from "react";
import { Switch, Route } from 'react-router-dom';
import CategoryRouters from "./category.routes";
import AccessDenied from "../access-denied/AccessDenied";
import AuthService from "../../../services/AuthService";
import useAppStore from "../../../applications/store/AppStore";
import { useTranslation } from 'react-i18next';

const Category = () => {
    const [role] = useState(AuthService.getRoleName());
    const [paths] = useState(useAppStore(state => state.categoryPath));
    const { i18n } = useTranslation();

    useEffect(() => {

    }, [i18n.language])

    return (
        <div style={{overflow: "auto"}}>
            <Switch>
                {
                    role === "ROLE_ADMIN" ? CategoryRouters.map((route, i) => {
                        return <Route key={i} path={route.path} component={route.component}/>
                    }) : CategoryRouters.map((route, i) => {
                        return (paths.some(p => p.path === route.path)) && (
                            <Route key={i} path={route.path} component={route.component}/>
                        )
                    })
                }
                <Route path="*" component={AccessDenied} />
            </Switch>
        </div>
    )
}

export default Category;