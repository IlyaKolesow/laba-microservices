export type ProductDto = {
  id?: number
  name: string
  weight: number
  description: string
  price: number
}

export type ProductCreationDto = Omit<ProductDto, 'id'>

export type ProductQuantityDto = {
  productId: number
  quantity: number
}

export type OrderDto = {
  id: string
  customerName: string
  createdAt: string
  products: ProductQuantityDto[]
}

export type OrderCreationDto = {
  customerName: string
  products: ProductQuantityDto[]
}

export type InventoryDto = {
  id?: number
  name: string
  city: string
}

export type InventoryCreationDto = Omit<InventoryDto, 'id'>

export type UpdateQuantityDto = {
  inventoryId: number
  productId: number
  delta: number
}

export type ProductInventoryDto = {
  id: number
  productId: number
  inventoryId: number
  quantity: number
}

export type ProductIdsDto = {
  productIds: number[]
}

export type NotificationDto = {
  id: string
  type: string
  message: string
  createdAt: string
}

export type NotificationCreationDto = Pick<NotificationDto, 'type' | 'message'>
