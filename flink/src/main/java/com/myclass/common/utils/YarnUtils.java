package com.myclass.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.*;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterInformationRetriever;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnConfigOptionsInternal;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class YarnUtils {

    private static final String HDFS_SERVER = "hdfs://127.0.0.1:9000";
    private static final String YARN_HOST_NAME = "127.0.0.1";
    private static final String HDFS_CONFIG_DIR = "/Users/sunsongyang/hadoop/etc/hadoop";
    private static final String FLINK_LIB_DIR = "/Users/sunsongyang/flink/lib";
    private static final String FLINK_DIST_JAR = "/Users/sunsongyang/flink/lib/flink-dist_2.11-1.13.5.jar";
    private static final String FLINK_CONFIG_DIR = "/Users/sunsongyang/flink/conf";
    private static final String FLINK_LOG4J_FILE = "/Users/sunsongyang/flink/conf/log4j.properties";
    private static final Boolean ENABLE_SAVEPOINT = true;
    private static final String SAVEPOINT_PATH = "/Users/sunsongyang/IdeaProjects/bigdata/flink/src/main/resources/savepoints";
    private static final String SAVEPOINT_FILE = "";
    private static final String APPLICATION_NAME = "WordCount";
    private static final String YARN_QUEUE = "flink_queue";
    private static final String DEFAULT_MEMORY = "1024";
    private static final Integer DEFAULT_PARALLELISM = 1;

    public static Tuple2<String, String> deployWithPreJob(String jarFile, String fullClassName) throws ProgramInvocationException, ClusterDeploymentException {
        System.setProperty("HADOOP_CONFIG_DIR", HDFS_CONFIG_DIR);
        // 生成flink配置，设置yarn部署模式、配置文件目录、savepoint、AppMaster所用到的ship file、yarn应用名称、yarn提交队列及内存
        Configuration flinkConfiguration = GlobalConfiguration.loadConfiguration(FLINK_CONFIG_DIR);
        flinkConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.PER_JOB.getName());
        flinkConfiguration.set(DeploymentOptionsInternal.CONF_DIR, FLINK_CONFIG_DIR);
        flinkConfiguration.set(YarnConfigOptionsInternal.APPLICATION_LOG_CONFIG_FILE, FLINK_LOG4J_FILE);
        flinkConfiguration.set(DeploymentOptions.ATTACHED, java.lang.Boolean.TRUE);
        // 提交时指定savepoint
        if (StringUtils.isNotBlank(SAVEPOINT_FILE)) {
            flinkConfiguration.set(SavepointConfigOptions.SAVEPOINT_PATH, SAVEPOINT_FILE);
            flinkConfiguration.set(SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE, java.lang.Boolean.FALSE);
        }
        flinkConfiguration.set(YarnConfigOptions.SHIP_FILES, Collections.singletonList(FLINK_LIB_DIR));
        flinkConfiguration.set(YarnConfigOptions.FLINK_DIST_JAR, FLINK_DIST_JAR);
        flinkConfiguration.set(YarnConfigOptions.APPLICATION_NAME, APPLICATION_NAME);
        // 设置提交任务到yarn的队列名
        // flinkConfiguration.set(YarnConfigOptions.APPLICATION_QUEUE, YARN_QUEUE);
        flinkConfiguration.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse(DEFAULT_MEMORY, MemorySize.MemoryUnit.MEGA_BYTES));
        flinkConfiguration.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse(DEFAULT_MEMORY, MemorySize.MemoryUnit.MEGA_BYTES));

        // 创建yarn配置，设置hadoop及yarn服务器地址
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.set("fs.defaultFS", HDFS_SERVER);
        yarnConfiguration.set("yarn.resourcemanager.hostname", YARN_HOST_NAME);

        // 程序打包并生成jobGraph
        PackagedProgram packageProgram = PackagedProgram.newBuilder()
                .setJarFile(new File(jarFile))
                .setEntryPointClassName(fullClassName)
                .build();
        JobGraph jobGraph = PackagedProgramUtils.createJobGraph(packageProgram, flinkConfiguration, DEFAULT_PARALLELISM, false);

        // 创建cluster client，设置配置信息
        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(Integer.parseInt(DEFAULT_MEMORY))
                .setTaskManagerMemoryMB(Integer.parseInt(DEFAULT_MEMORY))
                .createClusterSpecification();
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
        // 创建yarn集群的retriever，生成集群descriptor并部署任务
        YarnClusterInformationRetriever yarnClusterInformationRetriever = YarnClientYarnClusterInformationRetriever.create(yarnClient);
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(flinkConfiguration, yarnConfiguration, yarnClient, yarnClusterInformationRetriever, false);
        ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployJobCluster(clusterSpecification, jobGraph, true);
        // 获取任务提交后生成的application id
        String applicationId= clusterClientProvider.getClusterClient().getClusterId().toString();
        return new Tuple2<>(applicationId, jobGraph.getJobID().toString());
    }

    public static String cancelJobWithSavepoint(String appId, String jobId) throws ClusterRetrieveException, ExecutionException, InterruptedException {
        if (checkJobNeedCancel(appId, jobId)) {
            return createYarnClusterClient(appId).cancelWithSavepoint(JobID.fromHexString(jobId), SAVEPOINT_PATH).get();
        } else {
            return "";
        }
    }

    public static String cancelJob(String appId, String jobId) throws ClusterRetrieveException, ExecutionException, InterruptedException {
        if (checkJobNeedCancel(appId, jobId)) {
            return createYarnClusterClient(appId).cancel(JobID.fromHexString(jobId)).get().toString();
        } else {
            return "";
        }
    }

    public static JobStatus getJobStatus(String appId, String jobId) throws ClusterRetrieveException, ExecutionException, InterruptedException {
        JobStatus jobStatus;
        try {
            jobStatus = createYarnClusterClient(appId).getJobStatus(JobID.fromHexString(jobId)).get();
        } catch (Exception e) {
            System.out.println("can't get job status from yarn, change job status to canceled.");
            jobStatus = JobStatus.CANCELED;
        }
        return jobStatus;
    }

    public static boolean checkJobNeedCancel(String appId, String jobId) throws InterruptedException, ExecutionException, ClusterRetrieveException {
        JobStatus jobStatus = getJobStatus(appId, jobId);
        if (jobStatus.isGloballyTerminalState()) {
            System.out.println("job status is: " + jobStatus.toString() + ", don't need cancel, skip.");
            return false;
        } else {
            return true;
        }
    }

    private static ClusterClient<ApplicationId> createYarnClusterClient(String appId) throws ClusterRetrieveException {
        Configuration flinkConfiguration = GlobalConfiguration.loadConfiguration(FLINK_CONFIG_DIR);
        flinkConfiguration.set(YarnConfigOptions.APPLICATION_ID, appId);
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.set("fs.defaultFS", HDFS_SERVER);
        yarnConfiguration.set("yarn.resourcemanager.hostname", YARN_HOST_NAME);
        YarnClient yarnClient = YarnClient.createYarnClient();
        YarnClientYarnClusterInformationRetriever yarnRetriever = YarnClientYarnClusterInformationRetriever.create(yarnClient);
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        ApplicationId applicationId = clusterClientFactory.getClusterId(flinkConfiguration);
        return new YarnClusterDescriptor(flinkConfiguration, yarnConfiguration, yarnClient, yarnRetriever, false)
                .retrieve(applicationId).getClusterClient();
    }


    public static void test(String jarFile, String fullClassName) throws ClusterDeploymentException, ProgramInvocationException, InterruptedException, ExecutionException, ClusterRetrieveException {
        Tuple2<String, String> result = deployWithPreJob(jarFile, fullClassName);
        System.out.println("submit job (pre job mode) succeed! application id: " + result.f0 + ", job id: " + result.f1);
        System.out.println("--------------------------------------------------------------------");
        String jobStatus = getJobStatus(result.f0, result.f1).toString();
        System.out.println("application id: " + result.f0 + ", status: " + jobStatus);
        System.out.println("--------------------------------------------------------------------");
        Thread.sleep(10 * 1000);
        if (ENABLE_SAVEPOINT) {
            String savepoint = cancelJobWithSavepoint(result.f0, result.f1);
            if (StringUtils.isNotBlank(savepoint)) {
                System.out.println("cancel with savepoint succeed! savepoint file path: " + savepoint);
            }
        } else {
            String ack = cancelJob(result.f0, result.f1);
            if (StringUtils.isNotBlank(ack)) {
                System.out.println("cancel succeed!");
            }
        }
    }

    public static void main(String[] args) throws ClusterDeploymentException, ProgramInvocationException, InterruptedException, ExecutionException, ClusterRetrieveException {
        String jarFile = "/Users/sunsongyang/IdeaProjects/bigdata/flink/target/flink-1.0-SNAPSHOT.jar";
        String fullClassName = "com.myclass.demo.stream.DataStreamWordCount";
        test(jarFile, fullClassName);
    }

}
