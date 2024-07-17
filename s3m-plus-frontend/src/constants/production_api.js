import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/"

const PRODUCTION_API = {
    GET_PRODUCTIONS : BASE_API + "production",
    GET_PRODUCTION_STEPS : BASE_API + "production_step",
    ADD_PRODUCTION : BASE_API + "add_production",
    ADD_PRODUCTION_STEP : BASE_API + "add_production_step",
}

export default PRODUCTION_API
