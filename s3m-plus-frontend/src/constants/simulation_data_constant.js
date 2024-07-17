import CONS from "./constant";
const SIMULATION_HOST = CONS.LOCAL_HOST + "common/simulation-data/";

const SIMULATION_API = {
    LIST_EP: SIMULATION_HOST + "/list/ep",
    LIST_MONEY : SIMULATION_HOST + "/list/money",
    EDIT_EP: SIMULATION_HOST + "edit/ep/",
    EDIT_MONEY: SIMULATION_HOST + "edit/money/",
    ADD_EP: SIMULATION_HOST + "add/ep",
    ADD_MONEY : SIMULATION_HOST + "add/money",
}

export default SIMULATION_API;