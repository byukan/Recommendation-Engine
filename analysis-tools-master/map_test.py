#!/usr/bin/env python

#####!/usr/local/bin/python

import logging

import click

from pyspark import SparkContext

from pyspark.sql import SQLContext
from pyspark.sql import functions as F
import predictionio

import pandas as pd
import numpy as np
import ml_metrics as metrics
from tqdm import tqdm

from report import CSVReport, ExcelReport
from config import init_config

from uuid import uuid4

logging.basicConfig(level=logging.INFO,
    format='%(asctime)s %(levelname)s %(message)s')

#logging = logging.getlogging(__name__)
#logging.setLevel(level=logging.DEBUG)

cfg = init_config('config.json')
logging.debug("Application was launched with config: %s" % str(cfg.init_dict))


def get_split_date(df, split_event, train_ratio=0.8):
    """Calculates split date

    Calculates the moment of time that we will use to split
    data into the train (befor the moment) and the test sets

    Args:
        df: Spark DataFrame
        train_ratio: ratio of samples in train set

    Returns:
        A datetime object
    """
    date_rdd = (df
                .filter("event = '%s'" % split_event)
                .select("Date")
                .sort("Date", ascending=True)
                .rdd)
    total_primary_events = date_rdd.count()
    split_date = (date_rdd
                  .zipWithIndex()
                  .filter(lambda x: x[1] > total_primary_events * train_ratio)
                  .first()[0][0])
    return split_date


def split_data(df):
    if cfg.splitting.type == "random":
        return df.randomSplit([cfg.splitting.train_ratio, 1 - cfg.splitting.train_ratio], seed=cfg.splitting.random_seed)
    elif cfg.splitting.type == "date":
        split_date = get_split_date(df, cfg.splitting.split_event, cfg.splitting.train_ratio)
        return df.filter(F.col("Date") < split_date), df.filter(F.col("Date") >= split_date)


def mk_intersection_matrix(by_rows, columns_for_matrix,
                           horizontal_suffix="", vertical_suffix=""):
    """ Makes pandas dataframe of intersections out of list of rows

    """
    result = pd.DataFrame(columns=[col + horizontal_suffix for col in columns_for_matrix])
    for en in columns_for_matrix:
        result.loc[en + vertical_suffix, :] = [0] * len(columns_for_matrix)
    for r in by_rows:
        row = r.asDict()
        en_h = row['event_left']
        en_v = row['event_right']
        count = row['count']
        result.loc[en_v + vertical_suffix, en_h + horizontal_suffix] = count
    return result


