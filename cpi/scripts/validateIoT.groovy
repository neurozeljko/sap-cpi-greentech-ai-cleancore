import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.time.Instant

def Message processData(Message message) {
    def body = message.getBody(String)
    def jsonSlurper = new JsonSlurper()
    def input = jsonSlurper.parseText(body)

    String deviceId = input.deviceId ?: "UNKNOWN_TURBINE"
    String farmId = input.windFarmId ?: "WF-OFFSHORE-01"
    double kwhProduced = (input.kwhProduced ?: 0.0) as double
    String timestamp = input.timestamp ?: Instant.now().toString()

    double co2Tons = (kwhProduced / 1000.0) * 0.85
    double creditValueEur = co2Tons * 65.0

    message.setProperty("IoT_DeviceID", deviceId)
    message.setProperty("IoT_FarmID", farmId)
    message.setProperty("IoT_KWh", kwhProduced)
    message.setProperty("ESG_CO2_Tons", co2Tons)
    message.setProperty("ESG_CreditValue_EUR", creditValueEur)
    message.setProperty("IoT_Timestamp", timestamp)
    message.setProperty("Original_IoT_Payload", body)

    def aiInferenceRequest = [
        "model_id"    : "wind-generation-anomaly-detector-v2",
        "device_id"   : deviceId,
        "location_id" : farmId,
        "kwh_metric"  : kwhProduced,
        "timestamp"   : timestamp,
        "telemetry_context": [
            "wind_speed_mps": input.weatherContext?.windSpeed ?: 12.4,
            "rotor_rpm"     : input.weatherContext?.rotorRpm ?: 14.8
        ]
    ]

    message.setBody(JsonOutput.toJson(aiInferenceRequest))
    message.setHeader("Content-Type", "application/json")
    return message
}
