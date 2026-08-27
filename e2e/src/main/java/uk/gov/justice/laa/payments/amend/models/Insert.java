package uk.gov.justice.laa.payments.amend.models;

import java.util.List;

public interface Insert {

  String table();

  String id();

  List<Object> parameters();
}