@click.command()
@click.option('--intersections', is_flag=True)
@click.option('--csv_report', is_flag=True)
def split(intersections, csv_report):
    logging.info('Splitting started')

    if csv_report:
        if cfg.reporting.use_uuid:
            uuid = uuid4()
            reporter = CSVReport(cfg.reporting.csv_dir, uuid)
        else:
            reporter = CSVReport(cfg.reporting.csv_dir, None)
    else:
        reporter = ExcelReport(cfg.reporting.file)

    logging.info('Spark initialization')
    sc = SparkContext(cfg.spark.master, 'map_test: split')
    sqlContext = SQLContext(sc)

    logging.info('Source file reading')
    df = sqlContext.read.json(cfg.splitting.source_file)
    df = df.withColumn("Date", F.from_utc_timestamp("eventTime", "UTC"))

    users_with_event_count = df.groupBy(F.col("entityId").alias("user")).count()


    logging.info('Filter users with small number of events')
    min_events = 10
    users_with_few_events = (users_with_event_count
                             .filter("count < %d" % (min_events))
                             .select(F.col("user").alias("user_with_few_events")))
    ndf = df.join(users_with_few_events,
                  F.col("entityId")==F.col("user_with_few_events"),
                  how="left_outer")
    df1 = ndf.filter("user_with_few_events is NULL").drop("user_with_few_events")


    logging.info('Split data into train and test')
    train_df, test_df = split_data(df)
    train_df.write.json(cfg.splitting.train_file, mode="overwrite")
    test_df.write.json(cfg.splitting.test_file, mode="overwrite")


    train_df = train_df.select("entityId", "event", "targetEntityId").cache()
    test_df = test_df.select("entityId", "event", "targetEntityId").cache()


    logging.info('Calculation of different stat metrics of datasets')
    events_by_type = (df
                      .groupBy("event")
                      .count()
                      .select(F.col("event"), F.col("count").alias("count_total"))
                      .toPandas())

    events_by_type_test = (test_df
                           .groupBy("event")
                           .count()
                           .select(F.col("event"), F.col("count").alias("count_test"))
                           .toPandas()
                           .set_index("event"))

    events_by_type_train = (train_df
                            .groupBy("event")
                            .count()
                            .select(F.col("event"), F.col("count").alias("count_train"))
                            .toPandas()
                            .set_index("event"))

    unique_users_by_event = (df
                             .select(F.col("entityId"), F.col("event"))
                             .distinct()
                             .groupBy("event")
                             .count()
                             .select(F.col("event"), F.col("count").alias("unique_users_total"))
                             .toPandas()
                             .set_index("event"))

    unique_users_by_event_train = (train_df
                                   .select(F.col("entityId"), F.col("event"))
                                   .distinct()
                                   .groupBy("event")
                                   .count()
                                   .select(F.col("event"), F.col("count").alias("unique_users_train"))
                                   .toPandas()
                                   .set_index("event"))

    unique_users_by_event_test = (test_df
                                  .select(F.col("entityId"), F.col("event"))
                                  .distinct()
                                  .groupBy("event")
                                  .count()
                                  .select(F.col("event"), F.col("count").alias("unique_users_test"))
                                  .toPandas()
                                  .set_index("event"))

    unique_items_by_event = (df
                             .select(F.col("targetEntityId"), F.col("event"))
                             .distinct()
                             .groupBy("event")
                             .count()
                             .select(F.col("event"), F.col("count").alias("unique_items_total"))
                             .toPandas()
                             .set_index("event"))

    unique_items_by_event_train = (train_df
                                   .select(F.col("targetEntityId"), F.col("event"))
                                   .distinct()
                                   .groupBy("event")
                                   .count()
                                   .select(F.col("event"), F.col("count").alias("unique_items_train"))
                                   .toPandas()
                                   .set_index("event"))

    unique_items_by_event_test = (test_df
                                  .select(F.col("targetEntityId"), F.col("event"))
                                  .distinct()
                                  .groupBy("event")
                                  .count()
                                  .select(F.col("event"), F.col("count").alias("unique_items_test"))
                                  .toPandas()
                                  .set_index("event"))

    logging.info('Calculate total counts')
    events = df.count()
    events_train = train_df.count()
    events_test = test_df.count()

    unique_users = df.select("entityId").distinct().count()
    unique_users_train = train_df.select("entityId").distinct().count()
    unique_users_test = test_df.select("entityId").distinct().count()

    unique_items = df.select(F.col("targetEntityId")).distinct().count()
    unique_items_train = train_df.select(F.col("targetEntityId")).distinct().count()
    unique_items_test = test_df.select(F.col("targetEntityId")).distinct().count()

    info_df = events_by_type
    dfs = [unique_users_by_event, unique_items_by_event,
            events_by_type_train, events_by_type_test,
            unique_users_by_event_train, unique_users_by_event_test,
            unique_items_by_event_train, unique_items_by_event_test]

    for data_frame in dfs:
        info_df = info_df.join(data_frame, on="event")

    n_rows, n_cols = info_df.shape

    # totals
    info_df.loc[n_rows] = ['ANY EVENT', events, unique_users, unique_items,
                        events_train, events_test,
                        unique_users_train, unique_users_test,
                        unique_items_train, unique_items_test]

    info_df.insert(4, 'events per user', info_df.ix[:, 1] / info_df.ix[:, 2])
    info_df.insert(5, 'events per item', info_df.ix[:, 1] / info_df.ix[:, 3])

    logging.info('Create event stat worksheet')
    reporter.start_new_sheet('Events stat')
    reporter.report(
        ['event', 'event count', 'unique users', 'unique items',
         'events per user', 'events per item',
         'event count train', 'event count test',
         'unique users train', 'unique users test',
         'unique items train', 'unique items test'],
        [column.tolist() for _, column in info_df.iteritems()],
        selected_rows=[next(info_df.iteritems())[1].tolist().index(cfg.testing.primary_event)],
        cfg=cfg)
    reporter.finish_sheet()

    if intersections:
        logging.info('Start intersections calculation')

        reporter.start_new_sheet('Intersections')

        columns_for_matrix = cfg.testing.events
        logging.info('Process train / train user intersection')
        train_train_users = (
            train_df
            .select(F.col("entityId").alias("user"), F.col("event").alias("event_left"))
            .distinct()
            .join(train_df.select(F.col("entityId").alias("user"), F.col("event").alias("event_right")).distinct(),
               on="user", how="inner")
            .groupBy(["event_left", "event_right"])
            .count()
            .collect())

        trtru = mk_intersection_matrix(train_train_users, columns_for_matrix)
        reporter.report(
            [''] + list(trtru.columns.values),
            [trtru.index.tolist()] + [column for _, column in trtru.iteritems()],
            title='Train / train user intersection')

        logging.info('Process train / test user intersection')
        train_test_users = (
            train_df
            .select(F.col("entityId").alias("user"), F.col("event").alias("event_left"))
            .distinct()
            .join(test_df.select(F.col("entityId").alias("user"), F.col("event").alias("event_right")).distinct(),
               on="user", how="inner")
            .groupBy(["event_left", "event_right"])
            .count()
            .collect())

        trtsu = mk_intersection_matrix(train_test_users, columns_for_matrix,
                                       horizontal_suffix=" train", vertical_suffix=" test")
        reporter.report(
            [''] + list(trtsu.columns.values),
            [trtsu.index.tolist()] + [column for _, column in trtsu.iteritems()],
            title='Train / test user intersection')

        logging.info('Process train / train item intersection')
        train_train_items = (
            train_df
            .select(F.col("targetEntityId").alias("item"), F.col("event").alias("event_left"))
            .distinct()
            .join(train_df.select(F.col("targetEntityId").alias("item"), F.col("event").alias("event_right")).distinct(),
               on="item", how="inner")
            .groupBy(["event_left", "event_right"])
            .count()
            .collect())

        trtri = mk_intersection_matrix(train_train_items, columns_for_matrix)
        reporter.report(
            [''] + list(trtri.columns.values),
            [trtri.index.tolist()] + [column for _, column in trtri.iteritems()],
            title='Train / train item intersection'
        )

        logging.info('Process train / test item intersection')
        train_test_items = (
            train_df
            .select(F.col("targetEntityId").alias("item"), F.col("event").alias("event_left"))
            .distinct()
            .join(test_df.select(F.col("targetEntityId").alias("item"), F.col("event").alias("event_right")).distinct(),
               on="item", how="inner")
            .groupBy(["event_left", "event_right"])
            .count()
            .collect())

        trtsi = mk_intersection_matrix(train_test_items, columns_for_matrix,
                                       horizontal_suffix=" train", vertical_suffix=" test")
        reporter.report(
            [''] + list(trtsi.columns.values),
            [trtsi.index.tolist()] + [column for _, column in trtsi.iteritems()],
            title='Train / test item intersection'
        )

        reporter.report_config(cfg)

    reporter.finish_document()
    logging.info('Splitting finished successfully')


