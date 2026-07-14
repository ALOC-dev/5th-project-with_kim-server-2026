package com.with_kim.aloc_study.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class AnalysisResultSqsConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalysisResultSqsConsumer.class);

    private final SqsClient sqsClient;
    private final JsonMapper jsonMapper;
    private final AnalysisResultService analysisResultService;
    private final String resultQueueUrl;

    private boolean disabledLogPrinted = false;

    public AnalysisResultSqsConsumer(
            SqsClient sqsClient,
            JsonMapper jsonMapper,
            AnalysisResultService analysisResultService,
            @Value("${aws.sqs.result-queue-url:}") String resultQueueUrl
    ) {
        this.sqsClient = sqsClient;
        this.jsonMapper = jsonMapper;
        this.analysisResultService = analysisResultService;
        this.resultQueueUrl = resultQueueUrl;
    }

    @Scheduled(fixedDelayString = "${aws.sqs.result-poll-delay-ms:5000}")
    public void pollResultQueue() {
        if (resultQueueUrl == null || resultQueueUrl.isBlank()) {
            if (!disabledLogPrinted) {
                log.info("분석 결과 SQS consumer 비활성화: aws.sqs.result-queue-url 미설정");
                disabledLogPrinted = true;
            }
            return;
        }

        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(resultQueueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(10)
                    .visibilityTimeout(60)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();
            for (Message message : messages) {
                processMessage(message);
            }
        } catch (Exception e) {
            log.error("분석 결과 SQS polling 실패", e);
        }
    }

    private void processMessage(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.body());
            String submissionId = extractSubmissionId(root);
            JsonNode analysis = extractAnalysis(root);

            analysisResultService.applyResult(submissionId, analysis);
            deleteMessage(message);

            log.info("분석 결과 SQS 메시지 처리 완료: submissionId={}, messageId={}",
                    submissionId, message.messageId());
        } catch (Exception e) {
            log.error("분석 결과 SQS 메시지 처리 실패: messageId={}", message.messageId(), e);
        }
    }

    private String extractSubmissionId(JsonNode root) {
        JsonNode submissionIdNode = root.path("submissionId");
        if (submissionIdNode.isMissingNode() || submissionIdNode.isNull()
                || submissionIdNode.asString().isBlank()) {
            throw new IllegalArgumentException("분석 결과 메시지에 submissionId가 없습니다");
        }
        return submissionIdNode.asString();
    }

    private JsonNode extractAnalysis(JsonNode root) {
        JsonNode analysis = root.path("analysis");
        if (analysis.isMissingNode() || analysis.isNull()) {
            throw new IllegalArgumentException("분석 결과 메시지에 analysis가 없습니다");
        }
        return analysis;
    }

    private void deleteMessage(Message message) {
        DeleteMessageRequest request = DeleteMessageRequest.builder()
                .queueUrl(resultQueueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(request);
    }
}
