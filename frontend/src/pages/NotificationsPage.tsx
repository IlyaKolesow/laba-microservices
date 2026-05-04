import { useCallback, useEffect, useState } from 'react'
import { notificationApi } from '../api/clients'
import type { NotificationDto } from '../api/types'
import { Feedback } from '../components/Feedback'

export function NotificationsPage() {
  const [items, setItems] = useState<NotificationDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [notice, setNotice] = useState<string | null>(null)
  const [type, setType] = useState('INFO')
  const [message, setMessage] = useState('')
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const [selected, setSelected] = useState<NotificationDto | null>(null)

  const reload = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setItems(await notificationApi.list())
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
    try {
      await notificationApi.create({ type, message })
      setMessage('')
      setNotice('Уведомление создано')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function openOne(id: string) {
    if (selectedId === id) {
      setSelectedId(null)
      setSelected(null)
      return
    }
    setSelectedId(id)
    setError(null)
    try {
      setSelected(await notificationApi.get(id))
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
      setSelectedId(null)
      setSelected(null)
    }
  }

  return (
    <div className="page">
      <h1>Уведомления</h1>
      <Feedback message={error} tone="error" />
      <Feedback message={notice} tone="success" />

      <section className="panel">
        <h2>Новое уведомление</h2>
        <form className="form-grid" onSubmit={onCreate}>
          <label>
            Тип
            <input
              value={type}
              onChange={(e) => setType(e.target.value)}
              placeholder="например: заказ, доставка, система"
            />
          </label>
          <label className="span-2">
            Текст
            <textarea
              rows={3}
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              required
            />
          </label>
          <button type="submit">Отправить</button>
        </form>
      </section>

      <section className="panel">
        <div className="panel__head">
          <h2>Лента</h2>
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
                  <th>Тип</th>
                  <th>Сообщение</th>
                  <th>Время</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {items.map((n) => (
                  <tr key={n.id}>
                    <td className="mono cell-clip">{n.id}</td>
                    <td>{n.type}</td>
                    <td className="cell-muted">{n.message}</td>
                    <td>{new Date(n.createdAt).toLocaleString()}</td>
                    <td>
                      <button
                        type="button"
                        className="btn-small"
                        onClick={() => void openOne(n.id)}
                      >
                        {selectedId === n.id ? 'Свернуть' : 'Подробнее'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {selected && (
              <div className="detail-card">
                <dl className="detail-dl">
                  <dt>Тип</dt>
                  <dd>{selected.type}</dd>
                  <dt>Текст</dt>
                  <dd>{selected.message}</dd>
                  <dt>Создано</dt>
                  <dd>{new Date(selected.createdAt).toLocaleString()}</dd>
                  <dt>Номер записи</dt>
                  <dd className="mono">{selected.id}</dd>
                </dl>
              </div>
            )}
            {items.length === 0 && <p className="empty">Нет уведомлений</p>}
          </div>
        )}
      </section>
    </div>
  )
}
