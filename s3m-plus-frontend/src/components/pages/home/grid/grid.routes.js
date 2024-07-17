import OverviewGrid from "./overview/index";
import DeviceInformation from "./device-information/index";
import Forecast from "./forecast/index";
import Report from "./report/index";
import SystemMap from "./systemMap/index";
import WarningGrid from "./warning/index";
import ReceiverWarning from "./receiver-warning";
import SettingWarning from "./setting";

const GridRouters = [
    {
        component: ReceiverWarning,
        path: '/home/grid/:customerId/:projectId/receiver',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: OverviewGrid,
        path: '/home/grid/:customerId/:projectId/overview',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: DeviceInformation,
        path: '/home/grid/:customerId/:projectId/device-information/:deviceId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: Forecast,
        path: '/home/grid/:customerId/:projectId/forecast',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: Report,
        path: '/home/grid/:customerId/:projectId/report',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: WarningGrid,
        path: '/home/grid/:customerId/:projectId/warning',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SystemMap,
        path: '/home/grid/:customerId/:projectId/systemMap/:systemMapId/:deviceId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SystemMap,
        path: '/home/grid/:customerId/:projectId/systemMap/:systemMapId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SystemMap,
        path: '/home/grid/:customerId/:projectId/systemMap',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SettingWarning,
        path: '/home/grid/:customerId/:projectId/setting',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: OverviewGrid,
        path: '/home/grid/:customerId/:projectId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    }
];

export default GridRouters;