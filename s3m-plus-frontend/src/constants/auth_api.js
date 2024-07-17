import CONS from "./constant";

const localhost = CONS.LOCAL_HOST;

const AUTH_API = {
    FORGOT_PASSWORD: localhost + "auth/forgot-password",
    RESET_PASSWORD: localhost + "auth/reset-password",
    CHANGE_PASSWORD_FIRST_LOGIN: localhost + "auth/change-password-first-login"
}

export default AUTH_API;