<script setup>
import AlunoDashboard from './components/AlunoDashboard.vue'
</script>

<template>
  <AlunoDashboard />
</template>

<style>
:root {
  --golden-sand: #D0D480;
  --dark-teal: #104F58;
  --golden-apricot: #E08F40;
  --wine-plum: #603338;
  --pale-slate: #ABB2C5;
  --burnt-peach: #EC8366;
  --lilac: #BD97BC;
  --eggshell: #F1E8D7;

  /* mapeamento semantico usado no componente */
  --tinta: var(--eggshell);          /* fundo da pagina — bege */
  --tinta-clara: #FBF6EC;            /* fundo dos cartoes — bege mais claro */
  --pergaminho: var(--wine-plum);    /* texto principal — vinho/roxo escuro (legivel no bege) */
  --texto-suave: #8A6E86;            /* texto secundario — roxo acinzentado */
  --ouro: var(--golden-apricot);     /* acao principal — laranja */
  --ouro-claro: var(--lilac);        /* marca / destaques — lilas */
  --detalhe-verde: var(--dark-teal); /* verde/teal apenas em detalhes */
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