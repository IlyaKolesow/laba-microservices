import { NavLink, Navigate, Route, Routes } from 'react-router-dom'
import { InventoriesPage } from './pages/InventoriesPage'
import { NotificationsPage } from './pages/NotificationsPage'
import { OrdersPage } from './pages/OrdersPage'
import { ProductsPage } from './pages/ProductsPage'

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  `nav-link${isActive ? ' nav-link--active' : ''}`

export default function App() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <span className="app-title">Панель</span>
        <nav className="app-nav">
          <NavLink to="/products" className={navLinkClass}>
            Товары
          </NavLink>
          <NavLink to="/orders" className={navLinkClass}>
            Заказы
          </NavLink>
          <NavLink to="/inventory" className={navLinkClass}>
            Склады
          </NavLink>
          <NavLink to="/notifications" className={navLinkClass}>
            Уведомления
          </NavLink>
        </nav>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Navigate to="/products" replace />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/inventory" element={<InventoriesPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="*" element={<Navigate to="/products" replace />} />
        </Routes>
      </main>
    </div>
  )
}
