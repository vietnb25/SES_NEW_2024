import AddCustomer from "./add";
import EditCustomer from "./edit";
import ListCusomer from "./list";

const CustomerRouters = [
    {
        component: AddCustomer,
        path: '/category/customer/add',
        link: {
            title: "Thêm mới khách hàng",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditCustomer,
        path: '/category/customer/edit/:id',
        link: {
            title: "Cập nhật khách hàng",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListCusomer,
        path: '/category/customer',
        link: {
            title: "Danh sách khách hàng",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default CustomerRouters;