package org.veupathdb.lib.rabbit.jobs.serialization

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature.*
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

@Suppress("NOTHING_TO_INLINE")
internal object Json {
  val mapper: ObjectMapper = ObjectMapper()
    .registerModule(JsonOrgModule())
    .registerModule(JavaTimeModule())
    .registerModule(Jdk8Module())
    .registerModule(KotlinModule.Builder()
      .enable(NullToEmptyMap)
      .enable(NullToEmptyCollection)
      .enable(SingletonSupport)
      .build())
    .registerModule(ParameterNamesModule())

  inline fun <reified T : JsonNode> new(action: T.() -> Unit): T {
    val tmp = when (T::class) {
      ObjectNode::class -> mapper.createObjectNode()
      ArrayNode::class  -> mapper.createArrayNode()
      else              -> throw UnsupportedOperationException()
    }

    action(tmp as T)

    return tmp
  }

  inline fun <reified T> from(bytes: ByteArray): T =
    mapper.readValue(bytes, T::class.java)

  inline fun ObjectNode.add(key: String, value: Int) { put(key, value) }
}