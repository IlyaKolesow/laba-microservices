import { request } from './http'
import type {
  InventoryCreationDto,
  InventoryDto,
  NotificationCreationDto,
  NotificationDto,
  OrderCreationDto,
  OrderDto,
  ProductCreationDto,
  ProductDto,
  ProductIdsDto,
  ProductInventoryDto,
  ProductQuantityDto,
  UpdateQuantityDto,
} from './types'

export const productApi = {
  list: () => request<ProductDto[]>('/api/products'),
  get: (id: number) => request<ProductDto>(`/api/products/${id}`),
  create: (body: ProductCreationDto) =>
    request<ProductDto>('/api/products', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  update: (body: ProductDto) =>
    request<ProductDto>('/api/products', {
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  remove: (id: number) => request<string>(`/api/products/${id}`, { method: 'DELETE' }),
}

export const orderApi = {
  list: () => request<OrderDto[]>('/api/orders'),
  get: (id: string) => request<OrderDto>(`/api/orders/${id}`),
  create: (body: OrderCreationDto) =>
    request<OrderDto>('/api/orders', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  cancel: (id: string) => request<string>(`/api/orders/${id}`, { method: 'DELETE' }),
}

export const inventoryApi = {
  list: () => request<InventoryDto[]>('/api/inventories'),
  get: (inventoryId: number) =>
    request<InventoryDto>(`/api/inventories/${inventoryId}`),
  create: (body: InventoryCreationDto) =>
    request<InventoryDto>('/api/inventories', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  update: (body: InventoryDto) =>
    request<InventoryDto>('/api/inventories', {
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  remove: (inventoryId: number) =>
    request<string>(`/api/inventories/${inventoryId}`, { method: 'DELETE' }),
  getProducts: (inventoryId: number) =>
    request<ProductQuantityDto[]>(
      `/api/inventories/${inventoryId}/products`,
    ),
  addProducts: (inventoryId: number, body: ProductQuantityDto[]) =>
    request<ProductQuantityDto[]>(
      `/api/inventories/${inventoryId}/products`,
      { method: 'POST', body: JSON.stringify(body) },
    ),
  deleteProducts: (inventoryId: number, body: ProductIdsDto) =>
    request<string>(`/api/inventories/${inventoryId}/products`, {
      method: 'DELETE',
      body: JSON.stringify(body),
    }),
  takeProducts: (body: ProductQuantityDto[]) =>
    request<UpdateQuantityDto[]>(
      '/api/inventories/products/take',
      { method: 'PATCH', body: JSON.stringify(body) },
    ),
  updateQuantities: (body: UpdateQuantityDto[]) =>
    request<ProductQuantityDto[]>(
      '/api/inventories/products/quantity',
      { method: 'PATCH', body: JSON.stringify(body) },
    ),
  getByProductId: (productId: number) =>
    request<ProductInventoryDto[]>(
      `/api/inventories/product/${productId}`,
    ),
}

export const notificationApi = {
  list: () => request<NotificationDto[]>('/api/notifications'),
  get: (id: string) => request<NotificationDto>(`/api/notifications/${id}`),
  create: (body: NotificationCreationDto) =>
    request<NotificationDto>('/api/notifications', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
}
