import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput
import java.time.LocalDate

def Message processData(Message message) {
    def properties = message.getProperties()
    String deviceId = properties.get("IoT_DeviceID") ?: "TURBINE-01"
    String farmId = properties.get("IoT_FarmID") ?: "WF-OFFSHORE-01"
    double creditValueEur = properties.get("ESG_CreditValue_EUR") as double
    double co2Tons = properties.get("ESG_CO2_Tons") as double
    String postingDate = LocalDate.now().toString()

    double roundedAmount = Math.round(creditValueEur * 100.0) / 100.0

    def journalEntryPayload = [
        "CompanyCode"                 : "1010",
        "DocumentDate"                : postingDate,
        "PostingDate"                 : postingDate,
        "AccountingDocumentType"      : "SA",
        "DocumentHeaderText"          : "ESG Carbon Credit - ${farmId}".take(25),
        "CreatedByUser"               : "IOT_AUTONOMOUS",
        "to_Item"                     : [
            [
                "ReferenceDocumentItem" : "0000000001",
                "GLAccount"             : "0065001000",
                "AmountInTransactionCurrency": roundedAmount,
                "TransactionCurrency"   : "EUR",
                "DebitCreditCode"       : "S",
                "CostCenter"            : "CC_GREEN_01",
                "DocumentItemText"      : "Offset ${co2Tons}t CO2 - Dev ${deviceId}".take(50)
            ],
            [
                "ReferenceDocumentItem" : "0000000002",
                "GLAccount"             : "0021100000",
                "AmountInTransactionCurrency": -roundedAmount,
                "TransactionCurrency"   : "EUR",
                "DebitCreditCode"       : "H",
                "DocumentItemText"      : "Eco Certificate Credit - ${farmId}".take(50)
            ]
        ]
    ]

    message.setBody(JsonOutput.toJson(journalEntryPayload))
    message.setHeader("Content-Type", "application/json")
    message.setHeader("Accept", "application/json")
    return message
}
