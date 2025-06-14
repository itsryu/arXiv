@echo off
echo "Iniciando servidores e cliente em janelas separadas..."

start "Servidor A" mvn exec:java -Dexec.mainClass="br.com.puc.ServerA"
start "Servidor B" mvn exec:java -Dexec.mainClass="br.com.puc.ServerB"
start "Servidor C" mvn exec:java -Dexec.mainClass="br.com.puc.ServerC"

echo "Aguardando 5 segundos para os servidores iniciarem..."

start "Cliente" mvn exec:java -Dexec.mainClass="br.com.puc.ClientApp"

echo "Processos iniciados."