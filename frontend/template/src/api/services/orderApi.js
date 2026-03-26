import axiosClient from '../axiosClient';

const orderApi = {
    /** 🚀 買家：獲取我的訂單列表 */
    getBuyerOrders: () => axiosClient.get('/api/orders/my-orders'),

    /** 🚀 賣家：獲取我的銷售明細 */
    getMySales: () => axiosClient.get('/api/orders/my-sales'),

    /** 🚀 建立訂單 */
    createOrder: (orderData) => axiosClient.post('/api/orders', orderData)
};

export default orderApi;
