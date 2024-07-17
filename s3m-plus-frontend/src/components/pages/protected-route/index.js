import React from "react";
import { Redirect, Route } from "react-router-dom";
import Footer from "../../layouts/footer";
import Header from './../../layouts/header/index';
import authService from "../../../services/AuthService";

const ProtectedRouter = ({component: Component, ...rest}) => {
    const token = authService.getToken();

    return (
        <div>
            {
                token ? 
                <Route {...rest} render={props => 
                    <>
                        <Header />
                            <Component {...props}/>
                        <Footer />
                    </>
                }/> :
                <Redirect to={{pathname: "/login"}}/>
            }
        </div>
    )
}

export default ProtectedRouter;