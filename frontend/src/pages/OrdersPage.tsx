import { useCallback, useEffect, useState } from 'react'
import { orderApi } from '../api/clients'
import type { OrderDto, ProductQuantityDto } from '../api/types'
import { Feedback } from '../components/Feedback'

function emptyLine(): ProductQuantityDto {
  return { productId: 0, quantity: 1 }
}

export function OrdersPage() {
  const [orders, setOrders] = useState<OrderDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [notice, setNotice] = useState<string | null>(null)
  const [customerName, setCustomerName] = useState('')
  const [lines, setLines] = useState<ProductQuantityDto[]>([emptyLine()])
  const [expanded, setExpanded] = useState<string | null>(null)
  const [expandedOrder, setExpandedOrder] = useState<OrderDto | null>(null)

  const reload = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setOrders(await orderApi.list())
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void reload()
  }, [reload])

  async function onCreate(e: React.FormEvent) {
    e.preventDefault()
    setNotice(null)
    setError(null)
    const products = lines.filter((l) => l.productId > 0 && l.quantity > 0)
    if (products.length === 0) {
      setError('Добавьте хотя бы одну позицию: укажите номер товара из каталога и количество')
      return
    }
    try {
      await orderApi.create({ customerName, products })
      setCustomerName('')
      setLines([emptyLine()])
      setNotice('Заказ создан')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  function addLine() {
    setLines((l) => [...l, emptyLine()])
  }

  function updateLine(i: number, patch: Partial<ProductQuantityDto>) {
    setLines((rows) =>
      rows.map((row, j) => (j === i ? { ...row, ...patch } : row)),
    )
  }

  function removeLine(i: number) {
    setLines((rows) => rows.filter((_, j) => j !== i))
  }

  async function onCancel(id: string) {
    if (!confirm('Отменить заказ?')) return
    setNotice(null)
    setError(null)
    try {
      await orderApi.cancel(id)
      setNotice('Заказ отменён')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function toggleDetails(id: string) {
    if (expanded === id) {
      setExpanded(null)
      setExpandedOrder(null)
      return
    }
    setExpanded(id)
    setError(null)
    try {
      setExpandedOrder(await orderApi.get(id))
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
      setExpanded(null)
    }
  }

  return (
    <div className="page">
      <h1>Заказы</h1>
      <Feedback message={error} tone="error" />
      <Feedback message={notice} tone="success" />

      <section className="panel">
        <h2>Новый заказ</h2>
        <form onSubmit={onCreate}>
          <label className="block-label">
            Имя клиента
            <input
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
              required
            />
          </label>

          <h3 className="subheading">Состав заказа</h3>
          {lines.map((line, i) => (
            <div key={i} className="line-row">
              <label>
                Номер товара
                <input
                  type="number"
                  value={line.productId || ''}
                  placeholder="как в каталоге"
                  min={1}
                  onChange={(e) =>
                    updateLine(i, { productId: Number(e.target.value) })
                  }
                />
              </label>
              <label>
                Количество
                <input
                  type="number"
                  min={1}
                  value={line.quantity}
                  onChange={(e) =>
                    updateLine(i, { quantity: Number(e.target.value) })
                  }
                />
              </label>
              <button
                type="button"
                className="btn-secondary btn-small"
                onClick={() => removeLine(i)}
                disabled={lines.length <= 1}
              >
                Убрать
              </button>
            </div>
          ))}
          <div className="form-actions">
            <button type="button" className="btn-secondary" onClick={addLine}>
              Добавить строку
            </button>
            <button type="submit">Оформить заказ</button>
          </div>
        </form>
      </section>

      <section className="panel">
        <div className="panel__head">
          <h2>Список заказов</h2>
          <button type="button" className="btn-secondary" onClick={() => void reload()}>
            Обновить
          </button>
        </div>
        {loading ? (
          <p>Загрузка…</p>
        ) : (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Номер</th>
                  <th>Клиент</th>
                  <th>Создан</th>
                  <th>Строк</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {orders.map((o) => (
                  <tr key={o.id}>
                    <td className="mono">{o.id}</td>
                    <td>{o.customerName}</td>
                    <td className="cell-muted">{new Date(o.createdAt).toLocaleString()}</td>
                    <td>{o.products?.length ?? 0}</td>
                    <td className="cell-actions">
                      <button
                        type="button"
                        className="btn-small"
                        onClick={() => void toggleDetails(o.id)}
                      >
                        {expanded === o.id ? 'Свернуть' : 'Подробнее'}
                      </button>
                      <button
                        type="button"
                        className="btn-small btn-danger"
                        onClick={() => void onCancel(o.id)}
                      >
                        Отменить
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {expanded && expandedOrder && (
              <div className="detail-card">
                <strong>Состав заказа</strong>
                <ul>
                  {expandedOrder.products?.map((p, idx) => (
                    <li key={idx}>
                      Товар {p.productId} — {p.quantity} шт.
                    </li>
                  ))}
                </ul>
              </div>
            )}
            {orders.length === 0 && <p className="empty">Нет заказов</p>}
          </div>
        )}
      </section>
    </div>
  )
}
