// B1 / Desafio 6 - CI/CD (ADR-006)
// Pipeline unico do monorepo, com um estagio de teste por componente rodando em paralelo:
//   Core Java (JUnit + ArchUnit + JaCoCo 100%) | ia-service (pytest) | iot-gateway (pytest)
// No Jenkins: Novo item -> Pipeline -> "Pipeline script from SCM" -> Git -> este repositorio,
// Branch: a branch do B1, Script Path: Jenkinsfile
pipeline {
    agent any

    parameters {
        booleanParam(name: 'CONSTRUIR_IMAGENS', defaultValue: false,
                description: 'Construir as imagens Docker (docker compose build). Exige Docker no agente.')
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Testes por componente') {
            parallel {
                stage('Core Java') {
                    steps {
                        dir('backend') {
                            // verify = testes (JUnit + ArchUnit) + relatorio JaCoCo + jacoco:check (100% linha e branch)
                            script { executar('mvn -B clean verify') }
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                            recordCoverage tools: [[parser: 'JACOCO', pattern: 'backend/target/site/jacoco/jacoco.xml']]
                        }
                    }
                }

                stage('ia-service (Python)') {
                    steps {
                        dir('ia-service') {
                            script { pytest('requirements.txt') }
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'ia-service/relatorio-pytest.xml'
                        }
                    }
                }

                stage('iot-gateway (Python)') {
                    steps {
                        dir('iot-gateway') {
                            script { pytest('requirements-dev.txt') }
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'iot-gateway/relatorio-pytest.xml'
                        }
                    }
                }
            }
        }

        stage('Artefato do Core') {
            steps {
                // o jar ja foi gerado pelo "verify"; aqui so e arquivado no Jenkins
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            }
        }

        stage('Imagens Docker') {
            when { expression { params.CONSTRUIR_IMAGENS } }
            steps {
                script { executar('docker compose build') }
            }
        }
    }

    post {
        success {
            echo 'Pipeline OK: Core com 100% de cobertura e fronteiras validas; servicos Python testados.'
        }
        failure {
            echo 'Pipeline falhou: veja o estagio em vermelho (teste, fronteira ArchUnit ou cobertura abaixo de 100%).'
        }
    }
}

// Roda um comando no Windows (bat) ou no Linux (sh)
def executar(String comando) {
    if (isUnix()) {
        sh comando
    } else {
        bat comando
    }
}

// Cria um ambiente virtual isolado, instala as dependencias e roda o pytest gerando relatorio JUnit
def pytest(String requisitos) {
    if (isUnix()) {
        sh "python3 -m venv .venv && .venv/bin/pip install -q -r ${requisitos} && .venv/bin/python -m pytest -q --junitxml=relatorio-pytest.xml"
    } else {
        bat "python -m venv .venv && .venv\\Scripts\\pip install -q -r ${requisitos} && .venv\\Scripts\\python -m pytest -q --junitxml=relatorio-pytest.xml"
    }
}
