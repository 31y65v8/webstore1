<template>
  <nav class="navbar">
    <div class="logo">WebStore</div>
    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchQuery" 
        @keyup.enter="handleSearch"
        placeholder="搜索商品..."
      >
      <button @click="handleSearch">搜索</button>
    </div>
    <div class="nav-links">
      <a v-for="category in categories" 
         :key="category.value" 
         @click="handleCategorySelect(category)"
         :class="{ active: currentCategory === category.value }">
        {{ category.label }}
      </a>
    </div>
    <div class="user-actions">
      <template v-if="!isLoggedIn">
        <button @click="$emit('show-login')">登录</button>
        <button @click="$emit('show-register')">注册</button>
      </template>
      <template v-else>
        <span>欢迎, {{ username }}</span>
        <button @click="handleLogout">退出</button>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const router = useRouter()
const emit = defineEmits(['category-change', 'show-login', 'show-register'])

const { isLoggedIn, username, logout } = useAuth()

const searchQuery = ref('')
const currentCategory = ref('ALL')
const categories = [
  { value: 'ALL', label: '全部' },
  { value: 'FASHION', label: '服饰' },
  { value: 'HOME', label: '家居' },
  { value: 'FOOD', label: '食品' },
  { value: 'BEAUTY', label: '美妆' },
  { value: 'BABY', label: '母婴' },
  { value: 'SPORTS', label: '运动' },
  { value: 'BOOKS', label: '书籍' },
  { value: 'DIGITAL', label: '数码' }
]

const handleSearch = () => {
  if (!searchQuery.value.trim()) return
  router.push({
    path: '/search',
    query: { q: searchQuery.value }
  })
}

const handleCategorySelect = (category) => {
  currentCategory.value = category.value
  emit('category-change', category.value)
}

const handleLogout = () => {
  logout()
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  white-space: nowrap;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 1rem;
}

.search-bar input {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
  font-size: 0.9rem;
}

.search-bar button {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-bar button:hover {
  background-color: #40a9ff;
}

.nav-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
  margin: 0 2rem;
}

.nav-links a {
  cursor: pointer;
  color: #333;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-links a:hover {
  background-color: #f5f5f5;
}

.nav-links a.active {
  color: #1890ff;
  background-color: #e6f7ff;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  white-space: nowrap;
}

.user-actions button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #1890ff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.user-actions button:hover {
  background-color: #40a9ff;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .navbar {
    padding: 1rem;
    flex-wrap: wrap;
  }

  .search-bar {
    order: 2;
    width: 100%;
    margin: 1rem 0;
  }

  .search-bar input {
    width: 100%;
  }

  .nav-links {
    order: 3;
    width: 100%;
    margin: 1rem 0;
    justify-content: flex-start;
  }

  .user-actions {
    margin-left: auto;
  }
}

@media screen and (max-width: 480px) {
  .nav-links {
    overflow-x: auto;
    padding-bottom: 0.5rem;
  }

  .nav-links a {
    padding: 0.5rem;
  }

  .user-actions {
    flex-direction: column;
    align-items: flex-end;
  }
}
</style>
