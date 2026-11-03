import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput
import java.time.Instant

def Message processData(Message message) {
    def properties = message.getProperties()
    def ex = properties.get("CamelExceptionCaught")
    
    String rawError = ex ? ex.getMessage() : "Unknown S/4HANA Posting Exception"
    String deviceId = properties.get("IoT_DeviceID") ?: "UNKNOWN"
    String farmId = properties.get("IoT_FarmID") ?: "UNKNOWN"
    double creditValueEur = (properties.get("ESG_CreditValue_EUR") ?: 0.0) as double

    String businessErrorCategory = "GENERAL_POSTING_FAULT"
    if (rawError.contains("Posting period") || rawError.contains("closed")) {
        businessErrorCategory = "FINANCIAL_PERIOD_CLOSED"
    } else if (rawError.contains("Cost center") || rawError.contains("locked")) {
        businessErrorCategory = "COST_CENTER_LOCKED"
    }

    def aifPayload = [
        "AIF_Header": [
            "Namespace"         : "/ESG_FIN/",
            "InterfaceName"     : "CARBON_JE_INB",
            "InterfaceVersion"  : "1",
            "MessageGuid"       : UUID.randomUUID().toString(),
            "Timestamp"         : Instant.now().toString(),
            "SourceDevice"      : deviceId,
            "PlantFacility"     : farmId,
            "MonetaryImpactEur" : creditValueEur
        ],
        "AIF_ErrorContext": [
            "Category"          : businessErrorCategory,
            "RawSystemMessage"  : rawError,
            "RemediationAction" : "Open Financial Period in OB52 or adjust Cost Center status",
            "Reprocessable"     : true
        ],
        "OriginalPayload"       : properties.get("Original_IoT_Payload")
    ]

    message.setBody(JsonOutput.toJson(aifPayload))
    message.setHeader("Content-Type", "application/json")
    message.setHeader("AIF_Interface_Status", "ERROR_PENDING_BUSINESS_REPROCESS")
    return message
}