def run_map_test_dummy(data, items=None, probs=None, uniform=True, top=True,
                       users=None, primaryEvent=cfg.testing.primary_event, K=10, no_progress=False):
    """Performs dummy test

    Args:
        data: list of event rows
        items: np.array or list of items sorted in descending popularity order
        probs: np.array or list of corresponding probabilities (needed for experiment #2)
        uniform: Boolean flag to use uniform sampling
        top: Boolean flag to use top items
        users: set of users to consider
        primaryEvent: str name of primary event
        K: int for MAP @ K
        no_progress: Boolean flag not to show the progress bar during calculations

    Returns:
        list of [MAP@1, MAP@2, ... MAP@K] evaluations
    """
    d = {}
    for rec in data:
        if rec.event == primaryEvent:
            user = rec.entityId
            item = rec.targetEntityId
            if (users is None) or (user in users):
                d.setdefault(user, []).append(item)

    holdoutUsers = d.keys()

    prediction = []
    ground_truth = []
    if no_progress:
        gen = holdoutUsers
    else:
        gen = tqdm(holdoutUsers)
    for user in gen:
        if top:
            test_items = items[0:K]
        elif uniform:
            test_items = np.random.choice(items, size=(K,))
        else:
            test_items = np.random.choice(items, size=(K,), p=probs)
        prediction.append(test_items)
        ground_truth.append(d.get(user, []))
    return [metrics.mapk(ground_truth, prediction, k) for k in range(1, K + 1)]


