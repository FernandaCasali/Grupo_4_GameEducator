<script setup>
import { ref, computed } from 'vue'

const API_URL = 'http://localhost:8080/api/alunos'

const alunoId = ref(null)
const aluno = ref(null)
const carregando = ref(false)
const erro = ref('')

const nomeNovoAluno = ref('')
const planoNome = ref('Basico')
const planoLimite = ref(10)

const buscarId = ref(null)

const nomeCurso = ref('')
const media = ref('')

const quantidadeMoedas = ref(1)
const motivoMoedas = ref('Participacao no forum')

const quantidadeCripto = ref(1)
const taxaCambio = ref(0.15)
const resultadoCripto = ref(null)

const isPremium = computed(() => aluno.value?.premium === true)

function voltarParaInicio() {
  aluno.value = null
  alunoId.value = null
  erro.value = ''
  resultadoCripto.value = null
}

async function criarAluno() {
  if (!nomeNovoAluno.value.trim()) return
  carregando.value = true
  erro.value = ''
  try {
    const resp = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nome: nomeNovoAluno.value,
        planoNome: planoNome.value,
        planoLimiteCursos: Number(planoLimite.value)
      })
    })
    if (!resp.ok) throw new Error('Nao foi possivel criar o aluno.')
    aluno.value = await resp.json()
    alunoId.value = aluno.value.id
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

