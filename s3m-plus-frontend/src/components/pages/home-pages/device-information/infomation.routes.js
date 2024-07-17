import DeviceInfor from "./view";
import Information from "./information"
import ObjectOneLevel from "./object-one-level"
import ObjectTwoLevel1 from "./object-two-level-1"
import ObjectTwoLevel2 from "./object-two-level-2"

const SettingRouters = [
    {
        component: ObjectTwoLevel1,
        path: '/:customerId/:projectId/device-information/objectTypeTwoLv1/:objectTypeId/:type',
    },
    {
        component: ObjectTwoLevel2,
        path: '/:customerId/:projectId/device-information/:objectTypeId/:objectId/:type',
    },
    {
        component: ObjectOneLevel,
        path: '/:customerId/:projectId/device-information/objectTypeOneLv/:objectTypeId',
    },
    {
        component: Information,
        path: '/:customerId/device-information/:deviceId',
    },
    {
        component: DeviceInfor,
        path: '/:customerId/:projectId/device-information',
    },
    {
        component: DeviceInfor,
        path: '/:customerId/device-information',
    }
];

export default SettingRouters;