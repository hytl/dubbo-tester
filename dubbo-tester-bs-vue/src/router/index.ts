import { createRouter, createWebHistory } from 'vue-router'
import DubboServiceTester from '../views/DubboServiceTester.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: DubboServiceTester,
    },
  ],
})

export default router