def run_map_test(data, eventNames, users=None, primaryEvent=cfg.testing.primary_event,
                 consider_non_zero_scores=cfg.testing.consider_non_zero_scores_only,
                 num=200, K=cfg.testing.map_k, test=False, predictionio_url="http://0.0.0.0:8000"):
    N_TEST = 2000
    d = {}
    res_data = {}
    engine_client = predictionio.EngineClient(url=predictionio_url)

    for rec in data:
        if rec.event == primaryEvent:
            user = rec.entityId
            item = rec.targetEntityId
            if (users is None) or (user in users):
                d.setdefault(user, []).append(item)

    if test:
        holdoutUsers = d.keys()[1:N_TEST]
    else:
        holdoutUsers = d.keys()

    prediction = []
    ground_truth = []
    user_items_cnt = 0.0
    users_cnt = 0
    for user in tqdm(holdoutUsers):
        q = {
            "user": user,
            "eventNames": eventNames,
            "num": num,
        }

        try:
            res = engine_client.send_query(q)
            # Sort by score then by item name
            tuples = sorted([(r["score"], r["item"]) for r in res["itemScores"]], reverse=True)
            scores = [score for score, item in tuples]
            items = [item for score, item in tuples]
            res_data[user] = {
                "items": items,
                "scores": scores,
            }
            # Consider only non-zero scores
            if consider_non_zero_scores:
                if len(scores) > 0 and scores[0] != 0.0:
                    prediction.append(items)
                    ground_truth.append(d.get(user, []))
                    user_items_cnt += len(d.get(user, []))
                    users_cnt += 1
            else:
                prediction.append(items)
                ground_truth.append(d.get(user, []))
                user_items_cnt += len(d.get(user, []))
                users_cnt += 1
        except predictionio.NotFoundError:
            print("Error with user: %s" % user)
    return ([metrics.mapk(ground_truth, prediction, k) for k in range(1, K + 1)],
            res_data, user_items_cnt / (users_cnt + 0.00001))


def get_nonzero(r_data):
    users = [user for user, res_data in r_data.items() if res_data['scores'][0] != 0.0]
    return users


