<template>
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" v-if="Component" />
    </transition>
  </router-view>
</template>

<script setup>
</script>