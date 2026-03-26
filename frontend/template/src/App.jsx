
import { BrowserRouter, Routes, Route, Navigate, Link, Outlet } from 'react-router-dom';
import Home from './page/Home';
import About from './page/About';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Login from './page/Login';
import Profile from './page/Profile';
import Register from './page/Register';
import ProductDetail from './page/ProductDetail';
import { AuthProvider } from './api/context/AuthContext';
import BuyerOrders from './page/BuyerOrders';
import Cart from './page/Cart';
import SellerLayout from './components/SellerLayout';
import SellerOrders from './PagesSeller/Orders';
import SellerProducts from './PagesSeller/Products';
import StoreSettings from './PagesSeller/StoreSettings';
import AddProduct from './PagesSeller/AddProduct';
import EditProduct from './PagesSeller/EditProduct';
import Store from './page/Store';
import Checkout from './page/Checkout';

import './scss/main.scss'; // 1. 載入全局 SCSS 樣式 

// 2. 實作 ProtectedRoute 組件
// 邏輯：檢查 localStorage 有無 token，沒有就踢回 login
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('access_token');
  if (!token) {
    // 沒登入的話，強制導航到登入頁
    return <Navigate to="/login" replace />;
  }
  return children;
};

// 1. 建立一個買家專用的 Layout
const BuyerLayout = () => (
  <>
    <Navbar variant="home" />
    <Outlet /> {/* 這裡會渲染 Home, About 等買家頁面 */}
    <Footer variant="home" />
  </>
);

function App() {
  return (
    <AuthProvider>
      {/* BrowserRouter 包裝整個應用，啟用路由功能 */}
      <BrowserRouter>
        {/* Routes 定義了不同網址對應到的內容 */}
        <Routes>
          {/* A. 買家模式路由 (有 Navbar & Footer) */}
          <Route element={<BuyerLayout />}>
            <Route path="/" element={<Home />} />
            <Route path="/about" element={<About />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/product/:id" element={<ProductDetail />} />
            <Route path="/store" element={<Store />} />
            <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
            <Route path="/orders" element={<ProtectedRoute><BuyerOrders /></ProtectedRoute>} />
            <Route path="/cart" element={<ProtectedRoute><Cart /></ProtectedRoute>} />
            <Route path="/checkout" element={<ProtectedRoute><Checkout /></ProtectedRoute>} />

          </Route>

          {/* B. 賣家模式路由 (只有 SellerLayout 的側邊欄，沒有買家的 Navbar) */}
          <Route path="/seller" element={<ProtectedRoute><SellerLayout /></ProtectedRoute>}>
            <Route path="products" element={<SellerProducts />} />
            <Route path="products/add" element={<AddProduct />} />
            <Route path="products/edit/:id" element={<EditProduct />} />
            <Route path="orders" element={<SellerOrders />} />
            <Route path="settings" element={<StoreSettings />} />
          </Route>

        </Routes>

      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
