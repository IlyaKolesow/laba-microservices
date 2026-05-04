function parseBody(text: string): unknown {
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch {
    return text
  }
}

export function getErrorMessage(body: unknown, fallback: string): string {
  if (body && typeof body === 'object' && 'message' in body) {
    const m = (body as { message?: unknown }).message
    if (typeof m === 'string') return m
  }
  if (typeof body === 'string') return body
  return fallback
}

export async function request<T>(
  path: string,
  init?: RequestInit,
): Promise<T> {
  const res = await fetch(path, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  })
  const text = await res.text()
  const data = parseBody(text) as T
  if (!res.ok) {
    throw new Error(
      getErrorMessage(data, res.statusText || `HTTP ${res.status}`),
    )
  }
  return data
}
