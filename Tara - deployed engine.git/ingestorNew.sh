java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.UipIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI uip "\",\"" 11 >uipLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.CatalogIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI courses "\"\n\"" "\",\"" 13 null @# /tempNew/courses 100 true http://172.30.89.210:8081>coursesLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.StepIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI step "\"\n\"" "\",\"" 35 /tempNew/courses @# /tempNew/courses 100 true http://172.30.89.210:8081>stepLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.ProjectIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI proj "\",\"" 7 >projectsLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.UserRelationIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI ur "\",\"" 6 >urLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.SocialGroupIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI sg "\",\"" >sgLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.GroupMemberIngestor http://localhost:7070 aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI gm "\",\"" >gmLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.TrainingUpdateCatalogIngestor trainCatalog "\"\n\"" "\",\"" http://172.30.89.210:8081>trainCatalogLogs


