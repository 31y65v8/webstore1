import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SearchView from '@/views/SearchView.vue'
import SellerView from '@/views/SellerView.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import CustomerView from '@/views/CustomerView.vue'
import CartView from '@/views/CartView.vue'
import MyOrdersView from '@/views/MyOrdersView.vue'
import SellerList from '@/views/SellerList.vue'

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
      },
      children: [
        { 
          path: 'browse-history',
          component: () => import('@/components/seller/ViewBrowseHistory.vue')
        },
      ]
    },
    {
      path: '/product/:id',
      name: 'ProductDetail',
      component: ProductDetail
    },
    {
      path: '/customer',
      component: CustomerView
    },
    {
      path: '/cart',
      component: CartView
    },
    {
      path: '/orders',
      component: MyOrdersView,
      meta: {
        requiresAuth: true,
        roles: ['CUSTOMER']
      }
    },
    {
      path: '/seller/paid-orderitem',
      name: 'PaidOrderItem',
      component: () => import('@/views/PaidOrderItem.vue'),
      meta: { requiresAuth: true, roles: ['SELLER'] }
    },
    {
      path: '/admin',
      name: 'Admin',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: '/admin/sellers',
      name: 'SellerManagement',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: '/admin/report/sellers',
      name: 'SellerList',
      component: () => import('../views/SellerList.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: '/admin/report/categories',
      name: 'CategoriesSalesReport',
      component: () => import('../views/CategoriesSalesReport.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: '/admin/report/products',
      name: 'ProductSalesReport',
      component: () => import('../views/ProductSalesTrend.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] }
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