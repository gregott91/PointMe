package com.ottsandbox.pointme.logic.factories

import com.ottsandbox.pointme.models.*
import com.ottsandbox.pointme.utility.DEFAULT
import javax.inject.Inject

class ChannelFactory @Inject constructor() {
    private val channelMappings: Map<ChannelType, Channel> = mapOf(
        ChannelType.DEFAULT to DEFAULT
    )

    fun getChannel(type: ChannelType): Channel {
        return channelMappings[type] ?: error("Unable to get channel from type $type")
    }
}