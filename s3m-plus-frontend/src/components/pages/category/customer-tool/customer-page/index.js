import { useState } from 'react';
//import './index.css';
import CustomerService from '../../../../../services/CustomerService';
import { useEffect } from 'react';
import moment from "moment/moment";
import CONS from '../../../../../constants/constant';
import { Link, useHistory } from 'react-router-dom';
import { t } from 'i18next';

const CustomerTool = () => {
    const [customers, setCustomers] = useState([]);
    const history = useHistory();

    const getCustomerList = async () => {
        var customersResponse = await CustomerService.getListCustomer();
        setCustomers(customersResponse.data);
    }

    function handleProject(customerId) {
        history.push({
            pathname: "/category/tool-page/project-page/" + customerId
        });
    }

    useEffect(() => {
        getCustomerList();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left text-uppercase"><i className="fas fa-user-tie"></i> &nbsp;{t('content.category.customer.customer_list')}</h5>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th width="250px">{t('content.category.customer.customer_id')}</th>
                            <th width="400px">{t('content.category.customer.customer_name')} </th>
                            <th>{t('content.description')}</th>
                            <th width="150px">{t('content.update_date')}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            customers?.map(
                                (customer, index) => <tr key={customer.customerId} onClick={() => handleProject(customer.customerId)}>
                                    <td className="text-center">{index + 1}</td>
                                    <td style={{ wordWrap: "break-word" }}>{customer.customerCode}</td>
                                    <td style={{ wordWrap: "break-word" }}>{customer.customerName}</td>
                                    <td style={{ wordWrap: "break-word" }}>{customer.description}</td>
                                    <td className="text-center" style={{ wordWrap: "break-word" }}>{moment(customer.updateDate).format(CONS.DATE_FORMAT)}</td>
                                </tr>
                            )
                        }
                        <tr style={{ display: customers.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={6}>{t('content.no_data')}</td>
                        </tr>

                    </tbody>

                </table>
                <div id="main-button" className="text-left">
                    <Link to={`/`}>
                        <button type="button" className="btn btn-outline-secondary btn-s3m w-120px">
                            <i className="fa-solid fa-house"></i> &nbsp;{t('content.home')}
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default CustomerTool;