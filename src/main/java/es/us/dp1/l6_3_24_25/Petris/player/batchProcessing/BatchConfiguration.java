package es.us.dp1.l6_3_24_25.Petris.player.batchProcessing;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public Job updatePlayerStatsJob(@NonNull JobRepository jobRepository,
                                    @NonNull Step processStatsStep,
                                    @NonNull JobCompletionNotificationListener listener) {
        return new JobBuilder("UpdatePlayerStatsJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .start(processStatsStep)
            .build();
    }

    @Bean
    public Step processStatsStep(@NonNull JobRepository jobRepository,
                                 @NonNull PlatformTransactionManager transactionManager,
                                 @NonNull ItemReader<MatchStatPayload> statDataReader,
                                 @NonNull ItemProcessor<MatchStatPayload, PlayerStatsUpdate> statProcessor,
                                 @NonNull ItemWriter<PlayerStatsUpdate> playerStatsWriter) {
        return new StepBuilder("processStatsStep", jobRepository)
            .<MatchStatPayload, PlayerStatsUpdate>chunk(1, transactionManager)
            .reader(statDataReader)
            .processor(statProcessor)
            .writer(playerStatsWriter)
            .build();
    }

    @Bean
    @StepScope
    public StatDataReader statDataReader(TemporaryMatchStatStore statStore,
                                         @Value("#{jobParameters['matchId']}") Long matchId,
                                         @Value("#{jobParameters['playerId']}") Long playerId) {
        List<MatchStatPayload> payloads = statStore.consume(matchId, playerId);
        return new StatDataReader(payloads);
    }

    @Bean
    @StepScope
    public StatProcessor statProcessor() {
        return new StatProcessor();
    }

    @Bean
    @StepScope
    public PlayerStatsWriter playerStatsWriter() {
        return new PlayerStatsWriter();
    }
}
