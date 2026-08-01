# Residence Lambda OCR Design

## Goal

Create a dedicated Lambda container that receives resident-registration transcript requests from `sibang-residence-request`, downloads each PDF from S3, OCRs every page with the Korean and English Tesseract models, extracts address-change years, and publishes a Spring-compatible result to `sibang-result`.

The extractor must not infer residence periods. It records only years explicitly attached to move-in or address-change events in the transcript.

## Input Contract

The Lambda is invoked by an SQS event. Each record body has this shape:

```json
{
  "messageType": "RESIDENCE_ADDRESS_EXTRACTION",
  "userId": 4,
  "source": {
    "bucket": "aloc-sibang",
    "key": "resident-registration/4/transcript.pdf"
  }
}
```

`messageType`, `userId`, `source.bucket`, and `source.key` are required.

## OCR Pipeline

1. Download the PDF with `s3:GetObject`.
2. Open it with PyMuPDF and reject an empty, invalid, or page-less document.
3. Render every page at 300 DPI in RGB.
4. Run Tesseract on every rendered page with `OCR_LANGUAGE`, defaulting to `kor+eng`.
5. Join page OCR output in document order and normalize Unicode, whitespace, OCR punctuation, and common date separators.
6. Extract rows containing a Korean address and an explicit event year.

The pipeline always uses image OCR. It does not use or trust an embedded PDF text layer.

## Address Aggregation

- Accept years only from move-in and address-change records, including labels such as `전입`, `세대주변경`, `주소변경`, `상세주소변경`, `동번변경`, and `도로명주소`.
- Do not derive an end year from the next row and do not create an inferred year range.
- Normalize an address only for grouping. Preserve the first readable address as `rawAddress`.
- Merge repeated occurrences of the same normalized address and deduplicate years while preserving document order.
- Classify an address containing a road-name suffix such as `로` or `길` followed by a building number as `roadAddress`.
- Classify an address containing `동`, `읍`, `면`, `리`, or `가` followed by a lot number as `jibunAddress`.
- Mark only the last valid address-change record as `current: true`.
- Do not return names, resident-registration numbers, document confirmation numbers, or other personal fields.

## Result Contract

Successful extraction:

```json
{
  "messageType": "RESIDENCE_ADDRESS_EXTRACTION",
  "userId": 4,
  "status": "COMPLETED",
  "addresses": [
    {
      "rawAddress": "경기도 하남시 덕풍동 690 케이씨씨아파트 103-2001",
      "roadAddress": null,
      "jibunAddress": "경기도 하남시 덕풍동 690 케이씨씨아파트 103-2001",
      "current": true,
      "residenceYears": ["2006"]
    }
  ]
}
```

Readable PDF but no valid address events:

```json
{
  "messageType": "RESIDENCE_ADDRESS_EXTRACTION",
  "userId": 4,
  "status": "FAILED",
  "error": "주민등록초본에서 주소와 변동 연도를 찾지 못했습니다.",
  "addresses": []
}
```

## Error Handling

- Invalid request bodies are returned through SQS partial batch failure so the request queue can retry and eventually route them to its DLQ.
- S3 download failures, OCR runtime failures, and result-SQS publish failures are infrastructure failures and are returned through `batchItemFailures`.
- A successfully OCRed document with no valid address events produces a `FAILED` result message. Publishing that result completes the request and prevents pointless retries.
- Logs include message ID, user ID, S3 location, byte count, page count, OCR character count, and address count. Logs do not include OCR text or extracted personal data.

## Runtime Configuration

- `RESULT_QUEUE_URL`: required result queue URL.
- `OCR_LANGUAGE`: optional Tesseract language expression, default `kor+eng`.
- `OCR_DPI`: optional render resolution, default `300`, constrained to a safe range.

The Lambda execution role needs `s3:GetObject` for `aloc-sibang/resident-registration/*` and `sqs:SendMessage` for `sibang-result`.

## Tests

- OCRs every PDF page and uses `kor+eng` by default.
- Rejects invalid or empty PDFs.
- Extracts a year from an address-change row and ignores unrelated dates.
- Joins a date/event fragment with the corresponding OCR-split address row.
- Merges non-contiguous occurrences of the same address without inferring ranges.
- Marks the final valid address as current.
- Publishes the exact Spring result contract.
- Sends document-content failures as `FAILED` results.
- Returns infrastructure failures through SQS partial batch failure.
