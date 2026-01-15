INSERT INTO claims.calculated_fee_detail (
    id,
    claim_summary_fee_id,
    claim_id,
    created_by_user_id,
    created_on
) VALUES (
  '{{CALCULATED_FEE_DETAIL_ID}}'::uuid,
  '{{CLAIM_SUMMARY_FEE_ID}}'::uuid,
  '{{CLAIM_ID}}'::uuid,
  '{{CREATED_BY_USER_ID}}',
  now()
);