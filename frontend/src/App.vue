<script setup>
import AlunoDashboard from './components/AlunoDashboard.vue'
</script>

<template>
  <AlunoDashboard />
</template>

<style>
:root {
  /* paleta base */
  --golden-sand: #D0D480;
  --dark-teal: #104F58;
  --golden-apricot: #E08F40;
  --wine-plum: #603338;
  --pale-slate: #ABB2C5;
  --burnt-peach: #EC8366;
  --lilac: #BD97BC;
  --eggshell: #F1E8D7;

  /* mapeamento usado no componente */
  --tinta: #0B383E;
  --tinta-clara: #104F58;
  --pergaminho: #F1E8D7;
  --texto-suave: #ABB2C5;
  --ouro: #E08F40;
  --ouro-claro: #D0D480;
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