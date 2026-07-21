package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum StageReached implements FieldOption {
  INVA("INVA"),
  INVB("INVB"),
  INVC("INVC"),
  INVD("INVD"),
  INVE("INVE"),
  INVF("INVF"),
  INVG("INVG"),
  INVH("INVH"),
  INVI("INVI"),
  INVJ("INVJ"),
  INVK("INVK"),
  INVL("INVL"),
  INVM("INVM"),
  PRIA("PRIA"),
  PRIB("PRIB"),
  PRIC("PRIC"),
  PRID("PRID"),
  PRIE("PRIE"),
  PROC("PROC"),
  PROD("PROD"),
  PROE("PROE"),
  PROF("PROF"),
  PROH("PROH"),
  PROI("PROI"),
  PROJ("PROJ"),
  PROK("PROK"),
  PROL("PROL"),
  PROP("PROP"),
  PROT("PROT"),
  PROU("PROU"),
  PROV("PROV"),
  PROW("PROW"),
  APPA("APPA"),
  APPB("APPB"),
  APPC("APPC"),
  ASMS("ASMS"),
  ASPL("ASPL"),
  ASAS("ASAS"),
  YOUE("YOUE"),
  YOUF("YOUF"),
  YOUK("YOUK"),
  YOUL("YOUL"),
  YOUX("YOUX"),
  YOUY("YOUY"),
  VOID("VOID");

  private final String value;

  StageReached(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
