import ControlLoad from './control/index';
import DeviceInformation from './device-information/index';
import Forecast from './forecast/index';
import OverviewLoad from './overview/index';
import ReportLoad from './report/index';
import SystemMapLoad from "./systemMap/index";
import WarningLoad from './warning/index';
import ReceiverWarning from './receiver-warning';
import SettingWarning from './setting';

const LoadRouters = [
    {
        component: ReceiverWarning,
        path: '/home/load/:customerId/:projectId/receiver',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: ControlLoad,
        path: '/home/load/:customerId/:projectId/control',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: ReportLoad,
        path: '/home/load/:customerId/:projectId/report',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SystemMapLoad,
        path: '/home/load/:customerId/:projectId/systemMap/:systemMapId/:deviceId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SystemMapLoad,
        path: '/home/load/:customerId/:projectId/systemMap/:systemMapId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: SystemMapLoad,
        path: '/home/load/:customerId/:projectId/systemMap',
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
        path: '/home/load/:customerId/:projectId/forecast',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: ReportLoad,
        path: '/home/load/report/download/:path',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: WarningLoad,
        path: '/home/load/:customerId/:projectId/warning',
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
        path: '/home/load/:customerId/:projectId/device-information/:deviceId',
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
        path: '/home/load/:customerId/:projectId/setting',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    },
    {
        component: OverviewLoad,
        path: '/home/load/:customerId/:projectId',
        children: [
            
        ],
        link: {
            title: "",
            icon: ""
        },
        actions: []
    }
];

export default LoadRouters;