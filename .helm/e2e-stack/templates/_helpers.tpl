{{/*
Expand the name of the chart.
*/}}
{{- define "e2e-stack.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "e2e-stack.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "e2e-stack.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "e2e-stack.labels" -}}
helm.sh/chart: {{ include "e2e-stack.chart" . }}
{{ include "e2e-stack.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "e2e-stack.selectorLabels" -}}
app.kubernetes.io/name: {{ include "e2e-stack.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Resource names for e2e services
*/}}
{{- define "e2e-stack.amendName" -}}
{{- printf "%s-amend-claim" .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "e2e-stack.claimsName" -}}
{{- printf "%s-claims-api" .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}
