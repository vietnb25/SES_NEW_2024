import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/plan"

const PLAN_API = {
    GET_PLAN: BASE_API,
    ADD_PLAN: BASE_API + "/add/",
    UPDATE_PLAN: BASE_API + "/update/",
    GET_PLAN_BY_ID: BASE_API + "/getPlanById",
    DELETE_PLAN_BY_ID: BASE_API + "/deletePlanById",
}

export default PLAN_API
