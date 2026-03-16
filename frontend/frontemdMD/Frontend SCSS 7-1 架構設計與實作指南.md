# Frontend SCSS 7-1 架構設計與實作指南

根據您的需求，以下為基礎的 SCSS 模組化設計（7-1 Pattern 簡化版），並且為每個檔案提供實務上必備的預設程式碼。

> [!TIP]
> 這種架構不僅能大幅預防樣式互相干擾，還能**作為履歷上的優化成果重點**。您可以將以下的程式碼分別貼入對應的 SCSS 檔案中。

---

## 1. Variables（變數）

用來存放全站共用的設定值，所有的顏色與字型都該統一在這裡管控。

### [src/scss/variables/_color.scss](file:///Volumes/Crucial%20X8/coding/webStudy/frontend/template/src/scss/variables/_color.scss)
存放您網站的品牌色、輔助色彩與背景色：
```scss
// 主色與輔助色
$primary-color: #007bff;
$secondary-color: #6c757d;

// 狀態顏色 (成功、警告、錯誤等)
$success-color: #28a745;
$danger-color: #dc3545;
$warning-color: #ffc107;
$info-color: #17a2b8;

// 字體與背景色
$text-primary: #333333;
$text-secondary: #666666;
$bg-light: #f8f9fa;
$bg-dark: #343a40;
```

### [src/scss/variables/_typography.scss](file:///Volumes/Crucial%20X8/coding/webStudy/frontend/template/src/scss/variables/_typography.scss)
存放字型家族、大小與行高：
```scss
// 字體家族設定 (優先載入無襯線與常用中文字體)
$font-family-base: 'Helvetica Neue', Arial, 'LiHei Pro', '微軟正黑體', sans-serif;

// 字體大小與粗細基準
$font-size-base: 16px;
$font-weight-normal: 400;
$font-weight-bold: 700;

// 行高基準
$line-height-base: 1.5;
```

---

## 2. Mixins（混入）

用來存放「會重複使用的 SCSS 函數」。最常見的就是 RWD 響應式斷點設定。

### [src/scss/mixins/_respond-to.scss](file:///Volumes/Crucial%20X8/coding/webStudy/frontend/template/src/scss/mixins/_respond-to.scss)
```scss
// 定義 RWD 斷點
$breakpoints: (
  'sm': 576px,  // 手機版
  'md': 768px,  // 平板版
  'lg': 992px,  // 一般電腦
  'xl': 1200px  // 大螢幕
);

// RWD 響應式 Mixin
// 使用方式：@include respond-to('md') { ... }
@mixin respond-to($breakpoint) {
  @if map-has-key($breakpoints, $breakpoint) {
    @media (min-width: map-get($breakpoints, $breakpoint)) {
      @content;
    }
  } @else {
    @warn "找不到斷點：#{$breakpoint}。";
  }
}
```

---

## 3. Base（基礎）

用來進行跨瀏覽器的樣式重置 (Reset)，以及給予 HTML 基礎標籤屬性。

### `src/scss/base/_reset.scss`
解決每個瀏覽器有自帶不同預設 margin 的問題：
```scss
// 全站基礎 Reset
*, *::before, *::after {
  box-sizing: border-box; // 確保 padding 與 border 不會改變元素總寬度
  margin: 0;
  padding: 0;
}

html, body {
  width: 100%;
  height: 100%;
}

ul, ol {
  list-style: none; // 拿掉清單預設的小圓點
}
```

### `src/scss/base/_global.scss`
載入變數，設定全站基礎共用樣式：
```scss
// 注意：在這裡引入變數，讓這個檔案看得懂
@use '../variables/color' as *;
@use '../variables/typography' as *;

body {
  font-family: $font-family-base;
  font-size: $font-size-base;
  line-height: $line-height-base;
  color: $text-primary;
  background-color: $bg-light;
  -webkit-font-smoothing: antialiased; // 讓字體在 Mac 會比較平滑好看
}

a {
  color: $primary-color;
  text-decoration: none;
  transition: color 0.3s ease;
  
  &:hover {
    color: darken($primary-color, 10%);
    text-decoration: underline;
  }
}
```

---

## 4. Components（元件）

針對可以被重複使用的小模組撰寫樣式（例如按鈕與卡片）。

### `src/scss/components/_button.scss`
```scss
@use '../variables/color' as *;
@use '../variables/typography' as *;

.btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  text-align: center;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.1s;

  &:active {
    transform: scale(0.98); // 按下時微縮的動態效果
  }

  // 主色按鈕樣式
  &-primary {
    background-color: $primary-color;
    color: #fff;
    &:hover {
      background-color: darken($primary-color, 10%);
    }
  }
}
```

### `src/scss/components/_card.scss`
```scss
@use '../variables/color' as *;

.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); // 使用陰影增加立體感
  padding: 20px;
  margin-bottom: 20px;
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15); // 滑過疊加的陰影
  }

  &__title {
    font-size: 1.25rem;
    color: $text-primary;
    margin-bottom: 15px;
  }

  &__content {
    color: $text-secondary;
  }
}
```

---

## 5. Layout（佈局）

定義全站的巨觀佈局（例如 Header 導覽列與網格容器）。

### `src/scss/layout/_header.scss`
```scss
@use '../variables/color' as *;

.header {
  width: 100%;
  background-color: $bg-dark;
  color: #fff;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .nav {
    display: flex;
    gap: 15px; // 輕鬆產生間距

    a {
      color: #fff;
      &:hover {
        color: $primary-color;
        text-decoration: none;
      }
    }
  }
}
```

### `src/scss/layout/_grid.scss`
這個部分是專案中負責全站置中、處理左右留白的靈魂元件：
```scss
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto; // 讓區塊水平置中
  padding: 0 15px;
}
```

---

## 6. Pages（頁面）

針對特定的頁面做微調。如果你寫的樣式只會出現在首頁，就不該寫在 global 或 component。

### `src/scss/pages/_home.scss`
```scss
@use '../variables/color' as *;

.page-home {
  // 將特定於首頁的區塊包起來，防止污染
  .hero-section {
    background-color: lighten($primary-color, 30%); // 使用函數調整背景色
    text-align: center;
    padding: 60px 20px;
    border-radius: 8px;
    margin-bottom: 30px;
  }
}
```

---

## 7. Main（總匯出檔）

這個檔案就像是一本總目錄，負責將以上所有的 Partial 檔案組裝起來。
你只要在 React ([App.jsx](file:///Volumes/Crucial%20X8/coding/webStudy/frontend/template/src/App.jsx) 或 `main.jsx`) 中引入一次 `import './scss/main.scss';` 即可。

### `src/scss/main.scss`
```scss
// =============== SCSS 總匯出檔 =============== //

// 1. Variables (變數必須最先引入，否則後面會找不到變數)
@use 'variables/color';
@use 'variables/typography';

// 2. Mixins
@use 'mixins/respond-to';

// 3. Base (基礎重置與全域樣式)
@use 'base/reset';
@use 'base/global';

// 4. Components (共用小元件)
@use 'components/button';
@use 'components/card';

// 5. Layout (大佈局配置)
@use 'layout/header';
@use 'layout/grid';

// 6. Pages (單一頁面專屬微調)
@use 'pages/home';

// ============================================= //
```
