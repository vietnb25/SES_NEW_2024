import CONS from "./constant";

const CUSTOMER_HOST = CONS.LOCAL_HOST + "common/customer/";

const BASE_API_CUSTOMER = {
    CUSTOMER: {
        LIST: CUSTOMER_HOST + "list",
        ADD: CUSTOMER_HOST + "add",
        DELETE: CUSTOMER_HOST + "delete/",
        CUSTOMER: CUSTOMER_HOST + "",
        UPDATE: CUSTOMER_HOST + "update/",
        SEARCH: CUSTOMER_HOST + "search/",
        CHECK: CUSTOMER_HOST + "check/",
        GET_OTP : CUSTOMER_HOST + "getOTP/",
        CHECK_OTP : CUSTOMER_HOST + "checkOTP",
        EXPIRE_OTP : CUSTOMER_HOST + "expireOTP",
        LIST_IDS: CUSTOMER_HOST + "listIds"
    }
}
export default BASE_API_CUSTOMER;