export PATH=$JAVA_18:$PATH

java -classpath Lib:Lib/commons-logging-1.1.jar:addAudio.jar:dfs-libs.jar:darts-remote.jar com.logica.darts.webservice.client.DARTSAddAudioClient $1 $2 $3