async function buscarAluno() {
  if (!buscarId.value) return
  carregando.value = true
  erro.value = ''
  try {
    const resp = await fetch(`${API_URL}/${buscarId.value}`)
    if (!resp.ok) throw new Error('Aluno nao encontrado.')
    aluno.value = await resp.json()
    alunoId.value = aluno.value.id
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

async function concluirCurso() {
  if (!alunoId.value || !nomeCurso.value.trim() || media.value === '') return
  await chamarAcao(`${API_URL}/${alunoId.value}/concluir-curso`, {
    nomeCurso: nomeCurso.value,
    media: Number(media.value)
  })
  nomeCurso.value = ''
  media.value = ''
}

async function creditarMoedas() {
  if (!alunoId.value) return
  await chamarAcao(`${API_URL}/${alunoId.value}/moedas`, {
    quantidade: Number(quantidadeMoedas.value),
    motivo: motivoMoedas.value
  })
}

async function converterCripto() {
  if (!alunoId.value) return
  carregando.value = true
  erro.value = ''
  resultadoCripto.value = null
  try {
    const resp = await fetch(`${API_URL}/${alunoId.value}/converter-cripto`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        quantidadeMoedas: Number(quantidadeCripto.value),
        taxaCambio: Number(taxaCambio.value)
      })
    })
    const dados = await resp.json()
    if (!resp.ok) throw new Error(dados.erro || 'Erro na conversao.')
    aluno.value = dados
    resultadoCripto.value = (quantidadeCripto.value * taxaCambio.value).toFixed(4)
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

async function chamarAcao(url, corpo) {
  carregando.value = true
  erro.value = ''
  try {
    const resp = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(corpo)
    })
    const dados = await resp.json()
    if (!resp.ok) throw new Error(dados.erro || 'Ocorreu um erro.')
    aluno.value = dados
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="pagina">
    <header class="topo">
      <span class="marca">Trilha</span>
      <span class="subtitulo">educacao continuada gamificada</span>
    </header>

    <main class="conteudo">
      <section v-if="!aluno" class="cartao entrada">
        <h1>Comece sua trilha</h1>
        <p class="descricao">Cadastre um aluno para acompanhar cursos, moedas e o avanco de nivel.</p>
        <div class="campo">
          <label>Nome do aluno</label>
          <input v-model="nomeNovoAluno" placeholder="ex: Carolina" @keyup.enter="criarAluno" />
        </div>
        <div class="linha">
          <div class="campo">
            <label>Plano</label>
            <input v-model="planoNome" placeholder="Basico" />
          </div>
          <div class="campo">
            <label>Limite de cursos</label>
            <input v-model="planoLimite" type="number" min="0" />
          </div>
        </div>
        <button class="acao" :disabled="carregando" @click="criarAluno">Criar aluno</button>

        <div class="separador">ou</div>

        <div class="campo">
          <label>Buscar aluno existente pelo ID</label>
          <div class="formulario">
            <input v-model.number="buscarId" type="number" min="1" placeholder="ID do aluno" @keyup.enter="buscarAluno" />
            <button class="acao" :disabled="carregando" @click="buscarAluno">Buscar</button>
          </div>
        </div>
      </section>

      <template v-else>
        <button class="voltar" @click="voltarParaInicio">← Voltar</button>

        <section class="cartao perfil">
          <div class="identidade">
            <h1>{{ aluno.nome }}</h1>
            <span class="selo" :class="{ premium: isPremium }">
              {{ isPremium ? 'Premium' : aluno.planoNome || 'Sem plano' }}
            </span>
          </div>
          <dl class="metricas">
            <div>
              <dt>Cursos concluidos</dt>
              <dd>{{ aluno.totalCursos }}</dd>
            </div>
            <div>
              <dt>Moedas</dt>
              <dd class="moeda">{{ aluno.saldoMoedas }}</dd>
            </div>
          </dl>
        </section>

        <section class="cartao">
          <h2>Cursos desbloqueados como recompensa</h2>
          <ol v-if="aluno.cursosDesbloqueados.length" class="trilha">
            <li v-for="(c, i) in aluno.cursosDesbloqueados" :key="i">{{ c }}</li>
          </ol>
          <p v-else class="vazio">Nenhum curso desbloqueado ainda. Conclua um curso com media acima de 7,0 para desbloquear 3 cursos bonus.</p>

          <form class="formulario" @submit.prevent="concluirCurso">
            <input v-model="nomeCurso" placeholder="Nome do curso concluido" />
            <input v-model="media" type="number" step="0.1" min="0" max="10" placeholder="Media final" />
            <button class="acao" :disabled="carregando">Concluir curso</button>
          </form>
        </section>

        <section class="cartao">
          <h2>Engajamento e moedas</h2>
          <form class="formulario" @submit.prevent="creditarMoedas">
            <input v-model.number="quantidadeMoedas" type="number" min="1" />
            <input v-model="motivoMoedas" placeholder="Motivo (ex: post no forum)" />
            <button class="acao" :disabled="carregando">Creditar moedas</button>
          </form>
          <ul v-if="aluno.historicoMoedas.length" class="historico">
            <li v-for="(h, i) in aluno.historicoMoedas" :key="i">{{ h }}</li>
          </ul>
        </section>

        <section v-if="isPremium" class="cartao">
          <h2>Converter moedas em criptomoeda</h2>
          <form class="formulario" @submit.prevent="converterCripto">
            <input v-model.number="quantidadeCripto" type="number" min="1" :max="aluno.saldoMoedas" placeholder="Qtd moedas" />
            <input v-model.number="taxaCambio" type="number" step="0.01" min="0.01" placeholder="Taxa de cambio" />
            <button class="acao" :disabled="carregando">Converter</button>
          </form>
          <p v-if="resultadoCripto !== null" class="notificacoes">
            Convertido: {{ resultadoCripto }} cripto
          </p>
        </section>

        <section v-else class="cartao">
          <h2>Conversao para criptomoeda</h2>
          <p class="vazio">
            Disponivel apenas para alunos Premium (12+ cursos concluidos).
            Voce tem {{ aluno.totalCursos }} cursos.
          </p>
        </section>

        <p v-if="aluno.notificacoes.length" class="notificacoes">
          {{ Array.from(aluno.notificacoes).join(', ') }}
        </p>
      </template>

      <p v-if="erro" class="mensagem-erro">{{ erro }}</p>
    </main>
  </div>
</template>

<style scoped>
.pagina {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 20px 80px;
}

.topo {
  width: 100%;
  max-width: 640px;
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 36px;
}

.marca {
  font-family: 'Fraunces', serif;
  font-size: 28px;
  font-weight: 600;
  color: var(--ouro-claro);
}

.subtitulo {
  color: var(--texto-suave);
  font-size: 14px;
}

.conteudo {
  width: 100%;
  max-width: 640px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cartao {
  background: var(--tinta-clara);
  border-radius: 6px;
  padding: 28px 32px;
  border: 1px solid rgba(228, 185, 89, 0.18);
}

.cartao h1 {
  font-family: 'Fraunces', serif;
  font-size: 26px;
  margin: 0 0 8px;
}

.cartao h2 {
  font-family: 'Fraunces', serif;
  font-size: 18px;
  margin: 0 0 16px;
  font-weight: 500;
}

.descricao {
  color: var(--texto-suave);
  margin: 0 0 24px;
  line-height: 1.5;
}

.campo {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
  flex: 1;
}

.campo label {
  font-size: 13px;
  color: var(--texto-suave);
}

.linha {
  display: flex;
  gap: 16px;
}

.separador {
  text-align: center;
  color: var(--texto-suave);
  font-size: 13px;
  margin: 8px 0;
}

.voltar {
  background: none;
  border: none;
  color: var(--texto-suave);
  cursor: pointer;
  font-size: 14px;
  padding: 0;
  margin-bottom: 4px;
}

.voltar:hover {
  color: var(--pergaminho);
}

input {
  background: var(--tinta);
  border: 1px solid rgba(169, 180, 204, 0.3);
  border-radius: 4px;
  padding: 10px 12px;
  color: var(--pergaminho);
  font-size: 14px;
}

input:focus {
  outline: 2px solid var(--ouro);
  outline-offset: 1px;
}

.acao {
  background: var(--ouro);
  color: var(--tinta);
  border: none;
  border-radius: 4px;
  padding: 11px 20px;
  font-weight: 600;
  cursor: pointer;
  font-size: 14px;
}

.acao:hover {
  background: var(--ouro-claro);
}

.acao:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.identidade {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.selo {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 100px;
  border: 1px solid var(--texto-suave);
  color: var(--texto-suave);
}

.selo.premium {
  border-color: var(--ouro-claro);
  color: var(--ouro-claro);
}

.metricas {
  display: flex;
  gap: 40px;
  margin: 20px 0 0;
}

.metricas dt {
  font-size: 12px;
  color: var(--texto-suave);
  margin-bottom: 4px;
}

.metricas dd {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.moeda {
  color: var(--ouro-claro);
}

.trilha {
  list-style: none;
  padding: 0;
  margin: 0 0 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.trilha li {
  padding: 10px 14px;
  background: rgba(228, 185, 89, 0.08);
  border-left: 2px solid var(--ouro);
  border-radius: 2px;
  font-size: 14px;
}

.vazio {
  color: var(--texto-suave);
  font-size: 14px;
  margin: 0 0 20px;
}

.formulario {
  display: flex;
  gap: 10px;
}

.formulario input {
  flex: 1;
}

.historico {
  list-style: none;
  padding: 0;
  margin: 20px 0 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.historico li {
  font-size: 13px;
  color: var(--texto-suave);
}

.notificacoes {
  font-size: 13px;
  color: var(--ouro-claro);
}

.mensagem-erro {
  color: #e08585;
  font-size: 14px;
}
</style>