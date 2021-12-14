CREATE KEYSPACE IF NOT EXISTS fodmap WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TYPE fodmap.stratified_data(amount int, fructose text, lactose text, manitol text, sorbitol text, gos text, fructan text);
CREATE TYPE fodmap.aggregated_data(red frozen<stratified_data>, amber frozen<stratified_data>, green frozen<stratified_data>);

CREATE TABLE fodmap.food_item (food_group text, name text, overall_rating text, data aggregated_data, PRIMARY KEY (food_group,name)
) WITH bloom_filter_fp_chance = 0.1
    AND caching = {'keys':'ALL', 'rows_per_partition':'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.LeveledCompactionStrategy'}
    AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.SnappyCompressor'}
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99.0PERCENTILE';

INSERT INTO fodmap.food_item(food_group,name,data) VALUES('vegitable','carrot', {red:{fructose:'green'},amber:{fructose:'red'},green:{fructose:'amber'}});

