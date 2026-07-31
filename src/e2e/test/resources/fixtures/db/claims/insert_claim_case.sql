INSERT INTO claims.claim_case (
  id,
  claim_id,
  case_id,
  unique_case_id,
  outcome_code,
  stage_reached_code,
  created_by_user_id,
  created_on,
  updated_by_user_id,
  updated_on
) VALUES (
  ?::uuid,
  ?::uuid,
  ?,
  ?,
  ?,
  ?,
  ?,
  now(),
  ?,
  now()
);
