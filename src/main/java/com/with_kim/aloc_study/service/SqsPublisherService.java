package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.request.AnalysisRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;


@Service
public class SqsPublisherService {

    private static final Logger log = LoggerFactory.getLogger(SqsPublisherService.class);

    private final SqsClient sqsClient;
    private final JsonMapper jsonMapper;
    private final String queueUrl;

    public SqsPublisherService(
            SqsClient sqsClient,
            JsonMapper jsonMapper,
            @Value("${aws.sqs.request-queue-url}") String queueUrl
    ){
        this.sqsClient = sqsClient;
        this.jsonMapper = jsonMapper;
        this.queueUrl = queueUrl;
    }


    /**
     * 분석 요청 메시지를 발행한다.
     *
     * 실패 시 예외를 그대로 던진다 — 호출부(SubmissionService)에서
     * "DB/S3까지는 성공했는데 SQS 발행만 실패한 경우"를 어떻게 처리할지
     * (예: 재시도, 상태를 FAILED로 표시 후 배치 재처리) 결정하도록 한다.
     */

    public void publish(AnalysisRequestMessage message){
        try{
           String body = jsonMapper.writeValueAsString(message);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);

            log.info("SQS 발행 완료 : submissionId={}, messageId={}", message.submissionId(), response.messageId());
        }catch (JacksonException e){
            throw new IllegalArgumentException("분석 요청 메시지 직렬화 실패 : submissionId = " + message.submissionId(), e);
        }catch (Exception e){
            log.error("SQS 발행 실패 : submissionId=" + message.submissionId(), e);
            throw new IllegalArgumentException("SQS 발행 실패 : submissionId = " + message.submissionId(), e);
        }
    }
}
