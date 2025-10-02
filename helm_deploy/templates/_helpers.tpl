{{/*
Expand the name of the chart.
*/}}
{{- define "laa-amend-a-claim.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "laa-amend-a-claim.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "laa-amend-a-claim.labels" -}}
helm.sh/chart: {{ include "laa-amend-a-claim.chart" . }}
{{ include "laa-amend-a-claim.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}


{{/*
Selector labels
*/}}
{{- define "laa-amend-a-claim.selectorLabels" -}}
app: {{ .Chart.Name }}
app.kubernetes.io/metadata.name: {{ .Release.Name }}
app.kubernetes.io/name: {{ include "laa-amend-a-claim.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}