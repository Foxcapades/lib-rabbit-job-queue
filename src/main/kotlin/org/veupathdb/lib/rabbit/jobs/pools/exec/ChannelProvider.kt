package org.veupathdb.lib.rabbit.jobs.pools.exec

import com.rabbitmq.client.Channel

internal typealias ChannelProvider = () -> Channel

