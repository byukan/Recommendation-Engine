java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.UipIngestor http://localhost:7070 W-BPHhPngnOaDdWVDrvaJr_let3ZBR4Fqq3uDA7hElvkw_SiUE0C65Z-ecntJc4X uip "\",\"" 11 >uipLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.AmazonReportIngestor http://localhost:7070 W-BPHhPngnOaDdWVDrvaJr_let3ZBR4Fqq3uDA7hElvkw_SiUE0C65Z-ecntJc4X amazon "\"\n\"" "\",\"" 37 null @# /temp/courses 60 true http://172.30.89.210:8081>amazonLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.ProjectIngestor http://localhost:7070 W-BPHhPngnOaDdWVDrvaJr_let3ZBR4Fqq3uDA7hElvkw_SiUE0C65Z-ecntJc4X proj "\",\"" 7 >projectsLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.UserRelationIngestor http://localhost:7070 W-BPHhPngnOaDdWVDrvaJr_let3ZBR4Fqq3uDA7hElvkw_SiUE0C65Z-ecntJc4X ur "\",\"" 6 >urLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.SocialGroupIngestor http://localhost:7070 W-BPHhPngnOaDdWVDrvaJr_let3ZBR4Fqq3uDA7hElvkw_SiUE0C65Z-ecntJc4X sg "\",\"" >sgLogs

java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.dataIngestor.GroupMemberIngestor http://localhost:7070 W-BPHhPngnOaDdWVDrvaJr_let3ZBR4Fqq3uDA7hElvkw_SiUE0C65Z-ecntJc4X gm "\",\"" >gmLogs


