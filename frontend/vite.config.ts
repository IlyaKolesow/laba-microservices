import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Прокси обходит CORS при локальной разработке (сервисы на разных портах).
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api/products': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/orders': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/api/inventories': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/api/notifications': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
    },
  },
})
