task list{
  apache predictionio
}

#5/5{
scp -r root@f186347626b2:/ "C:\Users\Justin\Desktop"


hostname ip address
172.17.0.6

Train and Deploy: Import the "train" dataset into the EventServer, train, and deploy the model. This can also be scripted but is not part of these scripts.

http://predictionio.incubator.apache.org/datacollection/batchimport/
The import tool expects its input to be a file stored either in the local filesystem or on HDFS.

pio import --appid 10 --input /Tara/universalRecommenderNew/store_events/train.1
pio import --appid 10 --input /Tara/universalRecommenderNew/store_events/train.1
pio import --appid 10 --input /Tara/universalRecommenderNew/store_events/train.1
pio import --appid 10 --input /Tara/universalRecommenderNew/store_events/train.1

pio build --verbose
pio train

[INFO] [MasterActor] Engine is deployed and running. Engine API is live at http://0.0.0.0:8000.

test:
SPARK_HOME=/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6 PYTHONPATH=/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6/python:/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6/python/lib/py4j-0.9-src.zip ./map_test.py test --all

[INFO] [URAlgorithm] Misconfigured date information, either your engine.json date settings or your query's dateRange is incorrect.
Ingoring date information for this query.


File "./map_test.py", line 421, in run_map_test
  tuples = sorted([(r["score"], r["item"]) for r in res["itemScores"]], reverse=True)
KeyError: 'itemScores'


the response objects don't have the 'itemScores' key, was it modified from the original template?
solution:  modify itemScores to "score"

{ "user": "de22719ee50124fdb8c09882bc19fde8"}

"entityId":"de22719ee50124fdb8c09882bc19fde8","entityType":"user"

curl -H "Content-Type: application/json" \
-d '{ "user": "de22719ee50124fdb8c09882bc19fde8", "num": 4 }' http://0.0.0.0:8000/queries.json

change "itemScore" to "score"

  to deploy on an engine trained on only the train set,
  use an import script to import more events in batch




  ##Train a Model

  The above command will create a test and training split in the location specified in config.json. Now you must import, setup engine.json, train and deploy the "train" model so the rest of the MAP@k tests will be able to query the model.


start spark manually
./sbin/start-master.sh
http://spark.apache.org/docs/latest/spark-standalone.html

have to create workers too

  /Tara/universalRecommenderNew/store_events/part-00000


./sbin/start-slave.sh spark://f186347626b2:7077
"master": "spark://f186347626b2:7077"

http://f186347626b2:8080


  these scripts handle the transform from datasets to input events
  https://gitlab.os/hranalytics/Tara/tree/dev/InputValidator/src/main/scala/com/tara/dataIngestor
}

#5/8{
  deleted data on courserecommender app id 10
  used preparerNew.sh
    and ingestorNew.sh
  waiting since 3:57p


  }
}

#5/4{

started sparkUI at:
http://172.17.0.6:4040

  look at engine.json and see which one is the appname in there and use that one\

pio export --appid 10 --output /Tara/universalRecommenderNew/store_events

split:
SPARK_HOME=/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6 PYTHONPATH=/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6/python:/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6/python/lib/py4j-0.9-src.zip ./map_test.py split


/Tara/universalRecommenderNew/store_events



where to find source file of training data?


docker exec -it f186347626b2 /bin/bash

export SPARK_HOME=/PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6
export PYTHONPATH=$SPARK_HOME/python/:$SPARK_HOME/python/build/:$PYTHONPATH


put analysis-tools contents into universalrecommenderNew

store events in:
/Tara/universalRecommenderNew/store_events

courseRecommenderWilson


•	cd Tara/
•	pio app data-delete courseRecommender   (clear the application)
•	bash preparer.sh
•	bash ingestor.sh
takes a while, 15 mins?
•	cd universalRecommender
•	pio build
•	pio train (if fails then try to free some ram using the command “sudo echo 3 > /proc/sys/vm/drop_caches”)
•	pio deploy &



  /PredictionIO-0.10.0-incubating/vendors/spark-1.5.1-bin-hadoop2.6
  /PredictionIO-0.10.0-incubating/vendors/spark-1.6.3-bin-hadoop2.6
}

#5/3{
  precision at k scripts for the universal recommender
}


#4/27{
  courserecommenderJustin

}

#4/20{
  [INFO] [App$] Initialized Event Store for this app ID: 13.Connect
[INFO] [App$] Created new app:
[INFO] [App$]       Name: courserecommenderJustin
[INFO] [App$]         ID: 13est(Ama
[INFO] [App$] Access Key: awW4F_yxSocdlfLiq0xwNpjni8uvYA2_-JrvTUobYVh3-UEQoBcvk7BFA9NHNz2C

the justin one:
http://http://172.30.89.209:8080/events.json?accessKey=&lt;awW4F_yxSocdlfLiq0xwNpjni8uvYA2_-JrvTUobYVh3-UEQoBcvk7BFA9NHNz2C>

courserecommender:

http://http://172.30.89.209:8080/events.json?accessKey=&lt;aQUbYIo7fjAgAqwmDY-q68O09JLqdMF3bLxALQDvkxKDjwUoMQtepuEAyEaPsXWI

}

#4/19{
  laid out task list, figure out ingester input
  write paper about word2vec experiments, but don't put it in production
  implemnet evaluation metrics, using RMSE and MAE
  train test split plan
}

#4/13{
  universal recommender
}

#4/12{
  predictionIO
  recap and afterthoughts:
    - implementation objective is to get employees to actually take recommended course, in a way that betters the employee experience through personalization.  It's also to address skill mismatch for govermenent or unionized employees to update their skillsets and move to new positions.
    - prioritize evaluation metrics to compare variants per Wilson's request
    - focus on figuring out how to run multiple engines on PredictioIO
    - implement series / doc2vec / employee clustering ideas as supplementary to collaborative filtering, i.e. insert courses identified with those approaches to those generated by collaborative filtering
}

#4/7{
  published doc2vec notebook
  # Doc2Vec Cosine Similarity

#### :brief: This program finds similar courses based off course title, overview description, and target audience.  We convert the paragraphs to doc2vec vectors, then compute the cosine similarity among all the courses.
}

#4/6{
  gensim doc2vec & IMDB sentiment dataset
  https://github.com/RaRe-Technologies/gensim/blob/develop/docs/notebooks/doc2vec-IMDB.ipynb
  cosine similarity based only off descrtiption + target audience
}

#3/31{
  tfidf on course title, description, and target audience
}

#3/30{
  web scraper that follows url and scrapes the description
}

#3/29{
  met with Alessandro, notes in notebook
  start with a barebones system using predictionio
}

#3/24{
  1. discover insights from dataset (employee bios, social network data)
  2. improve the current recommendation engine built using predictionio
  3. build your own recommendation engine
  4. deal with couse ids changing, fuzzy match on title
}

#3/23{
  discussed fuzzy match and changeing course id challenge
  more tables and familiarity with interface
}

#3/22{
  explored tables and schema, exploration.ipynb
  thinking about project ideas
}
