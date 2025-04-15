import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SearchView from '@/views/SearchView.vue'
import SellerView from '@/views/SellerView.vue'
import ProductDetail from '@/views/ProductDetail.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/search',
      name: 'search',
      component: SearchView
    },
    {
      path: '/seller',
      name: 'seller',
      component: SellerView,
      meta: {
        requiresAuth: true,
        roles: ['SELLER']
      }
    },
    {
      path: '/product/:id',
      name: 'ProductDetail',
      component: ProductDetail
    }
  ]
})

// 导航守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ name: 'home' })
      return
    }

    // 如果需要特定角色
    if (to.meta.roles) {
      const userRole = localStorage.getItem('userRole')
      if (!to.meta.roles.includes(userRole)) {
        next({ name: 'home' })
        return
      }
    }
  }
  next()
})

export default router 