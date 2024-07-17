import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/landmarks-energy-plans"

const LANDMARK_AND_PLAN_API = {
    LIST_PLANS: BASE_API + '/list-plan',
    LIST_LANDMARK:BASE_API+'/list-landmark',
    UPDATE_PLAN:BASE_API+ '/plan?customer=',
    UPDATE_LANDMARK:BASE_API+ '/landmark?customer=',
    INSERT_PLAN:BASE_API+ '/plans?',
    INSERT_LANDMARK:BASE_API+ '/landmarks?'
}

export default LANDMARK_AND_PLAN_API