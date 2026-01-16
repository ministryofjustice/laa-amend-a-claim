INSERT INTO claims.calculated_fee_detail (
    id,
    claim_summary_fee_id,
    claim_id,
    created_by_user_id,
    created_on
) VALUES (
  ?::uuid,
  ?::uuid,
  ?::uuid,
  ?,
  now()
);