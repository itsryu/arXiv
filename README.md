<body>
    <h1>Sistema de Busca Distribuída de Artigos</h1>
    <hr>
    <h2>O que é o Projeto?</h2>
    <p>
        Este projeto implementa um sistema de busca distribuído em Java 17, utilizando Sockets para a comunicação em
        rede. A arquitetura simula um ambiente real onde dados são particionados e processados por diferentes máquinas
        para otimizar o desempenho e garantir escalabilidade.
    </p>
    <p>O sistema é composto por quatro componentes principais:</p>
    <ul>
        <li><b>Cliente:</b> Uma interface de linha de comando (CLI) que permite ao usuário final realizar buscas.</li>
        <li><b>Servidor A (Orquestrador):</b> Atua como um gateway. Ele recebe as requisições do cliente, as distribui
            para os servidores de trabalho e, por fim, agrega os resultados antes de devolvê-los ao cliente.</li>
        <li><b>Servidor B (Nó de Trabalho):</b> Responsável por buscar em uma metade do conjunto de dados de artigos.
        </li>
        <li><b>Servidor C (Nó de Trabalho):</b> Responsável por buscar na outra metade do conjunto de dados.</li>
    </ul>
    <hr>
    <h2>Como Executar</h2>
    <h3>1. Pré-requisitos</h3>
    <p>Antes de começar, garanta que você tenha os seguintes softwares instalados em seu sistema:</p>
    <ul>
        <li>Java 17 (JDK)</li>
        <li>Apache Maven</li>
    </ul>
    <h3>2. Instalação e Configuração</h3>
    <ol>
        <li>
            <strong>Clone o repositório</strong><br>
            <code>git clone https://github.com/itsryu/arXiv.git</code>
        </li>
        <li>
            <strong>Posicione os Arquivos de Dados</strong><br>
            Mova os arquivos <code>dados_servidor_b.json</code> e <code>dados_servidor_c.json</code> para o diretório
            correto dentro do projeto: <code>src/main/resources/data</code>.
        </li>
        <li>
            <strong>Compile o Projeto e Instale as Dependências</strong><br>
            Abra um terminal na raiz do projeto e execute o seguinte comando Maven. Ele irá baixar as dependências e
            compilar o código.
            <pre><code>mvn clean package</code></pre>
        </li>
    </ol>
    <h3>3. Execução do Sistema</h3>
    <p>Existem duas maneiras de iniciar o sistema: o método manual, que é mais confiável para desenvolvimento, e o método automatizado via scripts, para conveniência no Windows.</p>
    <h4>Método 1: Execução Manual (Recomendado)</h4>
    <p>Este método requer a abertura de <strong>quatro (4) janelas de terminal</strong> separadas. É o método mais
        estável, especialmente em ambientes de desenvolvimento.</p>
    <ol>
        <li>
            <strong>Terminal 1 - Iniciar Servidor A (Orquestrador)</strong><br>
            <pre><code>mvn exec:java -Dexec.mainClass="br.com.arxiv.ServerA"</code></pre>
        </li>
        <li>
            <strong>Terminal 2 - Iniciar Servidor B (Nó de Trabalho)</strong><br>
            <pre><code>mvn exec:java -Dexec.mainClass="br.com.arxiv.ServerB"</code></pre>
        </li>
        <li>
            <strong>Terminal 3 - Iniciar Servidor C (Nó de Trabalho)</strong><br>
            <pre><code>mvn exec:java -Dexec.mainClass="br.com.arxiv.ServerC"</code></pre>
        </li>
        <li>
            <strong>Terminal 4 - Iniciar Cliente</strong><br>
            <p>Após os três servidores estarem no ar, inicie o cliente:</p>
            <pre><code>mvn exec:java -Dexec.mainClass="br.com.arxiv.Client"</code></pre>
        </li>
    </ol>
    <h4>Método 2: Execução Automatizada com Scripts (.bat - Apenas Windows)</h4>
    <p>Para conveniência no sistema operacional Windows, foram criados arquivos de lote (.bat) que automatizam a inicialização de cada componente.</p>
    <ul>
        <li><code>server_a.bat</code>: Inicia o Servidor A (Orquestrador).</li>
        <li><code>server_b.bat</code>: Inicia o Servidor B (Nó de Trabalho).</li>
        <li><code>server_c.bat</code>: Inicia o Servidor C (Nó de Trabalho).</li>
        <li><code>client.bat</code>: Inicia o cliente para interação.</li>
        <li><code>all.bat</code>: Script mestre que executa todos os outros, iniciando o sistema completo, cada componente em sua própria janela de terminal.</li>
    </ul>
    <p>
        <strong>Como usar:</strong> Basta executar o arquivo <code>all.bat</code> para iniciar todos os
        servidores e o cliente de uma só vez.
    </p>
    <p>
        <strong>Aviso:</strong> A execução de scripts pode apresentar instabilidade dependendo do ambiente (por exemplo,
        dentro de terminais de IDEs), podendo levar a erros. Caso os scripts falhem, utilize o Método 1 (Execução
        Manual).
    </p>
</body>