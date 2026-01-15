INSERT INTO claims.bulk_submission (
  id,
  data,
  status,
  created_by_user_id,
  created_on,
  updated_by_user_id,
  updated_on
) VALUES (
  '{{BULK_SUBMISSION_ID}}'::uuid,
  '{}'::jsonb,
  'VALIDATION_SUCCEEDED',
  '{{CREATED_BY_USER_ID}}',
  now(),
  '{{CREATED_BY_USER_ID}}',
  now()
);