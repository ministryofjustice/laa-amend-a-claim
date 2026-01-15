INSERT INTO claims.claim_summary_fee (
  id,
  claim_id,
  created_by_user_id,
  created_on
) VALUES (
  '{{CLAIM_SUMMARY_FEE_ID}}'::uuid,
  '{{CLAIM_ID}}'::uuid,
  '{{CREATED_BY_USER_ID}}',
  now()
);