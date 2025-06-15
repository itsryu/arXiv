@echo off
echo "Iniciando servidores e cliente em janelas separadas..."

start "Servidor A" mvn exec:java -Dexec.mainClass="br.com.arxiv.ServerA"
start "Servidor B" mvn exec:java -Dexec.mainClass="br.com.arxiv.ServerB"
start "Servidor C" mvn exec:java -Dexec.mainClass="br.com.arxiv.ServerC"
start "Cliente" mvn exec:java -Dexec.mainClass="br.com.arxiv.Client"

echo "Processos iniciados."