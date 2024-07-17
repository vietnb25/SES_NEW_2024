import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/warning-car"

const WARNING_CAR_API = {
    GET_WARNING_CAR: BASE_API,
    ADD_WARNING_CAR: BASE_API + "/add/",
    UPDATE_WARNING_CAR: BASE_API + "/update/",
    GET_WARNING_CAR_BY_ID: BASE_API + "/getWarningCarById",
}

export default WARNING_CAR_API