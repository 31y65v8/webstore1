<template>
  <div class="product-card" @click="$emit('click')">
    <div class="product-image">
      <img :src="product.imgurl" :alt="product.name">
    </div>
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>
      <div class="product-footer">
        <p class="price">¥{{ formatPrice(product.price) }}</p>
        <span class="sales">销量: {{ product.sales || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineEmits(['product-click'])

const props = defineProps({
  product: {
    type: Object,
    required: true,
    validator: (value) => {
      return value.id && value.name && value.price
    }
  }
})

const formatPrice = (price) => {
  return Number(price).toFixed(2)
}
</script>

<style scoped>
.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 1rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-description {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 1rem;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f5222d;
  font-weight: bold;
  font-size: 1.2rem;
  margin: 0;
}

.sales {
  color: #999;
  font-size: 0.9rem;
}

@media screen and (max-width: 768px) {
  .product-image {
    height: 180px;
  }

  .product-name {
    font-size: 1rem;
  }

  .product-description {
    font-size: 0.8rem;
  }

  .price {
    font-size: 1.1rem;
  }
}
</style>
