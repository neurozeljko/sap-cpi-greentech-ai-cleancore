# Enterprise Integration Architecture: AI-Driven Green Energy Carbon Accounting & FI/CO Clean Core Migration

[![SAP Integration Suite](https://img.shields.io/badge/SAP%20Integration%20Suite-Cloud%20Integration%20%7C%20APIM-0a85ea.svg?style=for-the-badge&logo=sap)](https://www.sap.com)
[![SAP S/4HANA](https://img.shields.io/badge/SAP%20S%2F4HANA-OData%20V4%20Journal%20Entry%20Clean%20Core-E35205.svg?style=for-the-badge&logo=sap)](https://www.sap.com)
[![SAP AI Core](https://img.shields.io/badge/SAP%20AI%20Core-In--Flight%20Anomaly%20Inference-2BB673.svg?style=for-the-badge)](https://www.sap.com)
[![SAP AIF](https://img.shields.io/badge/Business%20Monitoring-SAP%20AIF%20Fiori%20Dashboard-blueviolet.svg?style=for-the-badge)](https://www.sap.com)
[![SAP Build](https://img.shields.io/badge/Workflow-SAP%20Build%20Process%20Automation-orange.svg?style=for-the-badge)](https://www.sap.com)
[![DevOps](https://img.shields.io/badge/Migration-Strangler%20Fig%20Canary%20Pattern-critical.svg?style=for-the-badge)](https://en.wikipedia.org/wiki/Strangler_fig_pattern)

---

## Executive Summary

This production-grade enterprise integration blueprint establishes an autonomous, event-driven integration mesh for high-frequency smart meter telemetry across offshore wind farms and distributed renewable utility networks. The system provisions end-to-end automation for corporate sustainability reporting (ESG), voluntary carbon credit generation, and immediate financial posting into **SAP S/4HANA Cloud**.

By eliminating obsolete batch integrations (periodic CSV uploads into SAP PI/PO calling synchronous RFC functions like `BAPI_ACC_DOCUMENT_POST`), this architecture enforces the **SAP Clean Core Paradigm** via direct consumption of native RESTful OData V4 services (`API_JOURNALENTRY_SRV`). To safeguard corporate compliance and financial ledgers against greenwashing penalties, all in-flight telemetry undergoes real-time sub-100ms machine learning inference via **SAP AI Core** to detect abnormal power production. High-risk payloads trigger autonomous audit workflows in **SAP Build Process Automation**, while ERP accounting faults are automatically routed to the **SAP Application Interface Framework (AIF)**, empowering finance controllers to monitor and reprocess postings directly through SAP Fiori without developer intervention.

---

## Architecture Overview

### High-Level Topology

```text
[Smart Grid IoT Gateways (Wind Farms & Solar Panels)]
       │
       │ HTTPS POST /v1/carbon-offsets (JSON Telemetry)
       ▼
[SAP API Management (Edge Gateway)] 
  └── Strangler Fig Canary Routing Rule (DevOps Governance)
       ├── [80% Legacy Traffic] ────────► Legacy SAP PI/PO (Graceful Deprecation Path)
       └── [20% Canary Traffic] ────────► SAP Cloud Integration (New Clean Core Pipeline)
                                               │
 ┌─────────────────────────────────────────────┴─────────────────────────────────────────────┐
 │ SAP Cloud Integration (CPI) - Runtime Execution Engine                                    │
 │                                                                                           │
 │ 1. Inbound Ingestion & Validation (Groovy Script: validateIoT.groovy)                     │
 │      ├── Schema assertion & metric normalization (kWh to CO2 offset conversion)          │
 │      └── Exchange Property extraction (Telemetry context, Device ID, Farm ID)             │
 │                                                                                           │
 │ 2. In-Flight AI Anomaly Detection (Request-Reply via HTTP)                                │
 │      ├── Endpoint: SAP AI Core / ML Inference Model (wind-generation-anomaly-detector)    │
 │      └── Evaluates generation against meteorological telemetry (Wind Speed, Rotor RPM)   │
 │                                                                                           │
 │ 3. Content-Based Routing & Automated Remediation                                          │
 │      ├── Condition: ${property.AI_AnomalyScore} <= '0.70' (Legitimate Generation)         │
 │      │     └── Proceed to Clean Core S/4HANA Financial Posting Pipeline                  │
 │      │                                                                                    │
 │      └── Default Route: Score > '0.70' (Greenwashing / Fraudulent Surges Detected)        │
 │            └── HTTP POST ──► SAP Build Process Automation (Escalation / Human Review)     │
 │                                                                                           │
 │ 4. Clean Core Payload Orchestration (Groovy Script: buildJournalEntry.groovy)            │
 │      └── Deep-insert construction: Debit COGS Expense (S) / Credit Carbon Asset (H)       │
 │                                                                                           │
 │ 5. Transactional Execution via SAP Cloud Connector                                        │
 │      └── OData V4 REST Engine: /sap/opu/odata4/sap/api_journalentry/srvd_a2x/...          │
 │                                                                                           │
 │ 6. Exception Subprocess: Business-Empowered Error Delegation                              │
 │      └── Error Start Event intercepts S/4HANA fault (Period Closed / Cost Center Locked)  │
 │      └── Groovy Engine formats standard SAP AIF Envelope (formatAIFError.groovy)         │
 │      └── Emits directly to SAP S/4HANA AIF Persistence layer via HTTP                     │
 └─────────────────────────────────────────────┬─────────────────────────────────────────────┘
                                               │
               ┌───────────────────────────────┴───────────────────────────────┐
               ▼                                                               ▼
[SAP S/4HANA Cloud (Clean Core ERP)]                            [SAP AIF Fiori Dashboard]
 ├── Native OData V4 Service: API_JOURNALENTRY_SRV               └── Financial Controllers resolve
 ├── Zero Custom ABAP Modifications & Zero Z-Tables                  ledger discrepancies in Fiori
 └── Full Audit Compliance via Principal Propagation                 with One-Click Reprocess
```

---

## Technical Specifications

| Component | Technical Implementation | Architectural Justification |
| :--- | :--- | :--- |
| **Edge API Gateway** | SAP API Management (APIM Reverse Proxy) | Enforces perimeter security (JSON Threat Protection, Spike Arrest) while orchestrating weighted traffic shifts (Strangler Fig Canary pattern) to eliminate cutover downtime. |
| **Telemetry Ingestion** | SAP CPI HTTPS Inbound Endpoint | Exposes high-throughput `/v1/carbon-offsets` endpoint supporting TLS 1.3, token authorization, and sub-second payload parsing. |
| **In-Flight AI Scoring** | Request-Reply to SAP AI Core REST Endpoint | Microsecond ML inference scoring incoming kWh telemetry against concurrent weather station metrics; blocks corrupted and fraudulent data pre-ledger write. |
| **Fraud Mitigation** | SAP Build Process Automation Workflow | Diverts high-risk generation records ($\text{Score} > 0.70$) into an automated auditing sequence with contextual smart-meter telemetry. |
| **Clean Core Ledger API** | SAP S/4HANA OData V4 Client | Decommissions legacy `BAPI_ACC_DOCUMENT_POST` and custom RFCs in favor of public, atomic OData V4 entities (`API_JOURNALENTRY_SRV`). |
| **Transactional Accounting** | Deep-Insert Nested JSON Structure | Generates balanced multi-line general ledger transactions (Debit: Environmental Expense / Credit: Carbon Asset Clearing). |
| **Autonomous Business Ops** | SAP Application Interface Framework (AIF) | Directs functional posting failures to standard Fiori tiles, allowing business accounting teams to correct and reprocess errors without IT intervention. |
| **Security & Identity** | OAuth2 SAML Bearer / Principal Propagation | Maps physical meter and edge credentials into ERP audit fields (`SY-UNAME`), ensuring full compliance with international auditing standards. |
| **Fault Resilience** | CPI Exception Subprocess Engine | Traps backend network dropouts or functional rejections, transforming technical HTTP 4xx/5xx errors into actionable AIF business records. |

---

## Clean Core Paradigm vs. Legacy Anti-Patterns

| Architectural Domain | Legacy Architecture (Anti-Pattern - 7/10) | Clean Core Modernization (This Blueprint - 11/10) |
| :--- | :--- | :--- |
| **ERP Interface Standard** | Synchronous RFC executing custom ABAP wrappers around `BAPI_ACC_DOCUMENT_POST`. | Native OData V4 RESTful service (`API_JOURNALENTRY_SRV`) fully decoupled via SAP Cloud Connector. |
| **Data Integrity & Fraud** | Blind batch posting: Defective IoT sensors log inflated generation, creating regulatory tax exposure. | In-Flight AI Scoring via SAP AI Core; values exceeding $0.70$ threshold divert to SAP Build workflows. |
| **Error Handling Model** | Failures are trapped inside technical CPI/PI message monitors, invisible to financial stakeholders. | Business errors are wrapped into standard SAP AIF payloads and surfaced in SAP Fiori for one-click re-execution. |
| **Migration Risk Model** | "Big Bang" cutover over a weekend, risking complete stoppage of carbon credit settlements. | Strangler Fig Canary Release via APIM: 10% ──► 25% ──► 50% ──► 100% traffic migration with zero operational downtime. |
| **Upgrade Extensibility** | Requires maintaining bespoke database Z-tables and ABAP validation routines in ERP. | 100% Clean Core: Zero ABAP modifications, zero custom Z-tables, enabling automated SAP S/4HANA release cycles. |

---

## Message Mapping Details (IoT Telemetry to OData V4 Journal Entry)

| Source Inbound JSON Field | Target S/4HANA OData V4 Node | Target Field Name | Business Semantic & Transformation Rule |
| :--- | :--- | :--- | :--- |
| `windFarmId` | `JournalEntry` | `DocumentHeaderText` | Header reference: Concatenated as `"ESG Carbon Credit - " + farmId` (max 25 chars). |
| Dynamic Runtime Context | `JournalEntry` | `CompanyCode` | Fixed company code assignment: `"1010"`. |
| Dynamic Runtime Date | `JournalEntry` | `DocumentDate` | Current UTC processing date in `YYYY-MM-DD` format. |
| Dynamic Runtime Date | `JournalEntry` | `PostingDate` | Current UTC accounting period date in `YYYY-MM-DD` format. |
| `"SA"` | `JournalEntry` | `AccountingDocumentType` | Standard G/L account document type for autonomous adjustment vouchers. |
| `"0000000001"` | `to_Item` (Item 1) | `ReferenceDocumentItem` | First accounting line identifier (Debit leg). |
| `"0065001000"` | `to_Item` (Item 1) | `GLAccount` | Environmental Expense & COGS general ledger account. |
| `kwhProduced` (calculated) | `to_Item` (Item 1) | `AmountInTransactionCurrency` | Total offset value in EUR: $\text{CO}_2\text{ Tons} \times 65.0\text{ EUR}$ (positive debit amount). |
| `"EUR"` | `to_Item` (Item 1) | `TransactionCurrency` | Transaction currency indicator. |
| `"S"` | `to_Item` (Item 1) | `DebitCreditCode` | Standard SAP debit indicator. |
| `"CC_GREEN_01"` | `to_Item` (Item 1) | `CostCenter` | Target renewable energy sustainability cost center. |
| `deviceId` + `kwhProduced` | `to_Item` (Item 1) | `DocumentItemText` | Narrative reference: `"Offset " + co2Tons + "t CO2 - Dev " + deviceId`. |
| `"0000000002"` | `to_Item` (Item 2) | `ReferenceDocumentItem` | Second accounting line identifier (Credit leg). |
| `"0021100000"` | `to_Item` (Item 2) | `GLAccount` | Carbon Offset Asset Clearing general ledger account. |
| `kwhProduced` (calculated) | `to_Item` (Item 2) | `AmountInTransactionCurrency` | Balanced offset credit value in EUR (negative credit amount). |
| `"H"` | `to_Item` (Item 2) | `DebitCreditCode` | Standard SAP credit indicator. |

---

## Machine Learning Scoring & Business Decision Logic

To protect corporate balance sheets against greenwashing, energy generation metrics ($kWh$) are reconciled against concurrent weather conditions (wind speed, solar irradiance) in real time:

$$\text{CO}_2\text{ Offset (Tons)} = \left(\frac{\text{kWh Produced}}{1000}\right) \times 0.85$$

$$\text{Financial Valuation (EUR)} = \text{CO}_2\text{ Offset} \times \text{Credit Rate (€65.00/Ton)}$$

$$\text{Execution Routing} = \begin{cases} \text{Proceed to S/4HANA OData V4 Posting}, & \text{if } \text{Anomaly Score} \le 0.70 \\ \text{Trigger SAP Build Process Automation Escalation}, & \text{if } \text{Anomaly Score} > 0.70 \end{cases}$$

```groovy
// Extract from evaluateAI.groovy: Dynamic Content Routing
double anomalyScore = (aiResponse.anomaly_score ?: 0.0) as double
message.setProperty("AI_AnomalyScore", anomalyScore)

if (anomalyScore > 0.70) {
    message.setProperty("FraudAlertTriggered", true)
    message.setHeader("EscalationChannel", "SAP_BUILD_BPA")
}
```

---

## Autonomous Business Error Handling via SAP AIF

When financial validation fails at the ERP boundary (such as `GL_ACCOUNT_BLOCKED` or `PERIOD_CLOSED`), the **Exception Subprocess** intercepts the fault without dropping the transaction:

1. **Error Interception:** Catches the backend exception (`CamelExceptionCaught`) within CPI.
2. **Context Enrichment:** Generates a standardized AIF JSON payload containing:
   * Interface Namespace: `/ESG_FIN/`
   * Interface Name: `CARBON_JE_INB`
   * Impacted Monetary Value (€) & Originating Device ID.
   * Suggested Remediation Action (e.g., *"Open period in OB52"*).
3. **Fiori Delivery:** Injects the record directly into SAP S/4HANA AIF engine, rendering an actionable alert on the Financial Controller's Fiori launchpad. The accountant resolves the issue in ERP and clicks **Reprocess**, executing the original payload with zero IT developer intervention.

```json
{
  "AIF_Header": {
    "Namespace": "/ESG_FIN/",
    "InterfaceName": "CARBON_JE_INB",
    "InterfaceVersion": "1",
    "MessageGuid": "7f1c9c4a-8921-4d32-b7e1-8f5bc39e2101",
    "SourceDevice": "TURBINE-042",
    "PlantFacility": "WF-NORTH-SEA",
    "MonetaryImpactEur": 248.63
  },
  "AIF_ErrorContext": {
    "Category": "FINANCIAL_PERIOD_CLOSED",
    "RawSystemMessage": "Posting period 11/2026 is closed for Company Code 1010",
    "RemediationAction": "Open Financial Period in OB52 or adjust Cost Center status",
    "Reprocessable": true
  }
}
```

---

## Zero-Downtime Strangler Fig Migration Strategy

```text
Incoming IoT Requests ──► [ SAP API Management ]
                               │
            ┌──────────────────┴──────────────────┐
     (Weight: 80%)                         (Weight: 20%)
            ▼                                     ▼
 [ Legacy SAP PI/PO ]                   [ SAP Cloud Integration ]
  ├── Batch CSV Ingestion                ├── Real-time JSON Event-Driven
  ├── Custom ABAP Z-Tables               ├── In-Flight AI Core Inference
  └── BAPI_ACC_DOCUMENT_POST             └── Native S/4HANA OData V4 Clean Core
```

By leveraging policy-driven weighted routing in SAP API Management, the enterprise safely migrates traffic in canary increments:
* **Canary Phase 1:** 10% of smart meters routed to SAP CPI to benchmark latency and validate AI inference models.
* **Canary Phase 2:** 50% shift following validation of reconciliation ledgers and automated AIF error resolution.
* **Final Cutover:** 100% traffic shifted to SAP Integration Suite; legacy SAP PI/PO pipelines decommissioned with zero business downtime.

---

## Repository Structure

```text
sap-cpi-greentech-ai-cleancore/
|-- .github/workflows/
|   `-- cpi-pipeline.yml                         # Automated CI/CD deployment pipeline
|-- apim/
|   |-- policies/
|   |   |-- StranglerFigRouting.xml             # APIM Canary traffic policy
|   |   `-- ThreatProtection.xml                # Perimeter JSON threat validation
|   `-- specs/
|       `-- carbon-offsets-v1.openapi.yaml      # OpenAPI 3.0 external gateway contract
|-- cpi/
|   |-- config/
|   |   `-- parameters.prop                     # Externalized integration parameters
|   |-- iflow-export/
|   |   `-- IoT_CarbonCredit_to_FI_Journal.zip  # Complete binary SAP CPI iFlow package
|   `-- scripts/
|       |-- validateIoT.groovy                  # Telemetry normalization & metric parsing
|       |-- evaluateAI.groovy                   # ML anomaly inference scoring engine
|       |-- buildJournalEntry.groovy            # Clean Core OData V4 deep payload generator
|       `-- formatAIFError.groovy               # SAP AIF Fiori business monitoring adapter
|-- docs/
|   `-- adr/
|       |-- ADR-001-strangler-fig-canary-migration.md
|       |-- ADR-002-in-flight-ai-fraud-remediation.md
|       `-- ADR-003-aif-business-exception-delegation.md
`-- tests/
    `-- GreenTech_ESG_Inference_Suite.postman_collection.json
```

---

## Author

**Željko Popov**  
Integration Architect  
SAP Integration Suite Specialist | Cloud Architecture & Clean Core
