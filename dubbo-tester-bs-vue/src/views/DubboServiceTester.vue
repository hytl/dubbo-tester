<template>
  <div class="main-container" style="width: 100%">
    <el-form class="dubbo-tester" ref="FormRef" :model="form" label-width="auto">
      <!-- Header -->
      <el-header class="app-header">
        <div class="header-content">
          <h1>Dubbo Service Tester</h1>
        </div>
      </el-header>
      <!-- Register Configuration -->
      <el-card class="registry-config-card" style="margin-bottom: 2px">
        <template #header>
          <div class="card-header">
            <span>Register Configuration</span>
          </div>
        </template>
        <div class="register-config-container">
          <el-form label-width="auto" label-position="left">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="Registry Type">
                  <el-select v-model="form.registryType">
                    <el-option
                      v-for="item in registryOptions"
                      :key="item"
                      :label="item"
                      :value="item"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="18">
                <el-form-item label="Registry Address">
                  <el-input
                    v-model="form.registryAddress"
                    placeholder="e.g. nacos://127.0.0.1:8848"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </el-card>

      <!-- Service Configuration -->
      <el-card class="service-config-card">
        <template #header>
          <div class="card-header">
            <span>Service Configuration</span>
          </div>
        </template>
        <div class="service-config-container">
          <el-form label-width="auto" label-position="left">
            <el-form-item label="Service Interface">
              <el-input v-model="form.serviceInterface" />
            </el-form-item>
            <el-form-item label="Method Name">
              <el-input v-model="form.methodName" />
            </el-form-item>

            <el-form-item class="flex parameter-type-container" label="Parameter Types">
              <el-tag
                v-for="(parameterType, index) in form.parameterTypes"
                :key="index"
                closable
                :disable-transitions="false"
                @close="handleParameterTypeTagClose(parameterType, index)"
              >
                {{ parameterType }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="TagInputRef"
                v-model="tagInputValue"
                class="w-20"
                size="small"
                @keyup.enter="handleTagInputConfirm"
                @blur="handleTagInputConfirm"
              />
              <el-button v-else class="button-new-tag" size="small" @click="showTagInput">
                + New
              </el-button>
            </el-form-item>

            <el-form-item
              v-for="(parameter, index) in form.parameterTypes"
              :key="index"
              :label="`Parameter ${index + 1}(${form.parameterTypes[index]})`"
            >
              <el-input v-model="form.parameters[index]"> {{ parameter }}</el-input>
            </el-form-item>
          </el-form>
        </div>
      </el-card>

      <el-form-item>
        <el-button
          type="primary"
          v-if="invokeContext.submitStatus === 'NOT_SUBMIT'"
          @click="onSubmit"
          style="width: 100%"
          >Invoke Service
        </el-button>
        <el-button
          type="primary"
          v-if="invokeContext.submitStatus === 'LOADING'"
          style="width: 100%"
          loading
        >
          Loading...
        </el-button>
      </el-form-item>
    </el-form>

    <!-- Invoke Result Area -->
    <el-input
      v-model="invokeContext.invokeResult"
      type="textarea"
      :input-style="invokeContext.invokeResultAreaStyle"
      :rows="Number('15')"
      placeholder="Please invoke service"
      readonly
      clearable
    >
    </el-input>
  </div>
</template>

<script lang="ts" setup>
import { nextTick, reactive, ref } from 'vue'
import type { InputInstance } from 'element-plus'
import axios from 'axios'

const registryOptions = ref(['Nacos', 'Zookeeper'])

const form = ref({
  registryType: 'Nacos',
  registryAddress: 'nacos://127.0.0.1:8848',

  serviceInterface: 'org.apache.dubbo.samples.quickstart.dubbo.api.DemoService',
  methodName: 'sayHello',
  parameterTypes: ['java.lang.String'],
  parameters: ['world'],
})
const invokeContext = reactive({
  submitStatus: 'NOT_SUBMIT',
  invokeResultAreaStyle: {
    color: 'black',
  },
  invokeResult: '',
})

const tagInputValue = ref('')
const tagInputVisible = ref(false)
const FormRef = ref<InputInstance>()
const TagInputRef = ref<InputInstance>()

const handleParameterTypeTagClose = (tag: string, index: number) => {
  form.value.parameterTypes.splice(index, 1)
  form.value.parameters.splice(index, 1)
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    TagInputRef.value!.input!.focus()
  })
}

const handleTagInputConfirm = () => {
  if (tagInputValue.value) {
    form.value.parameterTypes.push(tagInputValue.value)
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
  form.value.parameters.push('')
}

const onSubmit = async () => {
  invokeContext.submitStatus = 'LOADING'
  try {
    const request = {
      registryType: form.value.registryType.toUpperCase(),
      registryAddress: form.value.registryAddress.trim(),

      serviceInterface: form.value.serviceInterface.trim(),
      methodName: form.value.methodName.trim(),
      parameterTypes: form.value.parameterTypes.map((item) => item?.trim?.()),
      parameters: form.value.parameters.map((item) => item?.trim?.()),
    }
    console.log('dubbo test request:', request)
    const response = await axios.post(
      'http://127.0.0.1:8080/dubbo-tester/v1/dubbo/service/test',
      request,
      {
        timeout: 30000,
      },
    )
    if (response.data?.code !== 200) {
      invokeContext.invokeResultAreaStyle = {
        color: 'red',
      }
      invokeContext.invokeResult = response.data.msg
    } else if (response.data?.data?.exception) {
      invokeContext.invokeResultAreaStyle = {
        color: 'red',
      }
      invokeContext.invokeResult = response.data.data.exception
    } else if (response.data?.data?.result) {
      invokeContext.invokeResultAreaStyle = {
        color: 'black',
      }
      invokeContext.invokeResult = response.data.data.result
    }
  } catch (error) {
    console.error('request error:', error)
    invokeContext.invokeResultAreaStyle = {
      color: 'red',
    }
    const errorMessage =
      error instanceof Error
        ? error.message
        : typeof error === 'string'
          ? error
          : JSON.stringify(error)
    invokeContext.invokeResult = errorMessage
  } finally {
    invokeContext.submitStatus = 'NOT_SUBMIT'
  }
}
</script>

<style>
.dubbo-tester {
  background: #f5f7fa;

  .app-header {
    background: #2c3e50;
    height: 60px !important;

    .header-content {
      display: flex;
      align-items: center;
      height: 100%;

      h1 {
        color: white;
        margin: 0;
        font-size: 24px;
      }
    }
  }

  .card-header {
    font-weight: bold;
  }

  .el-form-item__content {
    gap: 0.5rem;
  }

  .flex {
    display: flex;
  }
}
</style>
