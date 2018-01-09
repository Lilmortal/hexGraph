package nz.co.hexgraph.partitioner;

import nz.co.hexgraph.HexGraphInitialization;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CameraPartitioner implements Partitioner {
    public static final Logger log = LoggerFactory.getLogger(CameraPartitioner.class);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.availablePartitionsForTopic(topic);
//        log.info("BB" + partitions.size());
//        partitions.stream().forEach(partition -> log.info("YY" + partition.partition()));
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        for (Map.Entry<String, ?> entry : configs.entrySet()) {
            if (entry.getKey().startsWith("partition.")) {
//                log.info("Partition stuff: " + entry.getKey() + " " + entry.getValue());
            }
        }
    }
}
