import { useCallback, useEffect, useState } from 'react'
import { productApi } from '../api/clients'
import type { ProductCreationDto, ProductDto } from '../api/types'
import { Feedback } from '../components/Feedback'

const emptyForm: ProductCreationDto = {
  name: '',
  weight: 0,
  description: '',
  price: 0,
}

export function ProductsPage() {
  const [items, setItems] = useState<ProductDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [notice, setNotice] = useState<string | null>(null)
  const [createForm, setCreateForm] = useState<ProductCreationDto>(emptyForm)
  const [editForm, setEditForm] = useState<ProductDto | null>(null)

  const reload = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setItems(await productApi.list())
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
      await productApi.create(createForm)
      setCreateForm(emptyForm)
      setNotice('Товар добавлен')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onUpdate(e: React.FormEvent) {
    e.preventDefault()
    if (!editForm?.id) return
    setNotice(null)
    setError(null)
    try {
      await productApi.update(editForm)
      setEditForm(null)
      setNotice('Изменения сохранены')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onDelete(id: number) {
    if (!confirm(`Удалить этот товар из каталога?`)) return
    setNotice(null)
    setError(null)
    try {
      await productApi.remove(id)
      setNotice('Товар удалён')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function startEdit(id: number) {
    setError(null)
    try {
      const p = await productApi.get(id)
      setEditForm(p)
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  return (
    <div className="page">
      <h1>Товары</h1>
      <Feedback message={error} tone="error" />
      <Feedback message={notice} tone="success" />

      <section className="panel">
        <h2>Добавить товар</h2>
        <form className="form-grid" onSubmit={onCreate}>
          <label>
            Название
            <input
              value={createForm.name}
              onChange={(e) =>
                setCreateForm((f) => ({ ...f, name: e.target.value }))
              }
              required
            />
          </label>
          <label>
            Вес
            <input
              type="number"
              step="any"
              value={createForm.weight}
              onChange={(e) =>
                setCreateForm((f) => ({
                  ...f,
                  weight: Number(e.target.value),
                }))
              }
            />
          </label>
          <label>
            Цена
            <input
              type="number"
              step="any"
              value={createForm.price}
              onChange={(e) =>
                setCreateForm((f) => ({ ...f, price: Number(e.target.value) }))
              }
            />
          </label>
          <label className="span-2">
            Описание
            <textarea
              rows={2}
              value={createForm.description}
              onChange={(e) =>
                setCreateForm((f) => ({
                  ...f,
                  description: e.target.value,
                }))
              }
            />
          </label>
          <button type="submit">Создать</button>
        </form>
      </section>

      {editForm && (
        <section className="panel">
          <h2>Редактировать #{editForm.id}</h2>
          <form className="form-grid" onSubmit={onUpdate}>
            <label>
              Название
              <input
                value={editForm.name}
                onChange={(e) =>
                  setEditForm((f) => f && { ...f, name: e.target.value })
                }
                required
              />
            </label>
            <label>
              Вес
              <input
                type="number"
                step="any"
                value={editForm.weight}
                onChange={(e) =>
                  setEditForm(
                    (f) => f && { ...f, weight: Number(e.target.value) },
                  )
                }
              />
            </label>
            <label>
              Цена
              <input
                type="number"
                step="any"
                value={editForm.price}
                onChange={(e) =>
                  setEditForm(
                    (f) => f && { ...f, price: Number(e.target.value) },
                  )
                }
              />
            </label>
            <label className="span-2">
              Описание
              <textarea
                rows={2}
                value={editForm.description}
                onChange={(e) =>
                  setEditForm(
                    (f) => f && { ...f, description: e.target.value },
                  )
                }
              />
            </label>
            <div className="form-actions">
              <button type="submit">Сохранить</button>
              <button type="button" className="btn-secondary" onClick={() => setEditForm(null)}>
                Отмена
              </button>
            </div>
          </form>
        </section>
      )}

      <section className="panel">
        <div className="panel__head">
          <h2>Каталог</h2>
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
                  <th>№</th>
                  <th>Название</th>
                  <th>Вес</th>
                  <th>Цена</th>
                  <th>Описание</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {items.map((p) => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.name}</td>
                    <td>{p.weight}</td>
                    <td>{p.price}</td>
                    <td className="cell-muted">{p.description}</td>
                    <td className="cell-actions">
                      <button
                        type="button"
                        className="btn-small"
                        onClick={() => p.id && void startEdit(p.id)}
                      >
                        Изменить
                      </button>
                      <button
                        type="button"
                        className="btn-small btn-danger"
                        onClick={() => p.id && void onDelete(p.id)}
                      >
                        Удалить
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {items.length === 0 && <p className="empty">В каталоге пока ничего нет</p>}
          </div>
        )}
      </section>
    </div>
  )
}