@click.command()
@click.option('--csv_report', is_flag=True)
@click.option('--all', is_flag=True)
@click.option('--dummy_test', is_flag=True)
@click.option('--separate_test', is_flag=True)
@click.option('--all_but_test', is_flag=True)
@click.option('--primary_pairs_test', is_flag=True)
@click.option('--custom_combos_test', is_flag=True)
@click.option('--non_zero_users_from_file', is_flag=True)
def test(csv_report,
         all,
         dummy_test,
         separate_test,
         all_but_test,
         primary_pairs_test,
         custom_combos_test,
         non_zero_users_from_file):

    logging.info('Testing started')

    if csv_report:
        if cfg.reporting.use_uuid:
            uuid = uuid4()
            reporter = CSVReport(cfg.reporting.csv_dir, uuid)
        else:
            reporter = CSVReport(cfg.reporting.csv_dir, None)
    else:
        reporter = ExcelReport(cfg.reporting.file)

    logging.info('Spark context initialization')
    sc = SparkContext(cfg.spark.master, 'map_test: test')
    sqlContext = SQLContext(sc)

    logging.info('Test data reading')
    test_df = sqlContext.read.json(cfg.splitting.test_file).select("entityId", "event", "targetEntityId").cache()

    test_data = test_df.filter("event = '%s'" % (cfg.testing.primary_event)).collect()

    #non_zero_users = set([r[0] for r in test_data][500:650]) # Because actually all our users have 0.0 scores -- too few data

    if all or dummy_test:
        logging.info('Train data reading')

        train_df = sqlContext.read.json(cfg.splitting.train_file).select("entityId", "event", "targetEntityId").cache()
        counts = train_df.filter("event = '%s'" % (cfg.testing.primary_event)).groupBy("targetEntityId").count().collect()

        sorted_rating = sorted([(row.asDict()['count'], row.asDict()['targetEntityId']) for row in counts], reverse=True)
        elements = np.array([item for cnt, item in sorted_rating])
        probs = np.array([cnt for cnt, item in sorted_rating])
        probs = 1.0 * probs / probs.sum()

        logging.info('Process dummy test')
        # case 1. Random sampling from items (uniform)
        dummy_uniform_res = run_map_test_dummy(test_data, items=elements, probs=probs,
                                               uniform=True, top=False, K=cfg.testing.map_k)

        # case 2. Random sampling from items (according to their distribution in training data)
        dummy_res = run_map_test_dummy(test_data, items=elements, probs=probs,
                                       uniform=False, top=False, K=cfg.testing.map_k)

        # case 3. Top-N items from training data
        dummy_top_res = run_map_test_dummy(test_data, items=elements, probs=probs,
                                           uniform=True, top=True, K=cfg.testing.map_k)

        reporter.start_new_sheet('Dummy MAP benchmark')
        reporter.report(
            ['', 'Random uniform', 'Random sampled from train', 'Top - N'],
            [[('MAP @ %d' % i) for i in range(1, len(dummy_res)+1)]] + [dummy_uniform_res, dummy_res, dummy_top_res],
            cfg=cfg
        )
        reporter.finish_sheet()

        logging.info('Process top 20 dummy test')
        scores = []
        for i in range(20):
            scores.append(run_map_test_dummy(test_data, items=elements[i:], uniform=True,
                                             top=True, K=1, no_progress=True)[0])

        reporter.start_new_sheet('Top-20 perfomance')
        reporter.report(
            ['Rank', 'MAP@1'],
            [list(range(1, 21)), scores],
            bold_first_column=False,
            cfg=cfg
        )
        reporter.finish_sheet()

    if all or separate_test or all_but_test or primary_pairs_test or custom_combos_test:
        logging.info('Non zero users')
        if non_zero_users_from_file:
            with open(cfg.testing.non_zero_users_file) as input:
                non_zero_users = set(input.read().split(','))
        else:
            _, r_data, _ = run_map_test(test_data, [cfg.testing.primary_event], test=False)
            non_zero_users = get_nonzero(r_data)
            with open(cfg.testing.non_zero_users_file, 'w') as output:
                output.write(','.join(non_zero_users))

    if all or separate_test:
        logging.info('Process "map separate events" test')
        columns = []
        for ev in cfg.testing.events:
            (r_scores, r_data, ipu) = run_map_test(test_data, [ev], users=non_zero_users, test=False)
            columns.append(r_scores + [len(non_zero_users)])

        first_column = [('MAP @ %d' % i) for i in range(1, len(columns[0]))] + ['non-zero users']

        reporter.start_new_sheet('MAP separate events')
        reporter.report(
            ['event'] + cfg.testing.events,
            [first_column] + columns,
            selected_columns=[cfg.testing.events.index(cfg.testing.primary_event) + 1],
            cfg=cfg
        )
        reporter.finish_sheet()

    if all or all_but_test:
        logging.info('Process "map all but..." test')
        events_scores = []
        for ev in cfg.testing.events:
            evs = list(cfg.testing.events)
            evs.remove(ev)
            (r_scores, r_data, ipu) = run_map_test(test_data, evs, users=non_zero_users, test=False)
            events_scores.append(r_scores + [len(non_zero_users)])

        evl = cfg.testing.events
        all_scores, r_data, ipu = run_map_test(test_data, evl, users=non_zero_users, test=False)
        all_scores.append(len(non_zero_users))

        first_column = [('MAP @ %d' % i) for i in range(1, len(all_scores))] + ['non-zero users']
        reporter.start_new_sheet('MAP all but...')
        reporter.report(
            ['event'] + cfg.testing.events + ['All'],
            [first_column] + events_scores + [all_scores],
            selected_columns=[cfg.testing.events.index(cfg.testing.primary_event) + 1],
            cfg=cfg
        )
        reporter.finish_sheet()

    if all or primary_pairs_test:
        logging.info('Process "map pairs with primary" test')
        columns = []
        events_without_primary = [event for event in cfg.testing.events if event != cfg.testing.primary_event]
        for event in events_without_primary:
            (r_scores, r_data, ipu) = run_map_test(test_data, [cfg.testing.primary_event, event],
                                                   users=non_zero_users, test=False)
            columns.append(r_scores + [len(non_zero_users)])

        first_column = [('MAP @ %d' % i) for i in range(1, len(columns[0]))] + ['non-zero users']

        reporter.start_new_sheet('MAP pairs with primary')
        reporter.report(
            ['event'] + events_without_primary,
            [first_column] + columns,
            cfg=cfg
        )
        reporter.finish_sheet()

    if all or custom_combos_test:
        logging.info('Process "custom combos" test')
        columns = []
        for event_group in cfg.testing.custom_combos.event_groups:
            if len(event_group) == 2 and cfg.testing.primary_event in event_group and primary_pairs_test:
                logging.warn("Report for group %s already generated in 'MAP pairs with primary'" % str(event_group))
                continue

            if len(event_group) == 1 and separate_test:
                logging.warn("Report for group %s already generated in 'MAP separate events'" % str(event_group))
                continue

            if len(event_group) >= len(cfg.testing.events) - 1 and all_but_test:
                logging.warn("Report for group %s already generated in 'All but...'" % str(event_group))
                continue

            (r_scores, r_data, ipu) = run_map_test(test_data, event_group,
                                                   users=non_zero_users, test=False)
            columns.append(r_scores + [len(non_zero_users)])

        first_column = [('MAP @ %d' % i) for i in range(1, len(columns[0]))] + ['non-zero users']

        reporter.start_new_sheet('Custom combos')
        reporter.report(
            ['event'] + [str([s.encode('utf-8') for s in group]) for group in cfg.testing.custom_combos.event_groups],
            [first_column] + columns,
            cfg=cfg
        )
        reporter.finish_sheet()

    reporter.finish_document()
    logging.info('Testing finished successfully')

# root group
@click.group()
def root():
    pass


root.add_command(split)
root.add_command(test)

if __name__ == "__main__":
    root()
