package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

  public JobCompletionNotificationListener() {
  }

  @Override
  public void afterJob(@NonNull JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("UpdatePlayerStatsJob finished for parameters {}", jobExecution.getJobParameters());
    } else {
      log.warn("UpdatePlayerStatsJob finished with status {}", jobExecution.getStatus());
    }
  }
}
