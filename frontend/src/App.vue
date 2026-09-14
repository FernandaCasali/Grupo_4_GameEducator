<script setup>
import AlunoDashboard from './components/AlunoDashboard.vue'
</script>

<template>
  <AlunoDashboard />
</template>

<style>
:root {
  --tinta: #16213a;
  --tinta-clara: #223258;
  --pergaminho: #f7f3ea;
  --ouro: #c9962f;
  --ouro-claro: #e4b959;
  --texto-suave: #a9b4cc;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  background: var(--tinta);
  color: var(--pergaminho);
  font-family: 'Inter', system-ui, sans-serif;
}
</style>