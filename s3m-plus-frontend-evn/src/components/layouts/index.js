import React from "react";
import Header from "./header/index";
import Footer from "./footer/index";

const MainLayout = ({children}) => {
    return (
        <>
            <Header />
                {children}
            <Footer />
        </>
    )
}

export default MainLayout;