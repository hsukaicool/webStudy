
import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import Home from './page/Home';
import About from './page/About';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Login from './page/Login';
import Profile from './page/Profile';
import Register from './page/Register';
import ProductDetail from './page/ProductDetail';

import './scss/main.scss'; // 1. 載入全局 SCSS 樣式 

// 2. 實作 ProtectedRoute 組件
// 邏輯：檢查 localStorage 有無 token，沒有就踢回 login
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    // 沒登入的話，強制導航到登入頁
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  return (
    // BrowserRouter 包裝整個應用，啟用路由功能
    <BrowserRouter>
      {/* Routes 定義了不同網址對應到的內容 */}
      <Navbar variant="home" />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/productDetail" element={<ProductDetail />} />

        <Route path="/profile" element={<ProtectedRoute> <Profile /> </ProtectedRoute>} />

      </Routes>
      <Footer variant="home" />
    </BrowserRouter>
  );
}

export default App;
