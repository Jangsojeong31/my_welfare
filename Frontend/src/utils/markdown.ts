function escapeHtml(value: string): string {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

export function rewriteWelfareDetailLinks(
  markdown: string,
  recommendations: Array<{ id: string | null; servId: string; servNm: string; detailLink: string | null }>,
): string {
  const withId = recommendations.filter((item) => item.id)
  if (!withId.length) {
    return markdown
  }

  let sequentialIndex = 0

  return markdown.replace(/\[([^\]]+)\]\(([^)\s]+)\)/g, (match, label: string, href: string, offset: number) => {
    const byLink = withId.find(
      (item) =>
        item.detailLink &&
        (href === item.detailLink || href.includes(item.detailLink) || item.detailLink.includes(href)),
    )
    if (byLink?.id) {
      return `[${label}](/welfare/${byLink.id})`
    }

    const byServId = withId.find((item) => item.servId && href.includes(item.servId))
    if (byServId?.id) {
      return `[${label}](/welfare/${byServId.id})`
    }

    if (!/자세한\s*내용/.test(label)) {
      return match
    }

    const before = markdown.slice(Math.max(0, offset - 500), offset)
    const byName = [...withId].reverse().find((item) => item.servNm && before.includes(item.servNm))
    if (byName?.id) {
      return `[${label}](/welfare/${byName.id})`
    }

    const fallback = withId[sequentialIndex]
    if (fallback?.id) {
      sequentialIndex += 1
      return `[${label}](/welfare/${fallback.id})`
    }

    return match
  })
}

export function renderSimpleMarkdown(markdown: string): string {
  const escaped = escapeHtml(markdown)
  const withInline = escaped
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\[([^\]]+)\]\(([^)\s]+)\)/g, (_, text: string, href: string) => {
      if (href.startsWith('/')) {
        return `<a href="${href}">${text}</a>`
      }
      return `<a href="${href}" target="_blank" rel="noopener noreferrer">${text}</a>`
    })

  return withInline
    .split(/\n{2,}/)
    .map((paragraph) => `<p>${paragraph.replace(/\n/g, '<br>')}</p>`)
    .join('')
}
