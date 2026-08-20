INSERT INTO claims.bulk_submission (
  id,
  data,
  status,
  created_by_user_id,
  created_on,
  updated_by_user_id,
  updated_on
) VALUES (
  ?::uuid,
  '{}'::jsonb,
  'VALIDATION_SUCCEEDED',
  ?,
  now(),
  ?,
  now()
);