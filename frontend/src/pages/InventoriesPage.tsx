import { useCallback, useEffect, useState } from 'react'
import { inventoryApi } from '../api/clients'
import type {
  InventoryCreationDto,
  InventoryDto,
  ProductQuantityDto,
  UpdateQuantityDto,
} from '../api/types'
import { Feedback } from '../components/Feedback'

const emptyInv: InventoryCreationDto = { name: '', city: '' }
const pq = (): ProductQuantityDto => ({ productId: 0, quantity: 0 })
const uq = (): UpdateQuantityDto => ({
  inventoryId: 0,
  productId: 0,
  delta: 0,
})

export function InventoriesPage() {
  const [list, setList] = useState<InventoryDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [notice, setNotice] = useState<string | null>(null)

  const [createForm, setCreateForm] = useState(emptyInv)
  const [editForm, setEditForm] = useState<InventoryDto | null>(null)

  const [focusId, setFocusId] = useState<number | null>(null)
  const [focusProducts, setFocusProducts] = useState<ProductQuantityDto[]>([])

  const [addBatch, setAddBatch] = useState<ProductQuantityDto[]>([{ ...pq(), quantity: 1 }])
  const [takeBatch, setTakeBatch] = useState<ProductQuantityDto[]>([{ ...pq(), quantity: 1 }])
  const [deltaBatch, setDeltaBatch] = useState<UpdateQuantityDto[]>([uq()])
  const [removeIdsRaw, setRemoveIdsRaw] = useState('')
  const [lookupProductId, setLookupProductId] = useState('')
  const [lookupResult, setLookupResult] = useState<Awaited<
    ReturnType<typeof inventoryApi.getByProductId>
  > | null>(null)

  const reload = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setList(await inventoryApi.list())
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void reload()
  }, [reload])

  async function reloadFocusProducts(invId: number) {
    setFocusProducts(await inventoryApi.getProducts(invId))
  }

  async function selectInventory(inv: InventoryDto) {
    if (!inv.id) return
    setFocusId(inv.id)
    setError(null)
    try {
      await reloadFocusProducts(inv.id)
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e))
      setFocusId(null)
      setFocusProducts([])
    }
  }

  async function onCreateInv(e: React.FormEvent) {
    e.preventDefault()
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.create(createForm)
      setCreateForm(emptyInv)
      setNotice('Склад создан')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onUpdateInv(e: React.FormEvent) {
    e.preventDefault()
    if (!editForm?.id) return
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.update(editForm)
      setEditForm(null)
      setNotice('Склад обновлён')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onDeleteInv(id: number) {
    if (!confirm(`Удалить склад #${id}?`)) return
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.remove(id)
      if (focusId === id) {
        setFocusId(null)
        setFocusProducts([])
      }
      setNotice('Склад удалён')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onAddProducts(e: React.FormEvent) {
    e.preventDefault()
    if (!focusId) return
    const body = addBatch.filter((x) => x.productId > 0 && x.quantity > 0)
    if (!body.length) {
      setError('Укажите номер товара и количество штук')
      return
    }
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.addProducts(focusId, body)
      setAddBatch([{ ...pq(), quantity: 1 }])
      setNotice('Товар принят на склад')
      await reloadFocusProducts(focusId)
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onRemoveProducts(e: React.FormEvent) {
    e.preventDefault()
    if (!focusId) return
    const ids = removeIdsRaw
      .split(/[\s,;]+/)
      .map((s) => s.trim())
      .filter(Boolean)
      .map(Number)
      .filter((n) => Number.isFinite(n))
    if (!ids.length) {
      setError('Перечислите номера товаров через пробел или запятую')
      return
    }
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.deleteProducts(focusId, { productIds: ids })
      setRemoveIdsRaw('')
      setNotice('Товары убраны со склада')
      await reloadFocusProducts(focusId)
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onTake(e: React.FormEvent) {
    e.preventDefault()
    const body = takeBatch.filter((x) => x.productId > 0 && x.quantity > 0)
    if (!body.length) {
      setError('Укажите товар и сколько штук списать')
      return
    }
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.takeProducts(body)
      setTakeBatch([{ ...pq(), quantity: 1 }])
      setNotice('Списание выполнено')
      if (focusId) await reloadFocusProducts(focusId)
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function onDelta(e: React.FormEvent) {
    e.preventDefault()
    const body = deltaBatch.filter(
      (x) => x.inventoryId > 0 && x.productId > 0 && x.delta !== 0,
    )
    if (!body.length) {
      setError('Укажите склад, товар и изменение количества (не ноль)')
      return
    }
    setNotice(null)
    setError(null)
    try {
      await inventoryApi.updateQuantities(body)
      setDeltaBatch([uq()])
      setNotice('Остатки обновлены')
      if (focusId) await reloadFocusProducts(focusId)
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    }
  }

  async function lookup(e: React.FormEvent) {
    e.preventDefault()
    const pid = Number(lookupProductId)
    if (!Number.isFinite(pid) || pid <= 0) {
      setError('Введите корректный номер товара')
      return
    }
    setError(null)
    try {
      setLookupResult(await inventoryApi.getByProductId(pid))
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
      setLookupResult(null)
    }
  }

  function startEdit(inv: InventoryDto) {
    setEditForm({ ...inv })
  }

  return (
    <div className="page">
      <h1>Склады</h1>
      <Feedback message={error} tone="error" />
      <Feedback message={notice} tone="success" />

      <section className="panel">
        <h2>Новый склад</h2>
        <form className="form-grid" onSubmit={onCreateInv}>
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
            Город
            <input
              value={createForm.city}
              onChange={(e) =>
                setCreateForm((f) => ({ ...f, city: e.target.value }))
              }
              required
            />
          </label>
          <button type="submit">Создать склад</button>
        </form>
      </section>

      {editForm && (
        <section className="panel">
          <h2>Редактировать склад #{editForm.id}</h2>
          <form className="form-grid" onSubmit={onUpdateInv}>
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
              Город
              <input
                value={editForm.city}
                onChange={(e) =>
                  setEditForm((f) => f && { ...f, city: e.target.value })
                }
                required
              />
            </label>
            <div className="form-actions">
              <button type="submit">Сохранить</button>
              <button
                type="button"
                className="btn-secondary"
                onClick={() => setEditForm(null)}
              >
                Отмена
              </button>
            </div>
          </form>
        </section>
      )}

      <section className="panel">
        <div className="panel__head">
          <h2>Склады</h2>
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
                  <th>Город</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {list.map((inv) => (
                  <tr key={inv.id} className={focusId === inv.id ? 'row-active' : ''}>
                    <td>{inv.id}</td>
                    <td>{inv.name}</td>
                    <td>{inv.city}</td>
                    <td className="cell-actions">
                      <button
                        type="button"
                        className="btn-small"
                        onClick={() => void selectInventory(inv)}
                      >
                        {focusId === inv.id ? 'Открыто' : 'Остатки'}
                      </button>
                      <button
                        type="button"
                        className="btn-small"
                        onClick={() => startEdit(inv)}
                      >
                        Изменить
                      </button>
                      <button
                        type="button"
                        className="btn-small btn-danger"
                        onClick={() => inv.id && void onDeleteInv(inv.id)}
                      >
                        Удалить
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {list.length === 0 && <p className="empty">Нет складов</p>}
          </div>
        )}
      </section>

      {focusId != null && (
        <section className="panel">
          <h2>Остатки на складе #{focusId}</h2>
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Товар</th>
                  <th>Штук</th>
                </tr>
              </thead>
              <tbody>
                {focusProducts.map((r, i) => (
                  <tr key={i}>
                    <td>{r.productId}</td>
                    <td>{r.quantity}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {focusProducts.length === 0 && (
              <p className="empty">На складе пока нет позиций</p>
            )}
          </div>

          <h3 className="subheading">Принять товар на этот склад</h3>
          <form onSubmit={onAddProducts}>
            {addBatch.map((row, i) => (
              <div key={i} className="line-row">
                <label>
                  Номер товара
                  <input
                    type="number"
                    value={row.productId || ''}
                    onChange={(e) =>
                      setAddBatch((rows) =>
                        rows.map((r, j) =>
                          j === i
                            ? { ...r, productId: Number(e.target.value) }
                            : r,
                        ),
                      )
                    }
                  />
                </label>
                <label>
                  Количество
                  <input
                    type="number"
                    min={1}
                    value={row.quantity}
                    onChange={(e) =>
                      setAddBatch((rows) =>
                        rows.map((r, j) =>
                          j === i
                            ? { ...r, quantity: Number(e.target.value) }
                            : r,
                        ),
                      )
                    }
                  />
                </label>
                <button
                  type="button"
                  className="btn-secondary btn-small"
                  onClick={() =>
                    setAddBatch((rows) => rows.filter((_, j) => j !== i))
                  }
                  disabled={addBatch.length <= 1}
                >
                  Убрать
                </button>
              </div>
            ))}
            <div className="form-actions">
              <button
                type="button"
                className="btn-secondary"
                onClick={() => setAddBatch((b) => [...b, { ...pq(), quantity: 1 }])}
              >
                Ещё строка
              </button>
              <button type="submit">Добавить на склад</button>
            </div>
          </form>

          <h3 className="subheading">Убрать товары с этого склада</h3>
          <form className="form-grid" onSubmit={onRemoveProducts}>
            <label className="span-2">
              Номера товаров через пробел или запятую
              <input
                placeholder="101 102"
                value={removeIdsRaw}
                onChange={(e) => setRemoveIdsRaw(e.target.value)}
              />
            </label>
            <button type="submit">Удалить со склада</button>
          </form>
        </section>
      )}

      <section className="panel">
        <h2>Списание со складов</h2>
        <p className="panel-lead">
          Укажите, какой товар и сколько штук нужно забрать — система подберёт склады.
        </p>
        <form onSubmit={onTake}>
          {takeBatch.map((row, i) => (
            <div key={i} className="line-row">
              <label>
                Номер товара
                <input
                  type="number"
                  value={row.productId || ''}
                  onChange={(e) =>
                    setTakeBatch((rows) =>
                      rows.map((r, j) =>
                        j === i ? { ...r, productId: Number(e.target.value) } : r,
                      ),
                    )
                  }
                />
              </label>
              <label>
                Количество
                <input
                  type="number"
                  min={1}
                  value={row.quantity}
                  onChange={(e) =>
                    setTakeBatch((rows) =>
                      rows.map((r, j) =>
                        j === i ? { ...r, quantity: Number(e.target.value) } : r,
                      ),
                    )
                  }
                />
              </label>
              <button
                type="button"
                className="btn-secondary btn-small"
                onClick={() =>
                  setTakeBatch((rows) => rows.filter((_, j) => j !== i))
                }
                disabled={takeBatch.length <= 1}
              >
                Убрать
              </button>
            </div>
          ))}
          <div className="form-actions">
            <button
              type="button"
              className="btn-secondary"
              onClick={() => setTakeBatch((b) => [...b, { ...pq(), quantity: 1 }])}
            >
              Ещё строка
            </button>
            <button type="submit">Списать</button>
          </div>
        </form>
      </section>

      <section className="panel">
        <h2>Корректировка остатка на складе</h2>
        <p className="panel-lead">
          Уже заведённые позиции: укажите склад, товар и на сколько штук изменить
          счётчик (можно минусом).
        </p>
        <form onSubmit={onDelta}>
          {deltaBatch.map((row, i) => (
            <div key={i} className="line-row line-row--four">
              <label>
                Склад
                <input
                  type="number"
                  value={row.inventoryId || ''}
                  onChange={(e) =>
                    setDeltaBatch((rows) =>
                      rows.map((r, j) =>
                        j === i
                          ? { ...r, inventoryId: Number(e.target.value) }
                          : r,
                      ),
                    )
                  }
                />
              </label>
              <label>
                Товар
                <input
                  type="number"
                  value={row.productId || ''}
                  onChange={(e) =>
                    setDeltaBatch((rows) =>
                      rows.map((r, j) =>
                        j === i
                          ? { ...r, productId: Number(e.target.value) }
                          : r,
                      ),
                    )
                  }
                />
              </label>
              <label>
                Изменение, шт.
                <input
                  type="number"
                  value={row.delta}
                  onChange={(e) =>
                    setDeltaBatch((rows) =>
                      rows.map((r, j) =>
                        j === i ? { ...r, delta: Number(e.target.value) } : r,
                      ),
                    )
                  }
                />
              </label>
              <button
                type="button"
                className="btn-secondary btn-small"
                onClick={() =>
                  setDeltaBatch((rows) => rows.filter((_, j) => j !== i))
                }
                disabled={deltaBatch.length <= 1}
              >
                Убрать
              </button>
            </div>
          ))}
          <div className="form-actions">
            <button
              type="button"
              className="btn-secondary"
              onClick={() => setDeltaBatch((b) => [...b, uq()])}
            >
              Ещё строка
            </button>
            <button type="submit">Применить</button>
          </div>
        </form>
      </section>

      <section className="panel">
        <h2>Где лежит товар</h2>
        <p className="panel-lead">
          По номеру из каталога покажем склады и сколько штук на каждом.
        </p>
        <form className="form-grid" onSubmit={lookup}>
          <label>
            Номер товара
            <input
              type="number"
              value={lookupProductId}
              onChange={(e) => setLookupProductId(e.target.value)}
            />
          </label>
          <button type="submit">Найти</button>
        </form>
        {lookupResult && lookupResult.length > 0 && (
          <div className="table-wrap lookup-table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Склад</th>
                  <th>Товар</th>
                  <th>Штук</th>
                </tr>
              </thead>
              <tbody>
                {lookupResult.map((row) => (
                  <tr key={row.id}>
                    <td>{row.inventoryId}</td>
                    <td>{row.productId}</td>
                    <td>{row.quantity}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        {lookupResult && lookupResult.length === 0 && (
          <p className="empty">Такого товара на складах не найдено</p>
        )}
      </section>
    </div>
  )
}
