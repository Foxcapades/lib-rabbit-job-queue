package org.veupathdb.lib.rabbit.jobs.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import org.veupathdb.lib.rabbit.jobs.serialization.JsonDeserializable
import org.veupathdb.lib.rabbit.jobs.serialization.JsonKey
import org.veupathdb.lib.rabbit.jobs.serialization.JsonSerializable

/**
 * Job Error Notification
 *
 * @constructor Constructs a new ErrorNotification instance.
 *
 * @param jobID   Hash ID of the job that failed.
 * @param code    Error/response code of the job failure.
 *
 *                If the error has no associated code, set this value to `0`.
 * @param message Optional error message for the job failure.
 */
data class ErrorNotification @JvmOverloads constructor(
  val jobID:         HashID,
  val code:          Int,
  val message:       String? = null,
) : JsonSerializable {
  override fun toJson(): JsonNode =
    Json.new<ObjectNode> {
      put(JsonKey.JobID, jobID.string)
      put(JsonKey.Code, code)
      put(JsonKey.Message, message)
    }

  override fun toString() =
    "ErrorNotification: " + toJson().toPrettyString()

  companion object : JsonDeserializable<ObjectNode, ErrorNotification> {
    @JvmStatic
    override fun fromJson(json: ObjectNode): ErrorNotification {
      if (!json.has(JsonKey.JobID))
        throw IllegalStateException("Error notification has no ${JsonKey.JobID} field!")
      if (!json.has(JsonKey.Code))
        throw IllegalStateException("Error notification has no ${JsonKey.Code} field!")

      if (json.get(JsonKey.JobID).isNull)
        throw IllegalStateException("Error notification has a null ${JsonKey.JobID} field!")
      if (json.get(JsonKey.Code).isNull)
        throw IllegalStateException("Error notification has a null ${JsonKey.Code} field!")

      if (!json.get(JsonKey.JobID).isTextual)
        throw IllegalStateException("Error notification has a non-textual ${JsonKey.JobID} field!")
      if (!json.get(JsonKey.Code).isIntegralNumber)
        throw IllegalStateException("Error notification has a non-integral ${JsonKey.Code} field!")

      return ErrorNotification(
        HashID(json.get(JsonKey.JobID).textValue()),
        json.get(JsonKey.Code).intValue(),
        json.get(JsonKey.Message).textValue()
      )
    }
  }
}
