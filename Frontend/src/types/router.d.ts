export {}

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    hideTabs?: boolean
    hideChrome?: boolean
  }
}
