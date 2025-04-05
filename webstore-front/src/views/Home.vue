<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="logo">WebStore</div>
      <div class="nav-links">
        <a v-for="category in categories" 
           :key="category.value" 
           @click="selectCategory(category)"
           :class="{ active: currentCategory === category.value }">
          {{ category.label }}
        </a>
      </div>
      <div class="user-actions">
        <template v-if="!isLoggedIn">
          <button @click="showLoginModal = true">登录</button>
          <button @click="showRegisterModal = true">注册</button>
        </template>
        <template v-else>
          <span>欢迎, {{ username }}</span>
          <button @click="handleLogout">退出</button>
        </template>
      </div>
    </nav>

    <!-- 商品展示区 -->
    <div class="products-container">
      <div class="products-grid">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.name">
          <h3>{{ product.name }}</h3>
          <p class="price">¥{{ product.price }}</p>
        </div>
      </div>
      
      <!-- 分页组件 -->
      <div class="pagination">
        <button 
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)">
          上一页
        </button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)">
          下一页
        </button>
      </div>
    </div>

    <!-- 登录模态框 -->
    <div v-if="showLoginModal" class="modal">
      <div class="modal-content">
        <h2>登录</h2>
        <form @submit.prevent="handleLogin">
          <input v-model="loginForm.account" type="text" placeholder="账号" required>
          <input v-model="loginForm.password" type="password" placeholder="密码" required>
          <select v-model="loginForm.role">
            <option value="CUSTOMER">顾客</option>
            <option value="SELLER">销售人员</option>
            <option value="ADMIN">管理员</option>
          </select>
          <button type="submit">登录</button>
          <button type="button" @click="showLoginModal = false">取消</button>
        </form>
      </div>
    </div>

    <!-- 注册模态框 -->
    <div v-if="showRegisterModal" class="modal">
      <div class="modal-content">
        <h2>注册</h2>
        <form @submit.prevent="handleRegister">
          <input 
            v-model="registerForm.account" 
            type="text" 
            placeholder="手机号或邮箱" 
            required>
          <input 
            v-model="registerForm.username" 
            type="text" 
            placeholder="用户名" 
            required>
          <input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="密码" 
            required>
          <input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="确认密码" 
            required>
          <select v-model="registerForm.role">
            <option value="CUSTOMER">顾客</option>
            <option value="SELLER">销售人员</option>
          </select>
          <button type="submit">注册</button>
          <button type="button" @click="showRegisterModal = false">取消</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Home',
  data() {
    return {
      products: [],
      currentPage: 1,
      pageSize: 12,
      totalPages: 1,
      categories: [
        { value: 'ALL', label: '全部' },
        { value: 'FASHION', label: '服饰' },
        { value: 'HOME', label: '家居' },
        { value: 'FOOD', label: '食品' },
        { value: 'BEAUTY', label: '美妆' },
        { value: 'BABY', label: '母婴' },
        { value: 'SPORTS', label: '运动' },
        { value: 'BOOKS', label: '书籍' },
        { value: 'DIGITAL', label: '数码' }
      ],
      currentCategory: 'ALL',
      isLoggedIn: false,
      username: '',
      showLoginModal: false,
      showRegisterModal: false,
      loginForm: {
        account: '',
        password: '',
        role: 'CUSTOMER'
      },
      registerForm: {
        account: '',
        username: '',
        password: '',
        confirmPassword: '',
        role: 'CUSTOMER'
      }
    }
  },
  created() {
    this.fetchProducts();
    // 检查是否已登录
    const token = localStorage.getItem('token');
    if (token) {
      this.isLoggedIn = true;
      // 解析token获取用户信息
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.username = payload.sub;
    }
  },
  methods: {
    async fetchProducts() {
      try {
        const url = this.currentCategory === 'ALL' 
          ? '/api/product/products'
          : '/api/product/products/category';
        
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize
        };
        
        if (this.currentCategory !== 'ALL') {
          params.category = this.currentCategory;
        }

        const response = await axios.get(url, { params });
        this.products = response.data.records;
        this.totalPages = response.data.pages;
      } catch (error) {
        console.error('获取商品列表失败:', error);
      }
    },
    selectCategory(category) {
      this.currentCategory = category.value;
      this.currentPage = 1;
      this.fetchProducts();
    },
    changePage(page) {
      this.currentPage = page;
      this.fetchProducts();
    },
    async handleLogin() {
      try {
        const response = await axios.post('/api/user/login', this.loginForm);
        const token = response.data.data;
        localStorage.setItem('token', token);
        this.isLoggedIn = true;
        this.username = this.loginForm.account;
        this.showLoginModal = false;
      } catch (error) {
        console.error('登录失败:', error);
      }
    },
    async handleRegister() {
      // 表单验证
      if (!this.registerForm.account) {
        alert('请输入手机号或邮箱');
        return;
      }
      if (!this.registerForm.username) {
        alert('请输入用户名');
        return;
      }
      if (this.registerForm.username.length < 4 || this.registerForm.username.length > 20) {
        alert('用户名长度必须在4-20位之间');
        return;
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        alert('两次输入的密码不一致');
        return;
      }
      if (this.registerForm.password.length < 6 || this.registerForm.password.length > 20) {
        alert('密码长度必须在6-20位之间');
        return;
      }

      // 验证账号格式（手机号或邮箱）
      const phoneRegex = /^1[3-9]\d{9}$/;
      const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
      if (!phoneRegex.test(this.registerForm.account) && !emailRegex.test(this.registerForm.account)) {
        alert('请输入有效的手机号或邮箱');
        return;
      }

      try {
        await axios.post('/api/user/register', {
          account: this.registerForm.account,
          username: this.registerForm.username,
          password: this.registerForm.password,
          role: this.registerForm.role
        });
        alert('注册成功，请登录');
        this.showRegisterModal = false;
        this.showLoginModal = true;
      } catch (error) {
        if (error.response) {
          alert('注册失败: ' + (error.response.data.message || '未知错误'));
        } else {
          alert('注册失败: ' + error.message);
        }
        console.error('注册失败:', error);
      }
    },
    handleLogout() {
      localStorage.removeItem('token');
      this.isLoggedIn = false;
      this.username = '';
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

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

/* 为固定导航栏添加内容区域的padding */
.products-container {
  padding: 2rem;
  margin-top: 4rem; /* 为固定导航栏留出空间 */
}

/* 添加响应式设计 */
@media screen and (max-width: 768px) {
  .navbar {
    padding: 1rem;
    flex-wrap: wrap;
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

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.product-card h3 {
  margin: 1rem 0;
}

.price {
  color: #f5222d;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
}

.modal-content input,
.modal-content select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100%;
  margin-bottom: 0.5rem;
}

.modal-content form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.modal-content button {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  background-color: #1890ff;
  color: white;
  cursor: pointer;
  width: 100%;
}

.modal-content button[type="button"] {
  background-color: #f5f5f5;
  color: #333;
}
</style> 