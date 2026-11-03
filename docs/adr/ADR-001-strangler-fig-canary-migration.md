# ADR-001: Zero-Downtime Strangler Fig Migration Pattern
## Status: ACCEPTED (Principal Review)
## Context: Legacy SAP PI/PO handles critical monthly CSV dumps. Big-bang cutover creates unmanageable financial risk.
## Decision: Implement weighted routing in SAP APIM. Divert 20% canary traffic to CPI, maintaining 80% on legacy until full parity validation.
