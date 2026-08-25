package uk.gov.justice.laa.payments.amend.viewmodels;

import org.thymeleaf.expression.Messages;

public abstract class ThymeleafString {

  public abstract String resolve(Messages messages);
}
