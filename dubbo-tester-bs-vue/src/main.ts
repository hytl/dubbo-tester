import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// import App from './App.vue'
import DubboServiceTester from './views/DubboServiceTester.vue'
import router from './router'

const app = createApp(DubboServiceTester)

app.use(ElementPlus)
app.use(createPinia())
app.use(router)

app.mount('#app')
