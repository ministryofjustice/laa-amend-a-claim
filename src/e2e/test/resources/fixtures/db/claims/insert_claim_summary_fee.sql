INSERT INTO claims.claim_summary_fee (
  id,
  claim_id,
  net_profit_costs_amount,
  net_disbursement_amount,
  disbursements_vat_amount,
  travel_waiting_costs_amount,
  net_waiting_costs_amount,
  is_vat_applicable,
  created_by_user_id,
  created_on
) VALUES (
  ?::uuid,
  ?::uuid,
  750,
  400,
  80,
  0,
  0,
  true,
  ?,
  now()
);