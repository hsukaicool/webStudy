# 前端 API 統一管理層架構 (API Layer Architecture)

## 📌 此架構解決了專案的什麼痛點？

在專案擴展的過程中，若只在各個 React 元件 (如 `Home.jsx`) 中直接打 API，會導致以下問題：
1. **高耦合 (High Coupling) 與代碼冗餘 (Code Duplication)**：每個請求都需要手動設定 Base URL 與 Headers。
2. **錯誤處理零散**：無法做到全域的 401 登出或 500 系統異常提示，需要在使用 API 的地方重複寫 `try..catch`。
3. **維護成本極高**：後端網址或規格一旦變動，需要在成百上千個檔案中尋找舊的 API 進行修改。

---

## 🏗️ 解決方案：模組化 API 管理層

我們基於**單一職責原則 (Single Responsibility Principle, SRP)**，建立了一個專責處理網路請求的 `API Layer`：

### 1. 核心實例封裝 (Infrastructure)
建立專用的 `axios` 實例，作為專案內所有請求的「唯一出口」。

```javascript
// src/api/request.js (參考範本)
import axios from 'axios';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000/api', 
  timeout: 10000, 
});

// Request 請求攔截器：統一處理 Token 等 Headers
request.interceptors.request.use((config) => {
    // 範例：將儲存的 token 自動帶入所有請求
    // const token = localStorage.getItem('token');
    // if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
}, (error) => Promise.reject(error));

// Response 回應攔截器：統一處理錯誤狀況 (如 401, 500)
request.interceptors.response.use((response) => {
    // 過濾多餘資料狀態，直接回傳實際的 data 
    return response.data;
}, (error) => {
    // 在這裡處理全域的例外狀況 (例如呼叫自定義的 Toast 跳錯誤訊息)
    console.error('API 請求發生錯誤:', error);
    return Promise.reject(error);
});

export default request;
```

### 2. 依功能切分模組 (Service Modules)
將相同 Domain 的 API 從 UI 元件中抽離並集中管理。

```javascript
// src/api/user.js (參考範本)
import request from './request';

export const userApi = {
  getUsers: () => request.get('/users'),
  getUserById: (id) => request.get(`/users/${id}`),
  login: (credentials) => request.post('/auth/login', credentials),
};
```

### 3. 在 UI 元件中的優雅呼叫
最終在 React 元件中，寫法會變得非常乾淨，UI 只負責關注資料與畫面渲染。

```javascript
// src/page/Home.jsx (參考範本)
import { useEffect, useState } from 'react';
import { userApi } from '../api/user';

export default function Home() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    userApi.getUsers().then(data => setUsers(data));
  }, []);
  
  // ...
}
```

---

## 📝 履歷與面試亮點 (技術含金量論述)

您可以將這段經歷寫進履歷，並在面試時重點講述，展現您對現代化開發架構的掌握：

### 履歷上的專案技術亮點 (Bullet points)：
- **「設計並實作前端 API 集中管理層 (API Layer)，透過 axios 攔截器 (Interceptors) 進行全域的網路請求與回應處理（包含自動攜帶 Token、統一錯誤攔截機制與錯誤提示）。」**
- **「將網路請求邏輯與 UI 元件完全解耦 (Decoupling)，遵循單一職責原則 (SRP)，大幅提升了代碼的專案級復用性 (Reusability) 與可維護性，使新功能的 API 對接時間縮短了 XX%。」**

### 面試實戰問答策略 (STAR 技巧)：
* **面試官提問**：「你在開發這個專案時，遇過最難維護或最花時間的部分是什麼？你怎麼解決？」
* **你可以這樣回答 (A - Action & R - Result)**：「專案初期所有的 API 都是分散在各個 Component 直接 call `fetch`，這導致當 token 需要加入 header 時，或是後端規格改變，我需要改好幾個地方，非常容易漏掉引發 Bug。
為了解決這個問題，我重構了專案，導入了統一的 **API Layer 架構**。我引入了 `axios` 並創建了一個全域的實例加上 interceptors，專門負責攔截並處理 Token 以及統一捕捉 401 或是 500 錯誤跳出警示。接著把 API 按照業務邏輯（像是 Member, Product）拆分成獨立模組。
結果就是，UI 元件的程式碼變得極度乾淨，未來後端要換網址我也只要改一個 `request.js` 就好。這個架構的引入不但解決了難以維護的問題，也讓現在開發新功能時除錯的效率大幅提升！」
