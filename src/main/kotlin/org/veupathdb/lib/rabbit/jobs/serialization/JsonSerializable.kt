package org.veupathdb.lib.rabbit.jobs.serialization

import com.fasterxml.jackson.databind.JsonNode

/**
 * JSON Serializable Type
 *
 * Represents a type that may be serialized to JSON.
 */
interface JsonSerializable {

  /**
   * Converts the parent instance into a JSON node.
   *
   * The return value of this method should be idempotent.
   *
   * @return A JSON representation of the parent instance.
   */
  fun toJson(): JsonNode
}