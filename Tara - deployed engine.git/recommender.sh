#userIds=`tail -n1 testUserIds`
userIds=$1
numberOfRecommendations=$2
java -cp /Tara/InputValidator/target/InputValidator-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tara.recommender.Recommender http://localhost:8000 $userIds $numberOfRecommendations false
