# ADR-002: Real-Time In-Flight AI Scoring for Carbon Credits
## Status: ACCEPTED (Principal Review)
## Context: Malfunctioning IoT meters report inflated generation, causing greenwashing audit breaches.
## Decision: Sub-100ms REST call to SAP AI Core. Anomaly score > 0.70 routes payload to SAP Build Process Automation for compliance sign-off.
