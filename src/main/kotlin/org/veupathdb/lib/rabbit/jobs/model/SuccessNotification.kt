package org.veupathdb.lib.rabbit.jobs.model

import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.rabbit.jobs.serialization.Json
import org.veupathdb.lib.rabbit.jobs.serialization.JsonDeserializable
import org.veupathdb.lib.rabbit.jobs.serialization.JsonKey
import org.veupathdb.lib.rabbit.jobs.serialization.JsonSerializable

/**
 * Job Success Notification
 *
 * @constructor Constructs a new SuccessNotification instance wrapping the given
 * job ID.
 *
 * @param jobID
 * Hash ID of the job that completed successfully.
 */
data class SuccessNotification(val jobID: HashID) : JsonSerializable {
  override fun toJson() =
    Json.new<ObjectNode> {
      put(JsonKey.JobID, jobID.string)
    }

  override fun toString() =
    "Success Notification: " + toJson().toPrettyString()

  companion object : JsonDeserializable<ObjectNode, SuccessNotification> {
    @JvmStatic
    override fun fromJson(json: ObjectNode): SuccessNotification {
      if (!json.has(JsonKey.JobID))
        throw IllegalStateException("Success notification has no ${JsonKey.JobID} field!")

      if (json.get(JsonKey.JobID).isNull)
        throw IllegalStateException("Success notification has a null ${JsonKey.JobID} field!")

      if (!json.get(JsonKey.JobID).isTextual)
        throw IllegalStateException("Success notification has a non-textual ${JsonKey.JobID} field!")

      return SuccessNotification(HashID(json.get(JsonKey.JobID).textValue()))
    }
  }
}
