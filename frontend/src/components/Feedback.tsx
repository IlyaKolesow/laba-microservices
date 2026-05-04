export function Feedback({
  message,
  tone,
}: {
  message: string | null
  tone: 'error' | 'success' | 'info'
}) {
  if (!message) return null
  return <div role="status" className={`feedback feedback--${tone}`}>{message}</div>
}
