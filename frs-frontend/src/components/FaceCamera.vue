<template>
  <div class="face-camera">
    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="!capturedImage" class="camera-container">
      <video ref="videoRef" autoplay playsinline muted></video>
      <canvas ref="canvasRef"></canvas>
      <div class="camera-overlay">
        <div class="face-guide"></div>
      </div>
    </div>

    <div v-else class="preview-container">
      <img :src="capturedImage" alt="captured face" />
    </div>

    <div class="camera-actions">
      <template v-if="!capturedImage">
        <button v-if="!streaming" class="btn btn-primary" @click="startCamera" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? '启动中...' : '开启摄像头' }}
        </button>
        <button v-else class="capture-btn" @click="capture" title="拍照"></button>
      </template>
      <template v-else>
        <button class="btn btn-outline" @click="retake">重新拍照</button>
        <button class="btn btn-primary" @click="confirm" :disabled="confirming">
          <span v-if="confirming" class="loading-spinner"></span>
          {{ confirming ? '确认中...' : '确认使用' }}
        </button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'

const emit = defineEmits<{
  capture: [base64: string]
}>()

const videoRef = ref<HTMLVideoElement | null>(null)
const canvasRef = ref<HTMLCanvasElement | null>(null)
const streaming = ref(false)
const capturedImage = ref('')
const loading = ref(false)
const confirming = ref(false)
const error = ref('')
let mediaStream: MediaStream | null = null

async function startCamera() {
  error.value = ''
  loading.value = true
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'user', width: { ideal: 640 }, height: { ideal: 480 } }
    })
    if (videoRef.value) {
      videoRef.value.srcObject = mediaStream
      await videoRef.value.play()
      streaming.value = true
    }
  } catch (e: any) {
    error.value = '无法访问摄像头，请确保已授权摄像头权限'
    console.error('Camera error:', e)
  } finally {
    loading.value = false
  }
}

function capture() {
  if (!videoRef.value || !canvasRef.value) return
  const video = videoRef.value
  const canvas = canvasRef.value
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  ctx.translate(canvas.width, 0)
  ctx.scale(-1, 1)
  ctx.drawImage(video, 0, 0)
  capturedImage.value = canvas.toDataURL('image/jpeg', 0.9)
  stopCamera()
}

function retake() {
  capturedImage.value = ''
  startCamera()
}

function confirm() {
  if (!capturedImage.value) return
  confirming.value = true
  const base64 = capturedImage.value.split(',')[1]
  emit('capture', base64)
  setTimeout(() => {
    confirming.value = false
  }, 1000)
}

function stopCamera() {
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop())
    mediaStream = null
  }
  streaming.value = false
}

onUnmounted(() => {
  stopCamera()
})
</script>